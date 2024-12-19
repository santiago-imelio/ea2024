package com.aegroupw;

import org.jgrapht.Graph;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.operator.crossover.impl.SinglePointCrossover;
import org.uma.jmetal.operator.mutation.impl.BitFlipMutation;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.util.evaluator.impl.MultiThreadedSolutionListEvaluator;

import com.aegroupw.evolutionary.BinarizedNetworkSolution;
import com.aegroupw.evolutionary.NetworkOptimizationProblem;
import com.aegroupw.network.NetworkEdge;
import com.aegroupw.network.NetworkNode;
import com.aegroupw.graphgenerator.GraphGenerator;;

public class Main {
    public static void main(String[] args) {
        // Generate a connected graph
        int numServers = 2;
        int numClients = 3;
        int numComponents = 10;
        double edgeProbability = 0.3;

        Graph<NetworkNode, NetworkEdge> graph = GraphGenerator.generateConnectedGraph(
            numServers, numClients, numComponents, edgeProbability
        );

        // Print 
        System.out.println("Generated Graph:");
        System.out.println("Nodes: " + graph.vertexSet());
        System.out.println("Edges: " + graph.edgeSet());

        NetworkOptimizationProblem problem = new NetworkOptimizationProblem(
            graph,
            0.8,
            100
        );

        // Define the operators
        SinglePointCrossover cx = new SinglePointCrossover<>(0.9);
        BitFlipMutation mutation = new BitFlipMutation<>(1.0 / problem.numberOfVariables());
        BinaryTournamentSelection<BinarizedNetworkSolution> selection = new BinaryTournamentSelection<>();

        NSGAII algo = new NSGAII(
            problem,
                500,
                    50,
            10,
            10,
            cx,
            mutation,
            selection,
            new MultiThreadedSolutionListEvaluator<BinarizedNetworkSolution>(2)
        );

        algo.run();

        System.out.println(algo.result());
    }
}