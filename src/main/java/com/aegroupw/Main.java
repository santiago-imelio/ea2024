package com.aegroupw;

import org.jgrapht.Graph;

import com.aegroupw.montecarlo.NetworkReliabilitySimulator;
import com.aegroupw.network.NetworkEdge;
import com.aegroupw.network.NetworkNode;
import com.aegroupw.utils.GraphParser;

public class Main {
    public static void main(String[] args) {
        String vertexFile = "graphs/example2_vertices.txt";
        String edgesFile = "graphs/example2_edges.txt";
        Graph<NetworkNode, NetworkEdge> graph = GraphParser.parseGraphFromFile(edgesFile, vertexFile);

        NetworkReliabilitySimulator.estimateReliability(graph, 100000);
    }
}