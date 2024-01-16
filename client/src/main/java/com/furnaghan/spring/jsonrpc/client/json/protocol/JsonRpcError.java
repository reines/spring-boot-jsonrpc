package com.furnaghan.spring.jsonrpc.client.json.protocol;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class JsonRpcError {

	private final int code;
	private final String message;
	private final Object data;

	// @formatter:off
	@JsonCreator
	public JsonRpcError(
			@JsonProperty("code") final int code,
			@JsonProperty("message") final String message,
			@JsonProperty("data") final Object data
	) {
	// @formatter:on
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public Object getData() {
		return data;
	}
}
