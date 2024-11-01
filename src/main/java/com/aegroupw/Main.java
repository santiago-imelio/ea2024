package com.aegroupw;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class Main {
    public static void main(String[] args) {
        SimpleWeightedGraph<Integer, DefaultEdge> network = new SimpleWeightedGraph<>(DefaultEdge.class);

        network.addVertex(0);
        network.addVertex(1);
        network.addEdge(0, 1);

        System.out.println(network.toString());
    }
}