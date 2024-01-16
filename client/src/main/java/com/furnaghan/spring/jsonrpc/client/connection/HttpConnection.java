package com.furnaghan.spring.jsonrpc.client.connection;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import stormpot.Slot;

public class HttpConnection extends Connection {

	public static Factory factory( final URI address ) {
		return ( slot, connectTimeout, requestTimeout ) -> new HttpConnection( slot, address,
				connectTimeout, requestTimeout );
	}

	private final HttpComponentsClientHttpRequestFactory factory;
	private final RestTemplate client;
	private final URI address;

	public HttpConnection( final Slot slot, final URI address, final Duration connectTimeout,
			final Duration requestTimeout ) {
		super( slot );

		this.address = address;

		factory = new HttpComponentsClientHttpRequestFactory();
		factory.setConnectTimeout( connectTimeout );
		factory.setConnectionRequestTimeout( requestTimeout );

		client = new RestTemplate( factory );
	}

	@Override
	public String send( final String request ) throws IOException {
		final ResponseEntity<String> response = client.postForEntity( address, request,
				String.class );
		if ( response.getStatusCode() != HttpStatus.OK ) {
			throw new IOException( "HTTP " + response.getStatusCode() );
		}
		return response.getBody();
	}

	@Override
	public void expire() {
		super.expire();

		try {
			factory.destroy();
		} catch ( final Exception e ) {
			throw new RuntimeException( e );
		}
	}
}
