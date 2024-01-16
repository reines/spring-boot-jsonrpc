package com.furnaghan.spring.jsonrpc.client.connection;

import java.io.IOException;
import java.time.Duration;

import stormpot.Poolable;
import stormpot.Slot;

public abstract class Connection implements Poolable {

	public interface Factory {
		Connection create( final Slot slot, final Duration connectTimeout,
				final Duration requestTimeout ) throws IOException;
	}

	private final Slot slot;

	public Connection( final Slot slot ) {
		this.slot = slot;
	}

	@Override
	public void release() {
		slot.release( this );
	}

	public void expire() {
		slot.expire( this );
	}

	public abstract String send( final String request ) throws IOException;
}
