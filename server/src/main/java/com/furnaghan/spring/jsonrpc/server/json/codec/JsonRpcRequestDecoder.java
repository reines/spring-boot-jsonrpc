package com.furnaghan.spring.jsonrpc.server.json.codec;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.furnaghan.spring.jsonrpc.server.json.protocol.JsonRpcRequest;

public class JsonRpcRequestDecoder extends ByteToMessageDecoder {

	private final ObjectMapper json;

	public JsonRpcRequestDecoder( final ObjectMapper json ) {
		this.json = json;
	}

	@Override
	protected void decode( final ChannelHandlerContext ctx, final ByteBuf in,
			final List<Object> out ) throws IOException {
		try ( final InputStream stream = new ByteBufInputStream( in ) ) {
			out.add( json.readValue( stream, JsonRpcRequest.class ) );
		}
	}
}
