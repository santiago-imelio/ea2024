package com.aegroupw;

import org.jgrapht.Graph;

public class Main {
    public static void main(String[] args) {
        String filename = "graphs/example.txt"; // specify the path to your graph file
        Graph<String, NetworkEdge> graph = GraphParser.parseGraphFromFile(filename);

        // Print out the graph to verify it was constructed correctly
        System.out.println("Vertices: " + graph.vertexSet());
        System.out.println("Edges: " + graph.edgeSet());

        for (NetworkEdge edge : graph.edgeSet()) {
            String source = graph.getEdgeSource(edge);
            String target = graph.getEdgeTarget(edge);
            System.out.println("Edge: " + source + " -- " + target + " " + edge);
        }
    }
}