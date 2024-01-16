package com.furnaghan.spring.jsonrpc.server.json.protocol;

import com.furnaghan.spring.jsonrpc.server.error.RpcServerException;

public record JsonRpcError(int code, String message, Object data) {
	public static final int METHOD_NOT_FOUND = -32601;
	public static final int INVALID_PARAMS = -32602;
	public static final int PARSE_ERROR = -32700;
	public static final int UNKNOWN_ERROR = -32603;

	public static JsonRpcError from( final Throwable error ) {
		if ( error instanceof RpcServerException rpc ) {
			return new JsonRpcError( rpc.getCode(), rpc.getMessage(), rpc.getData() );
		}

		return new JsonRpcError( UNKNOWN_ERROR, error.getMessage(), null );
	}
}
