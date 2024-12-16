package com.aegroupw;

import org.jgrapht.Graph;
import org.uma.jmetal.solution.binarysolution.BinarySolution;

import com.aegroupw.evolutionary.NetworkOptimizationProblem;
import com.aegroupw.network.NetworkEdge;
import com.aegroupw.network.NetworkNode;
import com.aegroupw.utils.GraphParser;

public class Main {
    public static void main(String[] args) {
        String vertexFile = "graphs/fully_connected/nodes.txt";
        String edgesFile = "graphs/fully_connected/edges.txt";

        Graph<NetworkNode, NetworkEdge> graph = GraphParser.parseGraphFromFile(edgesFile, vertexFile);

        NetworkOptimizationProblem problem = new NetworkOptimizationProblem(graph, 0.5, 100000);

        BinarySolution solution = problem.createSolution();

        BinarySolution evaluatedSolution = problem.evaluate(solution);

        System.out.println(evaluatedSolution);
    }
}