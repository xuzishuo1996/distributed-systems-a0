import java.util.*;
import java.util.concurrent.CountDownLatch;

public class ConcurrentTriangleFinder {
    private final String inputString;
    private final Map<String, Set<String>> graph;
    private final List<String> list;	// vertex lists for multi-threaded find triangle algorithm
    private final StringBuffer triangleStrBuf;    // result
    private final CountDownLatch endGate;

    public ConcurrentTriangleFinder(String inputString) {
        this.inputString = inputString;
        this.graph = new HashMap<>();
        this.list = new ArrayList<>();
        this.triangleStrBuf = new StringBuffer();
        this.endGate = new CountDownLatch(2);
    }

    /* construct the graph: Adjacency Sets - elems in sets are larger than their key */
    public void buildGraph() {
        Scanner scanner = new Scanner(inputString);	//default delimiter include " "(0x32) and "/n"(0x10)
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
        scanner.close();

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
            new Thread(new GetTrianglesTask(id)).start();
        }
        try {
            endGate.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return triangleStrBuf.toString();
    }

    private class GetTrianglesTask implements Runnable {
        private final int id;

        private GetTrianglesTask(int id) {
            this.id = id;
        }

        @Override
		public void run() {
			for (int i = id; i < list.size(); i += 2) {
				String v1 = list.get(i);
				for (String v2 : graph.get(v1)) {
					if (graph.containsKey(v2)) {
						for (String v3 : graph.get(v2)) {
							if (graph.get(v1).contains(v3)) {
								triangleStrBuf.append(v1 + " " + v2 + " " + v3 + '\n');
							}
						}
					}
				}
			}
			endGate.countDown();
		}
	}

}
