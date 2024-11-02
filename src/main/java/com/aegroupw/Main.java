package com.aegroupw;

import org.jgrapht.Graph;

public class Main {
    public static void main(String[] args) {
        String vertexFile = "graphs/example_vertices.txt";
        String edgesFile = "graphs/example_edges.txt"; // specify the path to your graph file
        Graph<NetworkNode, NetworkEdge> graph = GraphParser.parseGraphFromFile(edgesFile, vertexFile);

        // Print out the graph to verify it was constructed correctly
        System.out.println("Vertices: " + graph.vertexSet());
        System.out.println("Edges: " + graph.edgeSet());

        for (NetworkEdge edge : graph.edgeSet()) {
            NetworkNode source = graph.getEdgeSource(edge);
            NetworkNode target = graph.getEdgeTarget(edge);
            System.out.println("Edge: " + source + " -- " + target + " " + edge);
        }
    }
}