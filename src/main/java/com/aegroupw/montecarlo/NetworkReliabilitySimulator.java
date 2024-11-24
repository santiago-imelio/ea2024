package com.aegroupw.montecarlo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.BFSShortestPath;
import org.jgrapht.graph.SimpleGraph;

import com.aegroupw.network.NetworkEdge;
import com.aegroupw.network.NetworkNode;
import com.aegroupw.network.NetworkNodeType;

import java.util.Random;
import java.util.Set;

public class NetworkReliabilitySimulator {
  /**
   * Estimates reliability of the network using Monte Carlo simulation.
   * @param edgesFile graph edges file path
   * @param vertexFile graph vertex file path
   */
  public static Map<String, Double> estimateReliability(Graph<NetworkNode, NetworkEdge> network, int replications) {
    long startTime = System.nanoTime();

    System.out.println(
      "Starting Monte Carlo simulation " + "(" + replications + " replications)"
    );

    Random rnd = new Random();

    int sum = 0;
    double variance = 0;

    for (int i = 0; i < replications; i++) {
      Graph<NetworkNode, NetworkEdge> gConfig = configureRandomNetwork(network, rnd);

      int x = 0;
      if (isNetworkConnected(gConfig)) {
        x = 1;
      }

      sum += x;
      variance += x * x;
    }

    // log execution time
    long endTime = System.nanoTime();
    long duration = (endTime - startTime) / 1000000;

    System.out.println("Execution time for simulation: " + duration + " ms");

    Double q = (double)sum / replications;
    variance = (variance / replications - q * q)/(replications - 1);
    Double std = Math.sqrt(variance);

    Map<String, Double> results = new HashMap<String,Double>();

    results.put("rlb", q);
    results.put("variance", variance);
    results.put("std", std);
    results.put("rel_error", std / q);

    return results;
  }

  static Set<NetworkNode> findServerNodes(Graph<NetworkNode, NetworkEdge> g) {
    Set<NetworkNode> servers = new HashSet<>();
    Set<NetworkNode> vertexSet = g.vertexSet();

    for (NetworkNode n : vertexSet) {
      if (n.getType() == NetworkNodeType.SERVER) {
        servers.add(n);
      }
    }

    return servers;
  }

  static Set<NetworkNode> findClientNodes(Graph<NetworkNode, NetworkEdge> g) {
    Set<NetworkNode> clients = new HashSet<>();
    Set<NetworkNode> vertexSet = g.vertexSet();

    for (NetworkNode n : vertexSet) {
      if (n.getType() == NetworkNodeType.CLIENT) {
        clients.add(n);
      }
    }

    return clients;
  }

  /**
   * Creates a deep copy of the given network. Selects edges based on their probability.
   * @param g network
   * @param rnd random number generator
   * @return deep copy of a graph that might not have all the edges of the original graph.
   */
  static Graph<NetworkNode, NetworkEdge> configureRandomNetwork(Graph<NetworkNode, NetworkEdge> g, Random rnd) {
    Set<NetworkEdge> edgeSet = g.edgeSet();

    Graph<NetworkNode,NetworkEdge> gCopy = new SimpleGraph<NetworkNode,NetworkEdge>(NetworkEdge.class);

    for (NetworkEdge e : edgeSet) {
      NetworkNode v1 = e.getSource();
      NetworkNode v2 = e.getTarget();

      NetworkNode v1Copy = new NetworkNode(v1.getNumber(), v1.getType());
      NetworkNode v2Copy = new NetworkNode(v2.getNumber(), v2.getType());

      gCopy.addVertex(v1Copy);
      gCopy.addVertex(v2Copy);

      if (isEdgeOperational(e, rnd)) {
        NetworkEdge eCopy = new NetworkEdge(e.getNumber(), e.getCost(), e.getProbability());
        gCopy.addEdge(v1Copy, v2Copy, eCopy);
      }
    }

    return gCopy;
  }

  /**
   * Returns true if each client is connected to at least one server using BFS.
   * @param g
   * @return
   */
  static boolean isNetworkConnected(Graph<NetworkNode, NetworkEdge> g) {
    Set<NetworkNode> servers = findServerNodes(g);
    Set<NetworkNode> clients = findClientNodes(g);

    int connectedClients = 0;

    // store client to server assignments
    Map<NetworkNode, NetworkNode> clientServerMap = new HashMap<>();

    for (NetworkNode s : servers) {
      for (NetworkNode c : clients) {
        if (clientServerMap.get(c) == null && BFSShortestPath.findPathBetween(g, s, c) != null) {
          clientServerMap.put(c, s);
          connectedClients++;
        }
      }
    }

    return connectedClients == clients.size();
  }

  static boolean isEdgeOperational(NetworkEdge e, Random rnd) {
    return rnd.nextDouble(1.0) < e.getProbability();
  }
}
