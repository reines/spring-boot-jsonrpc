package com.furnaghan.spring.jsonrpc.client.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import stormpot.Slot;

public class TcpConnection extends Connection {

	public static Factory factory( final String host, final int port ) {
		return ( slot, connectTimeout, requestTimeout ) -> new TcpConnection( slot, host, port,
				connectTimeout, requestTimeout );
	}

	private final Socket socket;
	private final BufferedReader in;
	private final OutputStreamWriter out;

	public TcpConnection( final Slot slot, final String host, final int port,
			final Duration connectTimeout, final Duration requestTimeout ) throws IOException {
		super( slot );

		socket = new Socket();

		socket.setReuseAddress( true );
		socket.setKeepAlive( true );
		socket.setTcpNoDelay( true );
		socket.setSoLinger( true, 1 );

		socket.connect( new InetSocketAddress( host, port ), (int) connectTimeout.toMillis() );
		socket.setSoTimeout( (int) requestTimeout.toMillis() );

		in = new BufferedReader(
				new InputStreamReader( socket.getInputStream(), StandardCharsets.UTF_8 ) );
		out = new OutputStreamWriter( socket.getOutputStream(), StandardCharsets.UTF_8 );
	}

	@Override
	public String send( final String request ) throws IOException {
		out.write( request + "\n" );
		out.flush();
		return in.readLine();
	}

	@Override
	public void expire() {
		super.expire();

		try {
			in.close();
			out.close();
			socket.close();
		} catch ( final IOException e ) {
			throw new RuntimeException( e );
		}
	}
}
