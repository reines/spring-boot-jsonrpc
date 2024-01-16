package com.furnaghan.spring.jsonrpc.server.json.protocol;

public record JsonRpcResponse<T>(T result, JsonRpcError error) {}
