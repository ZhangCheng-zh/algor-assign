import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;

public class Graph {
    public Graph() {

    }

    private static boolean checkParametersCount(String[] args) {
        if (args.length != 4 || args[0].equals("--help") || args[0].equals("-h")) {
            System.out.println("Usage: java Graph <input file name> <number of nodes> <starting node> <output file name>");
            return false;
        }
        return true;
    }

    private static List<Integer[]> checkAndOpenFile(String fileName) {
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            List<Integer[]> edges = new ArrayList<Integer[]>();
            while ((line = bufferedReader.readLine()) != null) {
                String[] splitLine = line.split(" ");

                edges.add(new Integer[] { Integer.parseInt(splitLine[0]), Integer.parseInt(splitLine[1])});
            }
            bufferedReader.close();
            return edges;
        } catch (IOException e) {
            System.out.println("Failed to open input file.");
            return null;
        }
    }

    private static HashMap<Integer, List<Integer>> buildGraph(List<Integer[]> edges) {
        HashMap<Integer, List<Integer>> graph = new HashMap<>();

        for (int i = 0; i < edges.size(); i++) {
            Integer s = edges.get(i)[0], e = edges.get(i)[1];
            if (!graph.containsKey(s)) {
                graph.put(s, new ArrayList<>());
            }
            if (!graph.containsKey(e)) {
                graph.put(e, new ArrayList<>());
            }
            graph.get(s).add(e);
            graph.get(e).add(s);
        }

        return graph;
    }

    private static HashMap<Integer, List<Integer>> fromFile(String inputFileName) {
        List<Integer[]> edges =  checkAndOpenFile(inputFileName);
        HashMap<Integer, List<Integer>> graph = buildGraph(edges);
        return graph;
    }

    private static void createOutputFile(List<String> list, String outputFileName) {
        if (list == null || list.isEmpty()) {
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName))) {
            for (int i = 0; i < list.size(); i++) {
                String item = list.get(i);
                writer.write(item);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Integer[] swag(Integer firstNode, Integer secondNode) {
        if (firstNode > secondNode) {
            return new Integer[] { secondNode, firstNode };
        }
        return new Integer[] { firstNode, secondNode };
    }

    private static List<String> BFS(HashMap<Integer, List<Integer>>  graph, Integer nodeCount, Integer startNode) {
        List<String> outputList = new ArrayList<> ();
        outputList.add("BFS: ");

        Boolean[] visited = new Boolean[nodeCount + 1];
        for (int i = 0; i < visited.length; i++) {
            visited[i] = false;
        }

        Deque<Integer> deque = new ArrayDeque<>();
        deque.addFirst(startNode);
        visited[startNode] = true;

        while (!deque.isEmpty()) {
            Integer targetNode = deque.pollLast();

            List<Integer> neigbours = graph.get(targetNode);

            for (int j = 0; j < neigbours.size(); j++) {
                Integer nxtNode = neigbours.get(j);

                if (!visited[nxtNode]) {
                    deque.addFirst(nxtNode);
                    visited[nxtNode] = true;
                    Integer[] targetEdge = swag(targetNode, nxtNode);
                    outputList.add(targetEdge[0] + " " + targetEdge[1]);
                }
            }
        }

        return outputList;
    }

    private static List<String> nonRecursiveDFS(HashMap<Integer, List<Integer>>  graph, Integer nodeCount, Integer startNode) {
        List<String> outputList = new ArrayList<> ();
        outputList.add("DFS: ");

        Set<Integer> visited = new HashSet<>();
        Stack<Integer[]> st = new Stack<>();

        st.push(new Integer[] { 0, startNode });

        while (!st.isEmpty()) {
            Integer[] targetEdge = st.pop();
            int current = targetEdge[1];
            if (visited.contains(current)) continue;
            targetEdge = swag(targetEdge[0], targetEdge[1]);

            if (targetEdge[0] > 0) {
                outputList.add(targetEdge[0] + " " + targetEdge[1]);
            }

            visited.add(current);

            List<Integer> neigbours = graph.get(current);
            Collections.reverse(neigbours);

            for (int j = 0; j < neigbours.size(); j++) {
                if (!visited.contains(neigbours.get(j))) {
                    st.push(new Integer[] { current, neigbours.get(j)});
                }
            }
        }

        return outputList;
    }


    public static void main(String[] args) {
        // first check the parameter count is valid
        boolean parameterValid = checkParametersCount(args);
        if (!parameterValid) return;

        HashMap<Integer, List<Integer>> graph = fromFile(args[0]);
        Integer nodeCount = Integer.parseInt(args[1]);
        Integer startNode = Integer.parseInt(args[2]);
        String outputFileName = args[3];

        // third output the bfs result
        List<String> outputList = BFS(graph, nodeCount, startNode);

        // fourth output the dfs result
        outputList.addAll(nonRecursiveDFS(graph, nodeCount, startNode));
        createOutputFile(outputList, outputFileName);
    }

}
