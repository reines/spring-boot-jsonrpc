package com.furnaghan.spring.jsonrpc.client.error;

public class RpcClientException extends RuntimeException {

	private final int code;
	private final Object data;

	public RpcClientException( final int code, final String message, final Object data ) {
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
