package com.aegroupw.experiments;

import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;

import com.aegroupw.heuristics.ModifiedPrimSolver;
import com.aegroupw.montecarlo.NetworkReliabilitySimulator;
import com.aegroupw.network.NetworkEdge;
import com.aegroupw.network.NetworkNode;
import com.aegroupw.utils.GraphParser;

public class GreedyExperiment {
  public static void run(String experimentName, double alpha, double beta) {
    String edges = "graphs/fully_connected/edges.txt";
    String nodes = "graphs/fully_connected/nodes.txt";

    Graph<NetworkNode,NetworkEdge> g = GraphParser.parseGraphFromFile(edges, nodes);

    Set<NetworkNode> servers = NetworkReliabilitySimulator.findServerNodes(g);
    Set<NetworkNode> clients = NetworkReliabilitySimulator.findClientNodes(g);

    Graph<NetworkNode, NetworkEdge> mst = ModifiedPrimSolver.findMST(g, servers, clients, alpha, beta);

    Utils.saveExperimentDOTGraph(experimentName, mst);

    Map<String, Double> result = NetworkReliabilitySimulator.estimateReliability(mst, 10000);

    double totalCost = 0;
    for (NetworkEdge e : g.edgeSet()) {
      totalCost += e.getCost();
    }

    System.out.println("total cost: " + totalCost);
    System.out.println("rlb: " + result.get("rlb"));
  }
}
