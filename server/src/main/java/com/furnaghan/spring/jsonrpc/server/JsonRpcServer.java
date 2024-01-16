package com.furnaghan.spring.jsonrpc.server;

import static java.util.Objects.requireNonNull;

import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.Epoll;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringEncoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.furnaghan.spring.jsonrpc.api.RpcController;
import com.furnaghan.spring.jsonrpc.server.connection.Mode;
import com.furnaghan.spring.jsonrpc.server.error.MethodNotFoundException;
import com.furnaghan.spring.jsonrpc.server.json.codec.JsonRpcRequestDecoder;
import com.furnaghan.spring.jsonrpc.server.json.codec.JsonRpcResponseEncoder;
import com.furnaghan.spring.jsonrpc.server.json.protocol.JsonRpcError;
import com.furnaghan.spring.jsonrpc.server.json.protocol.JsonRpcRequest;
import com.furnaghan.spring.jsonrpc.server.json.protocol.JsonRpcResponse;
import com.furnaghan.spring.jsonrpc.server.rpc.ApiHandler;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Component
public class JsonRpcServer {

	private static final Logger LOGGER = LoggerFactory.getLogger( JsonRpcServer.class );

	private static final int MAX_FRAME_SIZE = 1024 * 1024 * 10;

	private final int port;
	private final ServerBootstrap bootstrap;
	private final ApplicationContext context;
	private final ExecutorService executor = Executors.newCachedThreadPool();
	private final Map<String, ApiHandler> handlers = new ConcurrentHashMap<>();
	private Channel channel;

	// @formatter:off
	public JsonRpcServer(
			@Value("${jsonrpc.server.port:50000}") final int port,
			@Value("${jsonrpc.server.mode:NIO}") final Mode mode,
			final ApplicationContext context
	) {
	// @formatter:on
		this.port = port;
		this.context = context;

		if ( mode == Mode.EPOLL && !Epoll.isAvailable() ) {
			throw new IllegalArgumentException( "Epoll is not available" );
		}

		final ObjectMapper json = context.getBean( ObjectMapper.class );

		bootstrap = new ServerBootstrap().childHandler( new ChannelInitializer<>() {
					@Override
					protected void initChannel( @NonNull final Channel channel ) {
						final ChannelPipeline pipeline = channel.pipeline();

						// Response encoding
						pipeline.addLast( "stringEncoder", new StringEncoder( StandardCharsets.UTF_8 ) );
						pipeline.addLast( "encoder", new JsonRpcResponseEncoder( json ) );

						// Request decoding
						pipeline.addLast( "linesplitter", new LineBasedFrameDecoder( MAX_FRAME_SIZE ) );
						pipeline.addLast( "decoder", new JsonRpcRequestDecoder( json ) );

						// API request handling
						pipeline.addLast( "handler", new SimpleChannelInboundHandler<JsonRpcRequest>() {
							@Override
							public void channelRead0( final ChannelHandlerContext ctx,
									final JsonRpcRequest request ) {
								handle( request, ctx::writeAndFlush );
							}

							@Override
							public void exceptionCaught( final ChannelHandlerContext ctx,
									final Throwable cause ) {
								if ( !( cause instanceof ClosedChannelException ) ) {
									LOGGER.warn( "Unexpected exception.", cause );
								}

								ctx.close();
							}
						} );
					}
				} )
				.group( mode.createEventLoopGroup(), mode.createEventLoopGroup() )
				.channel( mode.getChannelClass() )
				.childOption( ChannelOption.SO_KEEPALIVE, true )
				.childOption( ChannelOption.TCP_NODELAY, true );
	}

	private void handle( final JsonRpcRequest request,
			final Consumer<JsonRpcResponse<?>> consumer ) {
		executor.execute( () -> {
			try {
				final String namespace = request.extractNamespace().orElse( "" );
				final String method = request.extractMethod();

				final ApiHandler handler = handlers.get( namespace );
				if ( handler == null ) {
					throw new MethodNotFoundException( namespace, method );
				}

				final Object result = handler.invoke( method, request.params() );
				consumer.accept( new JsonRpcResponse<>( result, null ) );
			} catch ( final Exception e ) {
				consumer.accept( new JsonRpcResponse<>( null, JsonRpcError.from( e ) ) );
			}
		} );
	}

	@PostConstruct
	public void doStart() throws InterruptedException {
		createApiHandlers().forEach( handler -> {
			handlers.put( handler.getNamespace(), handler );
			LOGGER.info( "Registered handler: {}", handler );
		} );

		// Bind to the actual port for this new channel
		channel = bootstrap.bind( port ).sync().channel();

		LOGGER.info( "RPC Server listening on port {}", getPort() );
	}

	public int getPort() {
		return Optional.ofNullable( channel )
				.map( Channel::localAddress )
				.map( InetSocketAddress.class::cast )
				.map( InetSocketAddress::getPort )
				.orElse( port );
	}

	@PreDestroy
	public void doStop() {
		LOGGER.info( "RPC Server on {} preparing to shut down.", port );
		bootstrap.config().group().shutdownGracefully().awaitUninterruptibly();
		bootstrap.config().childGroup().shutdownGracefully().awaitUninterruptibly();
		executor.shutdown();
		LOGGER.info( "RPC Server has shut down." );
	}

	private Stream<ApiHandler> createApiHandlers() {
		final ObjectMapper objectMapper = context.getBean( ObjectMapper.class );
		return context.getBeansWithAnnotation( RpcController.class ).values().stream().map( api -> {
			final RpcController annotation = findAnnotation( api.getClass(), RpcController.class );
			final String namespace = requireNonNull( annotation ).namespace();
			final Class<?> apiInterface = AopUtils.getTargetClass( api );
			return new ApiHandler( namespace, apiInterface, api, objectMapper );
		} );
	}
}
