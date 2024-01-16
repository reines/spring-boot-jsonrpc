package com.furnaghan.spring.jsonrpc.server.json.protocol;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

public record JsonRpcRequest(String id, String method, Object params) {
	private static final String VERSION = "2.0";

	@JsonProperty("jsonrpc")
	public String getVersion() {
		return VERSION;
	}

	public Optional<String> extractNamespace() {
		final int pos = method.lastIndexOf( '.' );
		if ( pos < 0 ) {
			return Optional.empty();
		} else {
			return Optional.of( method.substring( 0, pos ) );
		}
	}

	public String extractMethod() {
		final int pos = method.lastIndexOf( '.' );
		if ( pos < 0 ) {
			return method;
		} else {
			return method.substring( pos + 1 );
		}
	}
}
