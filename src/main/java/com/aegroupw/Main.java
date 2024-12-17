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
import com.aegroupw.utils.GraphParser;

public class Main {
    public static void main(String[] args) {
        String vertexFile = "graphs/fully_connected/nodes.txt";
        String edgesFile = "graphs/fully_connected/edges.txt";

        Graph<NetworkNode, NetworkEdge> graph = GraphParser.parseGraphFromFile(edgesFile, vertexFile);

        NetworkOptimizationProblem problem = new NetworkOptimizationProblem(
            graph,
            0.8,
            1000
        );

        SinglePointCrossover cx = new SinglePointCrossover<>(0.9);
        BitFlipMutation mutation = new BitFlipMutation<>(1.0 / problem.numberOfVariables());
        BinaryTournamentSelection<BinarizedNetworkSolution> selection = new BinaryTournamentSelection<>();

        NSGAII algo = new NSGAII(
            problem,
            2500,
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