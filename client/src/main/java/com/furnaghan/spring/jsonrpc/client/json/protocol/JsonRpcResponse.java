package com.furnaghan.spring.jsonrpc.client.json.protocol;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;

public class JsonRpcResponse<T> {

	public static TypeReference<JsonRpcResponse<?>> ofType( final Type resultType ) {
		return new TypeReference<JsonRpcResponse<?>>() {
			@Override
			public Type getType() {
				return new ParameterizedType() {
					@Override
					public Type[] getActualTypeArguments() {
						return new Type[] { resultType };
					}

					@Override
					public Type getRawType() {
						return JsonRpcResponse.class;
					}

					@Override
					public Type getOwnerType() {
						return null;
					}
				};
			}
		};
	}

	private final T result;
	private final JsonRpcError error;

	// @formatter:off
	@JsonCreator
	public JsonRpcResponse(
			@JsonProperty("result") final T result,
			@JsonProperty("error") final JsonRpcError error
	) {
	// @formatter:on
		this.result = result;
		this.error = error;
	}

	public T getResult() {
		return result;
	}

	public JsonRpcError getError() {
		return error;
	}
}
