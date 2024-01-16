package com.furnaghan.spring.jsonrpc.server.rpc;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.furnaghan.spring.jsonrpc.api.RpcMethod;

public class ApiDefinition {

	private final String namespace;
	private final Map<String, ApiMethod> methods;

	public ApiDefinition( final String namespace, final Class<?> api,
			final ObjectMapper objectMapper ) {
		this.namespace = namespace;
		this.methods = Stream.of( api.getMethods() )
				.filter( method -> method.isAnnotationPresent( RpcMethod.class ) )
				.map( method -> new ApiMethod( method, objectMapper ) )
				.collect( Collectors.toMap( ApiMethod::getName, Function.identity() ) );
	}

	public String getNamespace() {
		return namespace;
	}

	public Optional<ApiMethod> getMethod( final String signature ) {
		return Optional.ofNullable( methods.get( signature ) );
	}

	@Override
	public String toString() {
		return String.format( "{namespace=%s, methods=%s}", namespace, methods.keySet() );
	}
}
