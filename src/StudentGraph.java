package src;

import java.util.*;

/**
 * Represents an undirected, weighted graph where each node is a {@link UniversityStudent}.
 * The graph is used for:
 * <ul>
 *     <li>Computing roommate matching (indirectly, via connection strengths)</li>
 *     <li>Referral path finding using Dijkstra's algorithm</li>
 *     <li>Displaying student relationship networks</li>
 * </ul>
 *
 * <p>The graph uses an adjacency list representation where each student maps
 * to a list of {@link Edge} objects. The constructor automatically builds
 * the complete graph by computing connection strengths between every pair
 * of students.</p>
 *
 * <p>Edges are symmetric (undirected): if A connects to B with weight W,
 * then B connects to A with the same weight.</p>
 */
public class StudentGraph {

    /**
     * Represents a weighted edge between two students.
     * Each edge contains:
     * <ul>
     *     <li>The neighboring {@code UniversityStudent}</li>
     *     <li>The integer connection strength used as the edge weight</li>
     * </ul>
     */
    public static class Edge {
        /** The student on the other end of the edge. */
        public final UniversityStudent neighbor;

        /** The connection weight between the two students. */
        public final int weight;

        /**
         * Constructs a graph edge.
         *
         * @param neighbor the connected student
         * @param weight the connection strength weight
         */
        public Edge(UniversityStudent neighbor, int weight) {
            this.neighbor = neighbor;
            this.weight = weight;
        }

        @Override
        public String toString() {
            return String.format("Edge{neighbor=%s, weight=%d}", neighbor.name, weight);
        }
    }

    /** Adjacency list storing each student and its list of weighted edges. */
    private final Map<UniversityStudent, List<Edge>> adj;

    /**
     * Constructs an undirected graph using a list of students.
     *
     * <p>The constructor performs the following steps:</p>
     * <ol>
     *     <li>Initializes each student as a node</li>
     *     <li>Computes connection strengths between every pair of students</li>
     *     <li>Adds edges in both directions using the maximum of A→B and B→A strength</li>
     * </ol>
     *
     * <p>Edges with weight 0 are still included to support referral path traversal.</p>
     *
     * @param students the list of students to include in the graph
     */
    public StudentGraph(List<UniversityStudent> students) {
        this.adj = new LinkedHashMap<>();
        if (students == null) return;

        // Initialize nodes
        for (UniversityStudent s : students) {
            adj.put(s, new ArrayList<>());
        }

        // Build symmetric edges
        List<UniversityStudent> list = new ArrayList<>(students);
        int n = list.size();

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                UniversityStudent a = list.get(i);
                UniversityStudent b = list.get(j);

                int w1 = a.calculateConnectionStrength(b);
                int w2 = b.calculateConnectionStrength(a);

                // Use the stronger of the two directional scores
                int weight = Math.max(w1, w2);

                // Add undirected edges
                addEdge(a, b, weight);
                addEdge(b, a, weight);
            }
        }
    }

    /**
     * Adds a directional edge from student {@code a} to student {@code b}
     * with a specified weight. Does not enforce symmetry by itself.
     *
     * @param a the source student
     * @param b the target student
     * @param weight the weight for the edge
     */
    public void addEdge(UniversityStudent a, UniversityStudent b, int weight) {
        adj.computeIfAbsent(a, k -> new ArrayList<>()).add(new Edge(b, weight));
    }

    /**
     * Retrieves a list of neighboring edges for a given student.
     *
     * @param s the student whose adjacency list is requested
     * @return a list of {@link Edge} objects connected to the student,
     *         or an empty list if the student is not in the graph
     */
    public List<Edge> getNeighbors(UniversityStudent s) {
        return adj.getOrDefault(s, Collections.emptyList());
    }

    /**
     * Returns a set of all student nodes in the graph.
     *
     * @return a set containing all {@link UniversityStudent} nodes
     */
    public Set<UniversityStudent> getAllNodes() {
        return adj.keySet();
    }

    /**
     * Prints the adjacency list of the graph to the console.
     * Useful for debugging and verifying graph construction.
     */
    public void displayGraph() {
        System.out.println("\n--- StudentGraph adjacency list ---");
        for (Map.Entry<UniversityStudent, List<Edge>> e : adj.entrySet()) {
            System.out.print(e.getKey().name + " -> ");

            List<String> parts = new ArrayList<>();
            for (Edge edge : e.getValue()) {
                parts.add(String.format("%s(%d)", edge.neighbor.name, edge.weight));
            }

            System.out.println(String.join(", ", parts));
        }
    }
}

