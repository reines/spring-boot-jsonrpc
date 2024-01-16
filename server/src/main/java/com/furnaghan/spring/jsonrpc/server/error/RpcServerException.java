package com.furnaghan.spring.jsonrpc.server.error;

import org.springframework.lang.Nullable;

public abstract class RpcServerException extends RuntimeException {

	private final int code;
	private final Object data;

	public RpcServerException( final int code, final String message ) {
		this( code, message, null );
	}

	public RpcServerException( final int code, final String message, @Nullable final Object data ) {
		super( message );

		this.code = code;
		this.data = data;
	}

	public int getCode() {
		return code;
	}

	public Object getData() {
		return data;
	}
}
