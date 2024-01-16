package com.furnaghan.spring.jsonrpc.server.error;

import java.lang.reflect.Parameter;
import java.util.Arrays;

import com.furnaghan.spring.jsonrpc.server.json.protocol.JsonRpcError;

public class InvalidParameterException extends RpcServerException {
	public InvalidParameterException( final Parameter[] parameters ) {
		super( JsonRpcError.INVALID_PARAMS,
				"Invalid parameter, expected: " + Arrays.stream( parameters )
						.map( parameter -> String.format( "%s: %s", parameter.getName(),
								parameter.getType().getSimpleName() ) )
						.toList() );
	}
}
