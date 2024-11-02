package com.aegroupw;

import org.jgrapht.Graph;

public class Main {
    public static void main(String[] args) {
        String vertexFile = "graphs/example_vertices.txt";
        String edgesFile = "graphs/example_edges.txt";
        Graph<NetworkNode, NetworkEdge> graph = GraphParser.parseGraphFromFile(edgesFile, vertexFile);

        System.out.println(GraphParser.networkToDOT(graph));
    }
}