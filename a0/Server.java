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
//		int port = 10123;

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
				/* accept a connection from the server socket. */
				Socket clientSocket = ssock.accept();

				while (true) {
					try {
						/* get edges from the input as bytes */
						DataInputStream in = new DataInputStream(clientSocket.getInputStream());
						int reqDataLen = in.readInt();
//						System.out.println("received request header, data payload has length " + reqDataLen);
						byte[] bytes = new byte[reqDataLen];
						in.readFully(bytes);
//						System.out.println("input bytes:");
//						for (Byte b : bytes) {
//							System.out.println(b);
//						}

						String inputDataString = new String(bytes, StandardCharsets.UTF_8);
						//System.out.println(inputDataString);

						/* construct the graph: Adjacency Sets - elems in sets are larger than their key */
						ConcurrentTriangleFinder finder = new ConcurrentTriangleFinder(inputDataString);
						finder.buildGraph();

						/* get triangles in the graph and write it to the output */
						String triangleStr = finder.getTriangles();
//						System.out.println("The triangles are:" + "\n" + triangleStr);

						/* transfer the result back to the client */
						byte[] outBytes = triangleStr.getBytes(StandardCharsets.UTF_8);
						DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
						out.writeInt(outBytes.length);
						out.write(outBytes);
						out.flush();
					} catch (IOException e) {    //the client has closed the connection
						break;
					}
				}

				try {
					clientSocket.close();
				} catch (IOException e) {
					System.out.println("fail to close the client socket!");
					e.printStackTrace();
				}

			} catch (Exception e) {
				try {
					ssock.close();
				} catch (IOException ioException) {
					System.out.println("fail to close the server socket!");
				}
				e.printStackTrace();
			}
		}
	}
}
