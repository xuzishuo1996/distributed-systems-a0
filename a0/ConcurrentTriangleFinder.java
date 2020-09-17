import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class ConcurrentTriangleFinder {
    private final String inputString;
    private final ConcurrentHashMap<String, Set<String>> graph;
    private final Set<String> set;	// vertex set for multi-threaded find triangle algorithm
    private final StringBuffer triangleStrBuf;    // result
    private final CyclicBarrier barrier;
    private final CountDownLatch findGate;

    public ConcurrentTriangleFinder(String inputString) {
        this.inputString = inputString;
        this.graph = new ConcurrentHashMap<>();
        this.set = Collections.newSetFromMap(new ConcurrentHashMap<>());
        this.triangleStrBuf = new StringBuffer();
        this.barrier = new CyclicBarrier(2);
        this.findGate = new CountDownLatch(2);
    }

    public String getTriangles() {
        BufferedReader reader = new BufferedReader(new StringReader(inputString));

        for (int id = 0; id < 2; ++id) {
            /* construct the graph: Adjacency Sets - elems in sets are larger than their key */
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

                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }

                //get triangles
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
}
