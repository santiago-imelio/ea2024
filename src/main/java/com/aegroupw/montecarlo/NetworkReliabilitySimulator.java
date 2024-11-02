package com.aegroupw.montecarlo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleGraph;

import com.aegroupw.network.NetworkEdge;
import com.aegroupw.network.NetworkNode;
import com.aegroupw.network.NetworkNodeType;

import java.util.Random;

public class NetworkReliabilitySimulator {
  /**
   * Estimates reliability of the network using Monte Carlo simulation.
   * @param edgesFile graph edges file path
   * @param vertexFile graph vertex file path
   */
  public static void estimateReliability(String edgesFile, String vertexFile, int replications) {

    for (int i = 0; i < replications; i++) {
      Graph<NetworkNode, NetworkEdge> g = buildRandomGraph(edgesFile, vertexFile);

      // TODO: get clients and server first to check connectivity on graph g
    }
  }

  /**
   * Builds a graph with randomly selected edges, based on their probability.
   * @param edgesFileName
   * @param vertexFileName
   * @return
   */
  private static Graph<NetworkNode, NetworkEdge> buildRandomGraph(String edgesFileName, String vertexFileName) {
        Graph<NetworkNode, NetworkEdge> graph = new SimpleGraph<>(NetworkEdge.class);
        Random rand = new Random();

        List<NetworkNode> vertices = new ArrayList<NetworkNode>();
        try (BufferedReader vertexFile = new BufferedReader(new FileReader(vertexFileName))) {
            String line;
            while ((line = vertexFile.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                Integer vertex = Integer.parseInt(parts[0]);
                NetworkNodeType type = NetworkNodeType.valueOf(parts[1]);
                NetworkNode node = new NetworkNode(vertex, type);

                // put node at index equal to vertex number
                vertices.add(vertex, node);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.err.println("Invalid weight format: " + e.getMessage());
        }

        try (BufferedReader edgesFile = new BufferedReader(new FileReader(edgesFileName))) {
            String line;
            int i = 0;
            while ((line = edgesFile.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length == 4) {
                    Integer vertex1 = Integer.parseInt(parts[0]);
                    Integer vertex2 = Integer.parseInt(parts[1]);

                    NetworkNode v1 = vertices.get(vertex1);
                    NetworkNode v2 = vertices.get(vertex2);

                    graph.addVertex(v1);
                    graph.addVertex(v2);

                    double reliability = Double.parseDouble(parts[3]);

                    if (isEdgeOperational(reliability, rand)) {
                      double cost = Double.parseDouble(parts[2]);
                      NetworkEdge edge = new NetworkEdge(i, cost, reliability);
                      graph.addEdge(v1, v2, edge);
                    }

                    i++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.err.println("Invalid weight format: " + e.getMessage());
        }

        return graph;
  }

  private static boolean isEdgeOperational(double edgeReliability, Random rnd) {
    return rnd.nextDouble(1.0) < edgeReliability;
  }
}
