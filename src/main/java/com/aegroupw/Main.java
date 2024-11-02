package com.aegroupw;

import org.jgrapht.Graph;

import com.aegroupw.network.NetworkEdge;
import com.aegroupw.network.NetworkNode;
import com.aegroupw.utils.GraphParser;

public class Main {
    public static void main(String[] args) {
        String vertexFile = "graphs/example_vertices.txt";
        String edgesFile = "graphs/example_edges.txt";
        Graph<NetworkNode, NetworkEdge> graph = GraphParser.parseGraphFromFile(edgesFile, vertexFile);

        System.out.println(GraphParser.networkToDOT(graph));
    }
}