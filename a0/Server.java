import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

class Server {
	public static void main(String args[]) throws Exception {
		if (args.length != 1) {
			System.out.println("usage: java CCServer port");
			System.exit(-1);
		}
		int port = Integer.parseInt(args[0]);

		ServerSocket ssock = new ServerSocket(port);
		System.out.println("listening on port " + port);
		while(true) {
			try {
				/*
				  YOUR CODE GOES HERE
				  - accept a connection from the server socket
				  - add an inner loop to read requests from this connection
				    repeatedly (client may reuse the connection for multiple
				    requests)
				  - for each request, compute an output and send a response
				  - each message has a 4-byte header followed by a payload
				  - the header is the length of the payload
				    (signed, two's complement, big-endian)
				  - the payload is a string (UTF-8)
				  - the inner loop ends when the client closes the connection
				*/
				/* 1. accept a connection from the server socket. */
				Socket connectionSocket = ssock.accept();

				while (true) {
					try {
						/* get edges from the input as bytes */
						DataInputStream in = new DataInputStream(connectionSocket.getInputStream());
						int reqDataLen = in.readInt();
						System.out.println("received request header, data payload has length " + reqDataLen);
						byte[] bytes = new byte[reqDataLen];
						in.readFully(bytes);
						System.out.println("write out input bytes:");
						for (Byte b: bytes) {
							System.out.println(b);
						}

						String inputDataString = new String(bytes, StandardCharsets.UTF_8);
						//System.out.println(inputDataString);

						/* construct the graph */
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
