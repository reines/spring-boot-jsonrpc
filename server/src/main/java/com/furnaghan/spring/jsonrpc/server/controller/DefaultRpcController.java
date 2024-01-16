package com.furnaghan.spring.jsonrpc.server.controller;

import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;

import com.furnaghan.spring.jsonrpc.api.RpcController;
import com.furnaghan.spring.jsonrpc.api.RpcMethod;

@RpcController(namespace = "")
public class DefaultRpcController {

	private final HealthEndpoint healthEndpoint;

	public DefaultRpcController( final HealthEndpoint healthEndpoint ) {
		this.healthEndpoint = healthEndpoint;
	}

	@RpcMethod
	public boolean ping() {
		return true;
	}

	@RpcMethod
	public boolean available() {
		return healthEndpoint.health().getStatus().equals( Status.UP );
	}
}
