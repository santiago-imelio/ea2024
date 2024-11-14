package com.aegroupw;

import java.util.Map;

import org.jgrapht.Graph;

import com.aegroupw.montecarlo.NetworkReliabilitySimulator;
import com.aegroupw.network.NetworkEdge;
import com.aegroupw.network.NetworkNode;
import com.aegroupw.utils.GraphParser;

public class Main {
    public static void main(String[] args) {
        String vertexFile = "graphs/fully_connected/nodes.txt";
        String edgesFile = "graphs/fully_connected/edges.txt";

        Graph<NetworkNode, NetworkEdge> graph = GraphParser.parseGraphFromFile(edgesFile, vertexFile);

        // System.out.println(GraphParser.networkToDOT(graph));
        Map<String, Double> res = NetworkReliabilitySimulator.estimateReliability(graph, 100000);
        System.out.println(res);
    }
}