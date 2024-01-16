package com.furnaghan.spring.jsonrpc.client;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.core.annotation.AnnotationUtils;

import com.furnaghan.spring.jsonrpc.api.RpcMethod;
import com.furnaghan.spring.jsonrpc.client.connection.Connection;

import stormpot.Pool;
import stormpot.Timeout;

public abstract class RpcClient {

	private static final Object[] NO_ARGS = new Object[0];

	private final Pool<Connection> connections;
	private final Timeout timeout;

	public RpcClient( final Pool<Connection> connections, final Duration timeout ) {
		this.connections = connections;
		this.timeout = new Timeout( timeout.toMillis(), TimeUnit.MILLISECONDS );
	}

	@SuppressWarnings("unchecked")
	public <T> T create( final String namespace, final Class<T> clazz ) {
		return (T) Proxy.newProxyInstance( clazz.getClassLoader(), new Class<?>[] { clazz },
				( proxy, method, args ) -> {
					final RpcMethod annotation = AnnotationUtils.findAnnotation( method,
							RpcMethod.class );
					if ( annotation == null ) {
						return method.invoke( this, args );
					}

					final String name = Optional.ofNullable( annotation.name() )
							.filter( n -> !n.isEmpty() )
							.orElseGet( method::getName );
					final Type returnType = method.getGenericReturnType();
					final Object[] params = Optional.ofNullable( args ).orElse( NO_ARGS );

					final String request = encode( namespace, name, params );
					final String response = call( request );

					return decode( response, returnType );
				} );
	}

	private String call( final String request ) throws InterruptedException, IOException {
		final Connection connection = connections.claim( timeout );
		try {
			return connection.send( request );
		} finally {
			connection.release();
		}
	}

	protected abstract String encode( final String namespace, final String name,
			final Object... params ) throws IOException;

	protected abstract Object decode( final String message, final Type returnType )
			throws IOException;
}
