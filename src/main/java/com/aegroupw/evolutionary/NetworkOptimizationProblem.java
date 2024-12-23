package com.aegroupw.evolutionary;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleGraph;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.binarysolution.BinarySolution;

import com.aegroupw.network.NetworkEdge;
import com.aegroupw.network.NetworkNode;
import com.aegroupw.montecarlo.NetworkReliabilitySimulator;

public class NetworkOptimizationProblem implements Problem<BinarizedNetworkSolution> {
  /** The network we want to optimize */
  private Graph<NetworkNode, NetworkEdge> network;

  /** We use these for normalizing costs */
  private double totalCost;

  /** Problem Parameters */

  /** Probability of an edge being in a solution */
  private double edgeProbability;

  /** Number of replications of Monte Carlo simulation to estimate network reliability */
  private int monteCarloReplications;

  /** Weight to evaluate fitness of a solution to a single value */
  private double w;

  public NetworkOptimizationProblem(
    Graph<NetworkNode, NetworkEdge> network,
    double edgeProbability,
    int monteCarloReplications,
    double weight
  ) {
    this.network = network;
    this.edgeProbability = edgeProbability;
    this.monteCarloReplications = monteCarloReplications;
    this.w = weight;

    for (NetworkEdge e : network.edgeSet()) {
      this.totalCost += e.getCost();
    }
  }

  // @Override
  public List<Integer> numberOfBitsPerVariable() {
    List<Integer> res = new ArrayList<>();

    for (int i = 0; i < this.numberOfVariables(); i++) {
      res.add(1);
    }

    return res;
  }

  @Override
  public int numberOfVariables() {
    return network.edgeSet().size();
  }

  @Override
  public int numberOfObjectives() {
    return 2;
  }

  @Override
  public int numberOfConstraints() {
    return 0;
  }

  @Override
  public String name() {
    return "Network Cost & Reliability Optimization";
  }

  @Override
  public BinarizedNetworkSolution createSolution() {
    return new BinarizedNetworkSolution(numberOfBitsPerVariable(), numberOfObjectives(), this.edgeProbability);
  }

  @Override
  public BinarizedNetworkSolution evaluate(BinarizedNetworkSolution solution) {
    Graph<NetworkNode, NetworkEdge> subNetwork = this.buildSubNetworkFromSolution(solution);

    // if the solution is not connected, penalize
    if (!NetworkReliabilitySimulator.isNetworkConnected(subNetwork)) {
      solution.objectives()[0] = Integer.MAX_VALUE;
      solution.objectives()[1] = Integer.MAX_VALUE;

      return solution;
    };

    int totalCost = 0;
    for (NetworkEdge e : subNetwork.edgeSet()) {
      totalCost += e.getCost();
    }

    Map<String, Double> mcResults = NetworkReliabilitySimulator.estimateReliability(
      subNetwork,
      this.monteCarloReplications
    );

    Double reliability = mcResults.get("rlb");

    solution.objectives()[0] = totalCost;
    solution.objectives()[1] = 1 - reliability;

    return solution;
  }

  public Graph<NetworkNode, NetworkEdge> buildSubNetworkFromSolution(BinarySolution solution) {
    Graph<NetworkNode, NetworkEdge> g = this.network;
    Set<NetworkEdge> edgeSet = g.edgeSet();

    Graph<NetworkNode, NetworkEdge> gCopy = new SimpleGraph<NetworkNode,NetworkEdge>(NetworkEdge.class);

    for (NetworkEdge e : edgeSet) {
      NetworkNode v1 = e.getSource();
      NetworkNode v2 = e.getTarget();

      NetworkNode v1Copy = new NetworkNode(v1.getNumber(), v1.getType());
      NetworkNode v2Copy = new NetworkNode(v2.getNumber(), v2.getType());

      gCopy.addVertex(v1Copy);
      gCopy.addVertex(v2Copy);

      int edgeNum = e.getNumber();

      BitSet bitset = solution.variables().get(edgeNum);
      boolean isEdgeInSolution = bitset.get(0);

      if (isEdgeInSolution) {
        NetworkEdge eCopy = new NetworkEdge(e.getNumber(), e.getCost(), e.getProbability());
        gCopy.addEdge(v1Copy, v2Copy, eCopy);
      }
    }

    return gCopy;
  }

  public double solutionWeightedFitness(BinarizedNetworkSolution solution) {
    double costGraph = this.totalCost;
    double totalCost = solution.objectives()[0];
    double antiRlb = solution.objectives()[1];
    double normalizedTotalCost = totalCost / costGraph;

    return w * normalizedTotalCost - (1 - w) * Math.log(antiRlb);
  }

  public double getEdgeProbability() {
    return edgeProbability;
  }
}
