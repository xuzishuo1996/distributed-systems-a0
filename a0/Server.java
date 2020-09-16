import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

class Server {
//	private static StringBuffer triangleStrBuf;

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
						Map<String, Set<String>> graph = new HashMap<>();
						List<String> list = new ArrayList<>();	// vertex lists for multi-threaded find triangle algorithm
						buildGraph(inputDataString, graph, list);
//						System.out.println("input graph: ");
//						for (Map.Entry<String, Set<String>> entry : graph.entrySet()) {
//							System.out.print(entry.getKey() + ": ");
//							for (String v2 : entry.getValue()) {
//								System.out.print(v2 + " ");
//							}
//							System.out.println();
//						}
//						System.out.println("===== The vertex list is: =====");
//						for (String s : list) {
//							System.out.println(s);
//						}
//						System.out.println();

						/* get triangles in the graph and write it to the output */
						StringBuffer triangleStrBuf = new StringBuffer();
						for (int id = 0; id < 2; ++id) {
							int finalId = id;
							new Thread(() -> {
								for (int i = finalId; i < list.size(); i += 2) {
									String v1 = list.get(i);
									for (String v2 : graph.get(v1)) {
										if (graph.containsKey(v2)) {
											for (String v3 : graph.get(v2)) {
												if (graph.get(v1).contains(v3)) {
													triangleStrBuf.append(v1 + " " + v2 + " " + v3 + '\n');	// StringBuffer is thread-safe
												}
											}
										}
									}
								}
							}).start();
						}
//						System.out.println("The triangles are:" + "\n" + triangleStrBuf);

						/* transfer the result back to the client */
						byte[] outBytes = triangleStrBuf.toString().getBytes(StandardCharsets.UTF_8);
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

	private static void buildGraph(String s, Map<String, Set<String>> graph, List<String> list) {
		Scanner scanner = new Scanner(s);	//default delimiter include " "(0x32) and "/n"(0x10)
		while (scanner.hasNext()) {
			String v1 = scanner.next();	//vertex 1
			String v2 = scanner.next();	//vertex 2

//            input is already is ascending order
//			// make sure (v1, v2) is in ascending order
//			if (isDescending(v1, v2)) {	// v1 > v2, swap
//				String tmp = v1;
//				v1 = v2;
//				v2 = tmp;
//			}
			if (!graph.containsKey(v1)) {
				graph.put(v1, new HashSet<>());
				list.add(v1);
			}
			graph.get(v1).add(v2);
		}
	}

//	private static class GetTriangles implements Runnable {
//		private final Map<String, Set<String>> graph;
//		private final List<String> list;
//		private final StringBuffer sb;	// wrong, should belong to Server
//		private final int id;
//
//		private GetTriangles(Map<String, Set<String>> graph, List<String> list, StringBuffer sb, int id) {
//			this.graph = graph;
//			this.list = list;
//			this.sb = sb;
//			this.id = id;
//		}
//
//		@Override
//		public void run() {
//			for (int i = id; i < list.size(); i += 2) {
//				String v1 = list.get(i);
//				for (String v2 : graph.get(v1)) {
//					if (graph.containsKey(v2)) {
//						for (String v3 : graph.get(v2)) {
//							if (graph.get(v1).contains(v3)) {
//								sb.append(v1 + " " + v2 + " " + v3 + '\n');
//							}
//						}
//					}
//				}
//			}
//		}
//	}

//	private static boolean isDescending(String v1, String v2) {
//		return v1.length() > v2.length() || (v1.length() == v2.length() && v1.compareTo(v2) > 0);
//	}
}
