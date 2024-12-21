package com.aegroupw;

import org.jgrapht.Graph;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SinglePointCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.BitFlipMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.util.evaluator.impl.MultiThreadedSolutionListEvaluator;

import com.aegroupw.evolutionary.BinarizedNetworkSolution;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import com.aegroupw.evolutionary.NetworkOptimizationProblem;
import com.aegroupw.network.NetworkEdge;
import com.aegroupw.network.NetworkNode;
import com.aegroupw.graphgenerator.GraphGenerator;

import com.aegroupw.evolutionary.CustomGeneticAlgorithm;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Generate a connected graph
        int numServers = 2;
        int numClients = 3;
        int numComponents = 10;
        double edgeProbability = 0.3;
        double minReliability = 0.7;

        Graph<NetworkNode, NetworkEdge> graph = GraphGenerator.generateConnectedGraph(
                numServers, numClients, numComponents, edgeProbability, minReliability
        );

        // Print graph details
        System.out.println("Generated Graph:");
        System.out.println("Nodes: " + graph.vertexSet());
        System.out.println("Edges: " + graph.edgeSet());

        // Define the optimization problem
        NetworkOptimizationProblem problem = new NetworkOptimizationProblem(
            graph,
            0.8,
            1000
        );


        // Define the operators
        double crossoverProbability = 0.5;
        double mutationProbability = 0.5;
        SelectionOperator<List<BinarySolution>, BinarySolution> selection = new BinaryTournamentSelection<>();
        CrossoverOperator<BinarySolution> crossover = new SinglePointCrossover<BinarySolution>(
                crossoverProbability);
        MutationOperator<BinarySolution> mutation = new BitFlipMutation<BinarySolution>(
                mutationProbability);

        // Define evaluators
                MultiThreadedSolutionListEvaluator<BinarySolution> multiThreadedEvaluator = new MultiThreadedSolutionListEvaluator<>(
                2);

        // ** Run the CustomGeneticAlgorithm **
        System.out.println("\nRunning CustomGeneticAlgorithm...");
        CustomGeneticAlgorithm customAlgo = new CustomGeneticAlgorithm(
                problem,
                2, // Máximo número de generaciones
                multiThreadedEvaluator, // El evaluador de soluciones
                selection, // El operador de selección
                crossover, // El operador de cruce
                mutation, // El operador de mutación
                500 // Tamaño de la población
        );

    // Execute the custom algorithm
    customAlgo.run();
    List<BinarySolution> customResult = customAlgo.result();

    // Print results from CustomGeneticAlgorithm
    System.out.println("CustomGeneticAlgorithm Result:");
    customResult.forEach(solution -> {
        System.out.println("Solution Objectives: " + solution.objectives()[0]);
    });

    // NSGAII algo = new NSGAII(
    // problem,
    // 2500,
    // 500,
    // 10,
    // 10,
    // cx,
    // mutation,
    // selection,
    // new MultiThreadedSolutionListEvaluator<BinarizedNetworkSolution>(2)
    // );

    // algo.run();

        // System.out.println(algo.result());
    }
}