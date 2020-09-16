import java.util.*;
import java.util.concurrent.*;

public class ConcurrentTriangleFinder {
    private static final int NUM_OF_CPU = Runtime.getRuntime().availableProcessors();

    private final String inputString;
    private final Map<String, Set<String>> graph;
    private final List<String> list;	// vertex lists for multi-threaded find triangle algorithm
    private final StringBuilder triangleStringBuilder;    // result

    public ConcurrentTriangleFinder(String inputString) {
        this.inputString = inputString;
        this.graph = new HashMap<>();
        this.list = new ArrayList<>();
        this.triangleStringBuilder = new StringBuilder();
        System.out.println("NUM_OF_CPU: " + NUM_OF_CPU);
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
//        ExecutorService exec = Executors.newFixedThreadPool(2);
        for (int id = 0; id < NUM_OF_CPU; ++id) {
//            Future<String> future = exec.submit(new GetTrianglesTask(id));
            FutureTask<StringBuilder> ft = new FutureTask<>(new GetTrianglesTask(id));
            ft.run();
            try {
                StringBuilder triangleStr = ft.get();
                triangleStringBuilder.append(triangleStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        exec.shutdownNow();
        return triangleStringBuilder.toString();
    }

    private class GetTrianglesTask implements Callable<StringBuilder> {
        private final int id;

        private GetTrianglesTask(int id) {
            this.id = id;
        }

        @Override
		public StringBuilder call() {
            StringBuilder buf = new StringBuilder();
			for (int i = id; i < list.size(); i += NUM_OF_CPU) {
				String v1 = list.get(i);
				for (String v2 : graph.get(v1)) {
					if (graph.containsKey(v2)) {
						for (String v3 : graph.get(v2)) {
							if (graph.get(v1).contains(v3)) {
								buf.append(v1 + " " + v2 + " " + v3 + '\n');
							}
						}
					}
				}
			}
			return buf;
		}
	}

}
