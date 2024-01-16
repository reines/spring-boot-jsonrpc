package com.furnaghan.spring.jsonrpc.client.json.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JsonRpcRequest {

	private static final String VERSION = "2.0";

	private final int id;
	private final String method;
	private final Object[] params;

	public JsonRpcRequest( final int id, final String method, final Object[] params ) {
		this.id = id;
		this.method = method;
		this.params = params;
	}

	@JsonProperty("jsonrpc")
	public String getVersion() {
		return VERSION;
	}

	@JsonProperty("id")
	public int getId() {
		return id;
	}

	@JsonProperty("method")
	public String getMethod() {
		return method;
	}

	@JsonProperty("params")
	public Object[] getParams() {
		return params;
	}
}
