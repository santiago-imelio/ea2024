package com.aegroupw.experiments;

import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import org.jgrapht.Graph;

import com.aegroupw.heuristics.ModifiedPrimSolver;
import com.aegroupw.montecarlo.NetworkReliabilitySimulator;
import com.aegroupw.network.NetworkEdge;
import com.aegroupw.network.NetworkNode;
import com.aegroupw.utils.GraphParser;

public class GreedyExperiment {
  public static List<Map<String, Object>> run(String experimentName) {
    if (experimentName == null || experimentName.trim().isEmpty()) {
      experimentName = "experiment" + (int) (Math.random() * 1000);
    }

    String edges = "graphs/fully_connected/edges.txt";
    String nodes = "graphs/fully_connected/nodes.txt";

    Graph<NetworkNode, NetworkEdge> g = GraphParser.parseGraphFromFile(edges, nodes);

    Set<NetworkNode> servers = NetworkReliabilitySimulator.findServerNodes(g);
    Set<NetworkNode> clients = NetworkReliabilitySimulator.findClientNodes(g);

    // Parámetros a variar
    List<Double> weights = List.of(0.1, 0.5, 0.9);
    List<Integer> replications = List.of(5000);

    // Generar todas las combinaciones posibles
    List<Map<String, Object>> combinations = generateCombinations(weights, replications);

    // Resultados de todos los experimentos
    List<Map<String, Object>> allResults = new ArrayList<>();

    // Ejecutar todos los experimentos con las combinaciones generadas
    for (Map<String, Object> combination : combinations) {
      double w = (double) combination.get("w");
      int replicationCount = (int) combination.get("replications");

      ModifiedPrimSolver prim = new ModifiedPrimSolver(g, w);
      Graph<NetworkNode, NetworkEdge> mst = prim.findMST(servers, clients);

      Utils.saveExperimentDOTGraph(experimentName, mst);

      Map<String, Double> result = NetworkReliabilitySimulator.estimateReliability(mst, replicationCount);

      double totalCost = 0;
      for (NetworkEdge e : mst.edgeSet()) {
        totalCost += e.getCost();
      }

      Map<String, Object> experimentResult = new HashMap<>();
      experimentResult.put("parameters", combination);
      experimentResult.put("totalCost", totalCost);
      experimentResult.put("rlb", result.get("rlb"));

      allResults.add(experimentResult);

      // Mostrar resultados por cada combinación
      // System.out.println("Experiment with w=" + w + " and replications=" +
      // replicationCount);
      // System.out.println("Total cost: " + totalCost);
      // System.out.println("Reliability: " + result.get("rlb"));
    }

    // Al final, puedes guardar o analizar los resultados agregados
    // saveResults(allResults); // Implementa esta función si es necesario
    return allResults;
  }

  private static List<Map<String, Object>> generateCombinations(List<Double> weights, List<Integer> replications) {
    List<Map<String, Object>> combinations = new ArrayList<>();

    // Generar combinaciones de todos los parámetros posibles
    for (double w : weights) {
      for (int replicationCount : replications) {
        Map<String, Object> combination = new HashMap<>();
        combination.put("w", w);
        combination.put("replications", replicationCount);
        combinations.add(combination);
      }
    }

    return combinations;
  }
}
