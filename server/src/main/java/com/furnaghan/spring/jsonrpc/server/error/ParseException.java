package com.furnaghan.spring.jsonrpc.server.error;

import com.furnaghan.spring.jsonrpc.server.json.protocol.JsonRpcError;

public class ParseException extends RpcServerException {
	public ParseException( final Throwable cause ) {
		this( cause.getMessage() );
	}

	public ParseException( final String message ) {
		super( JsonRpcError.PARSE_ERROR, message );
	}
}
