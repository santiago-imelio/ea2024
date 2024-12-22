package com.aegroupw.experiments;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import org.jgrapht.Graph;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.operator.crossover.impl.SinglePointCrossover;
import org.uma.jmetal.operator.mutation.impl.BitFlipMutation;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.solution.binarysolution.impl.DefaultBinarySolution;
import org.uma.jmetal.util.evaluator.impl.MultiThreadedSolutionListEvaluator;

import com.aegroupw.evolutionary.BinarizedNetworkSolution;
import com.aegroupw.evolutionary.NetworkOptimizationProblem;
import com.aegroupw.graphgenerator.GraphGenerator;
import com.aegroupw.network.NetworkEdge;
import com.aegroupw.network.NetworkNode;
import com.aegroupw.utils.GraphParser;

public class FullyConnectedNSGAII {
  public static void run(String experimentName) {
    if (experimentName == null || experimentName.trim().isEmpty()) {
      experimentName = "experiment" + new Random().nextInt(1000);
    }

    String edges = "graphs/fully_connected/edges.txt";
    String nodes = "graphs/fully_connected/nodes.txt";

    Graph<NetworkNode,NetworkEdge> g = GraphParser.parseGraphFromFile(edges, nodes);

    Utils.saveExperimentDOTGraph(experimentName, g);

    NetworkOptimizationProblem problem = new NetworkOptimizationProblem(
        g,
        0.6,
        1000,
        0.0
    );

    // Define the operators
    SinglePointCrossover cx = new SinglePointCrossover<>(0.9);
    BitFlipMutation mutation = new BitFlipMutation<>(1.0 / problem.numberOfVariables());
    BinaryTournamentSelection<BinarizedNetworkSolution> selection = new BinaryTournamentSelection<>();

    NSGAII algo = new NSGAII(
        problem,
        2500,
        500,
        10,
        10,
        cx,
        mutation,
        selection,
        new MultiThreadedSolutionListEvaluator<BinarizedNetworkSolution>(2)
    );

    algo.run();
    List<DefaultBinarySolution> res = algo.result();

    int i = 1;
    for (DefaultBinarySolution sol : res) {
      double c = sol.objectives()[0];
      double r = 1 - sol.objectives()[1];
      Graph<NetworkNode, NetworkEdge> solGraph = problem.buildSubNetworkFromSolution(sol);
      Utils.saveExperimentDOTGraph(experimentName + "/sol" + i + "_" + c + "_" + r, solGraph);
      i++;
    }
  }
}
