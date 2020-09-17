import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
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

    public String solve() {
        /* construct the graph: Adjacency Sets - elems in sets are larger than their key */
        BufferedReader reader = new BufferedReader(new StringReader(inputString));
        String s;
        try {
            while ((s = reader.readLine()) != null) {
                String[] nodes = s.split(" ");
                if (!graph.containsKey(nodes[0])) {
                    graph.put(nodes[0], new HashSet<>());
                    list.add(nodes[0]);
                }
                graph.get(nodes[0]).add(nodes[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //get triangles: multi-threaded using thread-safe StringBuffer
        for (int id = 0; id < 2; ++id) {
            final int finalId = id;
            new Thread(() -> {
                for (int i = finalId; i < list.size(); i += 2) {
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
            }).start();
        }
        try {
            endGate.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return triangleStrBuf.toString();
    }
}
