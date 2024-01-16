# JSON-RPC over TCP for Spring Boot

![GitHub Release](https://img.shields.io/github/v/release/reines/spring-boot-jsonrpc)
![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/reines/spring-boot-jsonrpc/build.yaml)
![GitHub License](https://img.shields.io/github/license/reines/spring-boot-jsonrpc)


> urm, what? who uses JSON-RPC over TCP??

This project consists of two modules:
* `com.furnaghan.spring.jsonrpc:server` - A JSON-RPC over TCP server implementation using Netty and Jackson. Usage is roughly akin to the well known Spring `@RestController` - using the `@RpcController(namespace = "foo)` and `@RpcMethod(name = "bar")` annotations.
* `com.furnaghan.spring.jsonrpc:client` - A JSON-RPC over TCP client.
