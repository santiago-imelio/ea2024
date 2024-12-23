package com.aegroupw.experiments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jgrapht.Graph;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SinglePointCrossover;
import org.uma.jmetal.operator.crossover.impl.UniformCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.BitFlipMutation;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.solution.binarysolution.impl.DefaultBinarySolution;
import org.uma.jmetal.util.evaluator.impl.MultiThreadedSolutionListEvaluator;

import com.aegroupw.evolutionary.BinarizedNetworkSolution;
import com.aegroupw.evolutionary.NetworkOptimizationProblemNSGAII;
import com.aegroupw.network.NetworkEdge;
import com.aegroupw.network.NetworkNode;
import com.aegroupw.utils.GraphParser;

public class FullyConnectedNSGAII {

  public static List<Map<String, Object>> run(String experimentName) {
    if (experimentName == null || experimentName.trim().isEmpty()) {
        experimentName = "experiment" + new Random().nextInt(1000);
    }

    String edges = "graphs/fully_connected/edges.txt";
    String nodes = "graphs/fully_connected/nodes.txt";

    // Parametrizar el problema
    Map<String, List<Double>> problemParams = new HashMap<>();
    problemParams.put("networkDensity", List.of(0.6, 0.7, 0.8));
    problemParams.put("monteCarloReplications", List.of(5000.0));

    // Parametrizar los operadores y otros parámetros como antes
    Map<String, List<Double>> params = new HashMap<>();
    params.put("crossoverProbability", List.of(0.5, 0.7, 0.9));
    params.put("mutationProbability", List.of(0.2, 0.5));
    params.put("maxEvaluations", List.of(500.0, 1000.0, 2000.0));
    params.put("populationSize", List.of(100.0, 200.0));

    List<Map<String, Object>> allResults = new ArrayList<>();

    // Generar combinaciones de parámetros del problema
    for (Map<String, Double> problemCombination : generateCombinations(problemParams)) {
        double networkDensity = problemCombination.get("networkDensity");
        double monteCarloReplications = problemCombination.get("monteCarloReplications");

        // Cargar el grafo con diferentes configuraciones
        Graph<NetworkNode, NetworkEdge> g = GraphParser.parseGraphFromFile(edges, nodes);

        // Crear el problema con los parámetros de la combinación actual
        NetworkOptimizationProblemNSGAII problem = new NetworkOptimizationProblemNSGAII(
                g,
            networkDensity,
            (int) monteCarloReplications
        );

        // Generar combinaciones de los parámetros del algoritmo
        for (Map<String, Double> combination : generateCombinations(params)) {
            double crossoverProbability = combination.get("crossoverProbability");
            double mutationProbability = combination.get("mutationProbability");
            int maxEvaluations = combination.get("maxEvaluations").intValue();
            int populationSize = combination.get("populationSize").intValue();

            // Iterar sobre diferentes operadores de cruce
            List<CrossoverOperator<BinarizedNetworkSolution>> crossovers = List.of(
                    new SinglePointCrossover<>(crossoverProbability),
                    new UniformCrossover<>(crossoverProbability)
            );

            for (CrossoverOperator crossover : crossovers) {
              MutationOperator mutation = new BitFlipMutation<>(mutationProbability);

              NSGAII algo = new NSGAII<>(
                        problem,
                        maxEvaluations,
                        populationSize,
                        populationSize / 2,
                        populationSize / 2,
                        crossover,
                        mutation,
                        new BinaryTournamentSelection<>(),
                        new MultiThreadedSolutionListEvaluator<>(0)
                );

                algo.run();

                // Añadir resultados junto con parámetros usados
                Map<String, Object> resultData = new HashMap<>();
                resultData.put("problemParams", problemCombination);
                resultData.put("algorithmParams", combination);
                resultData.put("crossoverType", crossover.getClass().getSimpleName());
                resultData.put("solutions", algo.result());

                allResults.add(resultData);
            }
        }
    }

    return allResults;
  }

  private static List<Map<String, Double>> generateCombinations(Map<String, List<Double>> params) {
      List<Map<String, Double>> combinations = new ArrayList<>();
      List<String> keys = new ArrayList<>(params.keySet());

      int[] indices = new int[keys.size()];
      int totalCombinations = params.values().stream().mapToInt(List::size).reduce(1, Math::multiplyExact);

      for (int i = 0; i < totalCombinations; i++) {
          Map<String, Double> combination = new HashMap<>();
          for (int j = 0; j < keys.size(); j++) {
              String key = keys.get(j);
              combination.put(key, params.get(key).get(indices[j]));
          }
          combinations.add(combination);

          // Update indices
          for (int j = 0; j < indices.length; j++) {
              if (++indices[j] < params.get(keys.get(j)).size())
                  break;
              indices[j] = 0;
          }
      }
      return combinations;
  }
}
