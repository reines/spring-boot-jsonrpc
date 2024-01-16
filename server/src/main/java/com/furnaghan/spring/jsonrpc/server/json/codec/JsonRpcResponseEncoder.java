package com.furnaghan.spring.jsonrpc.server.json.codec;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.furnaghan.spring.jsonrpc.server.json.protocol.JsonRpcResponse;

public class JsonRpcResponseEncoder extends MessageToMessageEncoder<JsonRpcResponse<?>> {

	private final ObjectMapper json;

	public JsonRpcResponseEncoder( final ObjectMapper json ) {
		this.json = json;
	}

	@Override
	protected void encode( final ChannelHandlerContext ctx, final JsonRpcResponse<?> response,
			final List<Object> out ) throws JsonProcessingException {
		out.add( json.writeValueAsString( response ) );
	}
}
