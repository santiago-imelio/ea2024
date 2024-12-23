package com.aegroupw.experiments;

import org.jgrapht.Graph;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SinglePointCrossover;
import org.uma.jmetal.operator.crossover.impl.UniformCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.BitFlipMutation;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.solution.binarysolution.impl.DefaultBinarySolution;
import org.uma.jmetal.util.evaluator.impl.MultiThreadedSolutionListEvaluator;
import com.aegroupw.evolutionary.BinarizedNetworkSolution;
import com.aegroupw.evolutionary.NetworkOptimizationProblem;
import com.aegroupw.network.NetworkEdge;
import com.aegroupw.network.NetworkNode;
import com.aegroupw.utils.GraphParser;

import com.aegroupw.evolutionary.CustomGeneticAlgorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class FullyConnectedCustom {

    public static Map<Map<String, Double>, List<DefaultBinarySolution>> run(String experimentName) {
        if (experimentName == null || experimentName.trim().isEmpty()) {
            experimentName = "experiment" + new Random().nextInt(1000);
        }

        String edges = "graphs/fully_connected/edges.txt";
        String nodes = "graphs/fully_connected/nodes.txt";

        // Parametrizar el problema
        Map<String, List<Double>> problemParams = new HashMap<>();
        problemParams.put("edgeProbability", List.of(0.6, 0.7, 0.8));
        problemParams.put("monteCarloReplications", List.of(5000.0));
        problemParams.put("weight", List.of(0.1, 0.5, 0.9));

        // Parametrizar los operadores y otros parámetros como antes
        Map<String, List<Double>> params = new HashMap<>();
        params.put("crossoverProbability", List.of(0.7, 0.8, 0.9));
        params.put("mutationProbability", List.of(0.1, 0.2, 0.3));
        params.put("maxGenerations", List.of(500.0, 1000.0, 2000.0));
        params.put("populationSize", List.of(100.0, 200.0));

        // Almacenar resultados asociados con los parámetros
        Map<Map<String, Double>, List<DefaultBinarySolution>> resultsWithParams = new HashMap<>();

        // Generar combinaciones de parámetros del problema
        for (Map<String, Double> problemCombination : generateCombinations(problemParams)) {
            double edgeProbability = problemCombination.get("edgeProbability");
            double monteCarloReplications = problemCombination.get("monteCarloReplications");
            double weight = problemCombination.get("weight");

            // Cargar el grafo con diferentes configuraciones
            Graph<NetworkNode, NetworkEdge> g = GraphParser.parseGraphFromFile(edges, nodes);

            // Crear el problema con los parámetros de la combinación actual
            NetworkOptimizationProblem problem = new NetworkOptimizationProblem(
                    g,
                    edgeProbability, // Probabilidad de borde
                    (int) monteCarloReplications, // Replicaciones de Monte Carlo
                    weight // Peso
            );

            // Generar combinaciones de los parámetros del algoritmo
            for (Map<String, Double> combination : generateCombinations(params)) {
                double crossoverProbability = combination.get("crossoverProbability");
                double mutationProbability = combination.get("mutationProbability");
                int maxGenerations = combination.get("maxGenerations").intValue();
                int populationSize = combination.get("populationSize").intValue();

                // Iterar sobre diferentes operadores de cruce
                List<CrossoverOperator<BinarizedNetworkSolution>> crossovers = List.of(
                        new SinglePointCrossover<>(crossoverProbability),
                        new UniformCrossover<>(crossoverProbability));

                for (CrossoverOperator<BinarizedNetworkSolution> crossover : crossovers) {
                    MutationOperator<BinarizedNetworkSolution> mutation = new BitFlipMutation<>(mutationProbability);

                    CustomGeneticAlgorithm customAlgo = new CustomGeneticAlgorithm(
                            problem,
                            maxGenerations,
                            new MultiThreadedSolutionListEvaluator<>(0),
                            new BinaryTournamentSelection<>(),
                            crossover,
                            mutation,
                            populationSize);

                    customAlgo.run();

                    // Crear una combinación completa de parámetros
                    Map<String, Double> fullCombination = new HashMap<>(problemCombination);
                    fullCombination.putAll(combination);

                    // Guardar resultados
                    resultsWithParams.put(fullCombination, new ArrayList<>(customAlgo.result()));
                }
            }
        }

        return resultsWithParams;
    }

    private static List<Map<String, Double>> generateCombinations(Map<String, List<Double>> params) {
        List<Map<String, Double>> combinations = new ArrayList<>();
        List<String> keys = new ArrayList<>(params.keySet());

        // Creamos un array de índices para cada parámetro
        int[] indices = new int[keys.size()];
        int totalCombinations = params.values().stream().mapToInt(List::size).reduce(1, Math::multiplyExact);

        // Generar las combinaciones
        for (int i = 0; i < totalCombinations; i++) {
            Map<String, Double> combination = new HashMap<>();
            for (int j = 0; j < keys.size(); j++) {
                String key = keys.get(j);
                combination.put(key, params.get(key).get(indices[j]));
            }
            combinations.add(combination);

            for (int j = 0; j < indices.length; j++) {
                if (++indices[j] < params.get(keys.get(j)).size()) {
                    break;
                }
                indices[j] = 0;
            }
        }
        return combinations;
    }
}
