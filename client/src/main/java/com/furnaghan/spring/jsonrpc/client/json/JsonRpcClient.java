package com.furnaghan.spring.jsonrpc.client.json;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.furnaghan.spring.jsonrpc.client.RpcClient;
import com.furnaghan.spring.jsonrpc.client.RpcClientBuilder;
import com.furnaghan.spring.jsonrpc.client.connection.Connection;
import com.furnaghan.spring.jsonrpc.client.error.RpcClientException;
import com.furnaghan.spring.jsonrpc.client.json.protocol.JsonRpcError;
import com.furnaghan.spring.jsonrpc.client.json.protocol.JsonRpcRequest;
import com.furnaghan.spring.jsonrpc.client.json.protocol.JsonRpcResponse;

import stormpot.Pool;

public class JsonRpcClient extends RpcClient {

	// @formatter:off
	private static final ObjectMapper MAPPER = new ObjectMapper()
			.configure( FAIL_ON_UNKNOWN_PROPERTIES, false )
			.registerModule( new JavaTimeModule() );
	// @formatter:on

	public static RpcClientBuilder<JsonRpcClient> builder() {
		return new RpcClientBuilder<>() {
			@Override
			protected JsonRpcClient build( final Pool<Connection> connections,
					final Duration poolTimeout ) {
				return new JsonRpcClient( connections, poolTimeout );
			}
		};
	}

	private final AtomicInteger ids;

	public JsonRpcClient( final Pool<Connection> connections, final Duration timeout ) {
		super( connections, timeout );

		ids = new AtomicInteger();
	}

	@Override
	protected String encode( final String namespace, final String name, final Object... params )
			throws IOException {
		final String method = String.format( "%s.%s", namespace, name );

		final JsonRpcRequest request = new JsonRpcRequest( ids.getAndIncrement(), method, params );
		return MAPPER.writeValueAsString( request );
	}

	@Override
	protected Object decode( final String message, final Type returnType ) throws IOException {
		final TypeReference<JsonRpcResponse<?>> responseType = JsonRpcResponse.ofType( returnType );
		final JsonRpcResponse<?> response = MAPPER.readValue( message, responseType );
		if ( response.getError() != null ) {
			final JsonRpcError error = response.getError();
			throw new RpcClientException( error.getCode(), error.getMessage(), error.getData() );
		}
		return response.getResult();
	}
}
