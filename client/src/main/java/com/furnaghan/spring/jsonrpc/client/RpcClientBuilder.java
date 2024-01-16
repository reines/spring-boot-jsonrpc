package com.furnaghan.spring.jsonrpc.client;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import com.furnaghan.spring.jsonrpc.client.connection.Connection;

import stormpot.Allocator;
import stormpot.BlazePool;
import stormpot.Config;
import stormpot.Expiration;
import stormpot.Pool;
import stormpot.Slot;
import stormpot.TimeExpiration;

public abstract class RpcClientBuilder<C extends RpcClient> {

	private Duration connectTimeout = Duration.ofSeconds( 2 );
	private Duration requestTimeout = Duration.ofSeconds( 2 );
	private Duration poolTimeout = Duration.ofSeconds( 2 );
	private Duration poolExpiration = Duration.ofSeconds( 55 );
	private int poolSize = 100;

	public RpcClientBuilder<C> connectTimeout( final Duration connectTimeout ) {
		this.connectTimeout = connectTimeout;
		return this;
	}

	public RpcClientBuilder<C> requestTimeout( final Duration requestTimeout ) {
		this.requestTimeout = requestTimeout;
		return this;
	}

	public RpcClientBuilder<C> poolTimeout( final Duration poolTimeout ) {
		this.poolTimeout = poolTimeout;
		return this;
	}

	public RpcClientBuilder<C> poolExpiration( final Duration poolExpiration ) {
		this.poolExpiration = poolExpiration;
		return this;
	}

	public RpcClientBuilder<C> poolSize( final int poolSize ) {
		this.poolSize = poolSize;
		return this;
	}

	protected abstract C build( final Pool<Connection> connections, final Duration poolTimeout );

	public C build( final Connection.Factory factory ) {
		final Allocator<Connection> allocator = new Allocator<Connection>() {
			@Override
			public Connection allocate( final Slot slot ) throws IOException {
				return factory.create( slot, connectTimeout, requestTimeout );
			}

			@Override
			public void deallocate( final Connection connection ) {
				// No-op, keep-alive
			}
		};

		final Expiration<Connection> expiration = new TimeExpiration<>( poolExpiration.toMillis(),
				TimeUnit.MILLISECONDS );

		final Pool<Connection> connections = new BlazePool<>(
				new Config<>().setAllocator( allocator )
						.setSize( poolSize )
						.setExpiration( expiration )
						.setBackgroundExpirationEnabled( true ) );

		return build( connections, poolTimeout );
	}
}
