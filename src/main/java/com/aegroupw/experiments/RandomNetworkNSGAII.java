package com.aegroupw.experiments;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleGraph;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.operator.crossover.impl.SinglePointCrossover;
import org.uma.jmetal.operator.mutation.impl.BitFlipMutation;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.util.evaluator.impl.MultiThreadedSolutionListEvaluator;

import com.aegroupw.evolutionary.BinarizedNetworkSolution;
import com.aegroupw.evolutionary.NetworkOptimizationProblem;
import com.aegroupw.graphgenerator.GraphGenerator;
import com.aegroupw.network.NetworkEdge;
import com.aegroupw.network.NetworkNode;
import com.aegroupw.utils.GraphParser;

public class RandomNetworkNSGAII {
  public static void run(String experimentName) {
    if (experimentName == null || experimentName.trim().isEmpty()) {
      experimentName = "experiment" + new Random().nextInt(1000);
    }

    // Generate a connected graph
    int numServers = 2;
    int numClients = 3;
    int numComponents = 10;
    double edgeProbability = 0.3;
    double minReliability = 0.7;

    Graph<NetworkNode, NetworkEdge> graph = GraphGenerator.generateConnectedGraph(
            numServers, numClients, numComponents, edgeProbability, minReliability
    );

    saveExperimentDOTGraph(experimentName, graph);

    NetworkOptimizationProblem problem = new NetworkOptimizationProblem(
        graph,
        0.8,
        1000
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

    System.out.println(algo.result());
  }

  public static void saveExperimentDOTGraph(String experimentName, Graph<NetworkNode, NetworkEdge> graph) {
    String outDir = Constants.experimentsDir + "/" + experimentName;

    try {
      File dir = new File(outDir);

      if (!dir.exists()) {
        dir.mkdirs();
      }

      String dotRepresentation = GraphParser.networkToDOT(graph);
      BufferedWriter writer = new BufferedWriter(new FileWriter(outDir + "/graph.txt"));
      writer.write(dotRepresentation);
      writer.close();
    } catch (IOException e) {
      System.out.println("Problem generating dot file for graph");
      System.out.println(e);
    }
  }
}