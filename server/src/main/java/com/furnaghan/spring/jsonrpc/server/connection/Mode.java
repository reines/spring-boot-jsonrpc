package com.furnaghan.spring.jsonrpc.server.connection;

import java.util.function.Supplier;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;

public enum Mode {
	@Deprecated OIO( OioServerSocketChannel.class, OioEventLoopGroup::new ),
	NIO( NioServerSocketChannel.class, NioEventLoopGroup::new ),
	EPOLL( EpollServerSocketChannel.class, EpollEventLoopGroup::new );

	private final Class<? extends ServerChannel> channelClass;
	private final Supplier<EventLoopGroup> eventLoopGroupFactory;

	Mode( final Class<? extends ServerChannel> channelClass,
			final Supplier<EventLoopGroup> eventLoopGroupFactory ) {
		this.channelClass = channelClass;
		this.eventLoopGroupFactory = eventLoopGroupFactory;
	}

	public Class<? extends ServerChannel> getChannelClass() {
		return channelClass;
	}

	public EventLoopGroup createEventLoopGroup() {
		return eventLoopGroupFactory.get();
	}
}
