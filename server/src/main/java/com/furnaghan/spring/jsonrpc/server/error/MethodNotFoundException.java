package com.furnaghan.spring.jsonrpc.server.error;

import java.util.Map;

import org.springframework.lang.Nullable;

import com.furnaghan.spring.jsonrpc.server.json.protocol.JsonRpcError;

public class MethodNotFoundException extends RpcServerException {
	public MethodNotFoundException( @Nullable final String namespace, final String method ) {
		super( JsonRpcError.METHOD_NOT_FOUND, "Method not found",
				Map.of( "namespace", String.valueOf( namespace ), "method", method ) );
	}
}
