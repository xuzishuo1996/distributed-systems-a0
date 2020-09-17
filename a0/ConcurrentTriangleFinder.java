import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class ConcurrentTriangleFinder {
    private final String inputString;
    private final ConcurrentHashMap<String, Set<String>> graph;
    private final Set<String> set;	// vertex set for multi-threaded find triangle algorithm
    private final StringBuffer triangleStrBuf;    // result
    private final CountDownLatch buildGate;
    private final CountDownLatch findGate;

    public ConcurrentTriangleFinder(String inputString) {
        this.inputString = inputString;
        this.graph = new ConcurrentHashMap<>();
        this.set = Collections.newSetFromMap(new ConcurrentHashMap<>());
        this.triangleStrBuf = new StringBuffer();
        this.buildGate = new CountDownLatch(2);
        this.findGate = new CountDownLatch(2);
    }

    /* construct the graph: Adjacency Sets - elems in sets are larger than their key */
    public void buildGraph() {
        BufferedReader reader = new BufferedReader(new StringReader(inputString));

        for (int id = 0; id < 2; ++id) {
            new Thread(() -> {
                try {
                    String s;
                    while ((s = reader.readLine()) != null) {
                        String[] nodes = s.split(" ");
                        if (!graph.containsKey(nodes[0])) {
                            graph.put(nodes[0], new HashSet<>());
                            set.add(nodes[0]);
                        }
                        graph.get(nodes[0]).add(nodes[1]);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                buildGate.countDown();
            }).start();
        }

        try {
            buildGate.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        System.out.println("input graph: ");
//        for (Map.Entry<String, Set<String>> entry : graph.entrySet()) {
//            System.out.print(entry.getKey() + ": ");
//            for (String v2 : entry.getValue()) {
//                System.out.print(v2 + " ");
//            }
//            System.out.println();
//        }
//        System.out.println("===== The vertex list is: =====");
//        for (String s : list) {
//            System.out.println(s);
//        }
//        System.out.println();
    }

//  private boolean isDescending(String v1, String v2) {
//		return v1.length() > v2.length() || (v1.length() == v2.length() && v1.compareTo(v2) > 0);
//	}

    public String getTriangles() {
        for (int id = 0; id < 2; ++id) {
//            new Thread(new GetTrianglesTask(id)).start();
            new Thread(() -> {
                for (String v1 : set) {
                    for (String v2 : graph.get(v1)) {
                        if (graph.containsKey(v2)) {
                            for (String v3 : graph.get(v2)) {
                                if (graph.get(v1).contains(v3)) {
                                    triangleStrBuf.append(v1 + " " + v2 + " " + v3 + '\n');
                                }
                            }
                        }
                    }
                    set.remove(v1);
                }

                findGate.countDown();
            }).start();
        }
        try {
            findGate.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return triangleStrBuf.toString();
    }

//    private class GetTrianglesTask implements Runnable {
//        private final int id;
//
//        private GetTrianglesTask(int id) {
//            this.id = id;
//        }
//
//        @Override
//		public void run() {
//			for (int i = id; i < list.size(); i += 2) {
//				String v1 = list.get(i);
//				for (String v2 : graph.get(v1)) {
//					if (graph.containsKey(v2)) {
//						for (String v3 : graph.get(v2)) {
//							if (graph.get(v1).contains(v3)) {
//								triangleStrBuf.append(v1 + " " + v2 + " " + v3 + '\n');
//							}
//						}
//					}
//				}
//			}
//			findGate.countDown();
//		}
//	}

}
