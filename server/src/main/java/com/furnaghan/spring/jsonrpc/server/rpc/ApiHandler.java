package com.furnaghan.spring.jsonrpc.server.rpc;

import java.lang.reflect.InvocationTargetException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.furnaghan.spring.jsonrpc.server.error.MethodNotFoundException;

public class ApiHandler extends ApiDefinition {

	private final Object api;

	public ApiHandler( final String namespace, final Class<?> apiInterface, final Object api,
			final ObjectMapper objectMapper ) {
		super( namespace, apiInterface, objectMapper );

		this.api = api;

		if ( !apiInterface.isInstance( api ) ) {
			throw new IllegalArgumentException();
		}
	}

	public Object invoke( final String methodName, final Object params )
			throws InvocationTargetException, IllegalAccessException {
		final ApiMethod method = getMethod( methodName ).orElseThrow(
				() -> new MethodNotFoundException( getNamespace(), methodName ) );

		return method.invoke( api, params );
	}
}
