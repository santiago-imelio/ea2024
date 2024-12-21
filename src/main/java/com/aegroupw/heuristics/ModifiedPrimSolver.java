package com.aegroupw.heuristics;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleGraph;

import com.aegroupw.network.NetworkEdge;
import com.aegroupw.network.NetworkNode;

public class ModifiedPrimSolver {
    public static Graph<NetworkNode,NetworkEdge> findMST(
            Graph<NetworkNode, NetworkEdge> graph,
            Set<NetworkNode> servers,
            Set<NetworkNode> clients,
            double alpha,
            double beta) {

        // Create a priority queue to select the next edge
        PriorityQueue<NetworkEdge> pq = new PriorityQueue<>(
                Comparator.comparingDouble(e -> e.score()));

        Set<NetworkNode> inMST = new HashSet<>();
        Set<NetworkEdge> selectedEdges = new HashSet<>();

        // Start Prim's algorithm from any server (pick server 1 as an example)
        NetworkNode startVertex = servers.iterator().next();
        inMST.add(startVertex);

        // Add all edges connected to the start vertex to the priority queue
        for (NetworkEdge edge : graph.edgesOf(startVertex)) {
            pq.offer(edge);
        }

        while (!pq.isEmpty()) {
            NetworkEdge edge = pq.poll();
            NetworkNode source = graph.getEdgeSource(edge);
            NetworkNode target = graph.getEdgeTarget(edge);

            // Skip the edge if both vertices are already in the MST (cycle detected)
            if (inMST.contains(source) && inMST.contains(target)) {
                continue;
            }

            // Add the edge to the MST
            selectedEdges.add(edge);
            NetworkNode newVertex = inMST.contains(source) ? target : source;
            inMST.add(newVertex);

            // Add new edges from the newly added vertex to the priority queue
            for (NetworkEdge newEdge : graph.edgesOf(newVertex)) {
                if (!inMST.contains(graph.getEdgeSource(newEdge)) || !inMST.contains(graph.getEdgeTarget(newEdge))) {
                    pq.offer(newEdge);
                }
            }

            // Check if all clients are connected to at least one server
            if (areClientsConnectedToServers(clients, servers, inMST)) {
                break;
            }
        }

        Graph<NetworkNode, NetworkEdge> mst = buildMSTFromEdgeSet(graph, selectedEdges);

        return mst;
    }

    // Method to check if all clients are connected to at least one server
    private static boolean areClientsConnectedToServers(Set<NetworkNode> clients, Set<NetworkNode> servers, Set<NetworkNode> inMST) {
        for (NetworkNode client : clients) {
            boolean connectedToServer = false;
            for (NetworkNode server : servers) {
                if (inMST.contains(client) && inMST.contains(server)) {
                    connectedToServer = true;
                    break;
                }
            }
            if (!connectedToServer) {
                return false;
            }
        }
        return true;
    }

    private static Graph<NetworkNode, NetworkEdge> buildMSTFromEdgeSet(
      Graph<NetworkNode, NetworkEdge> g,
      Set<NetworkEdge> edgesInMST
    ) {
        Set<NetworkEdge> edgeSet = g.edgeSet();

        Graph<NetworkNode,NetworkEdge> mst = new SimpleGraph<NetworkNode,NetworkEdge>(NetworkEdge.class);

        for (NetworkEdge e : edgeSet) {
          NetworkNode v1 = e.getSource();
          NetworkNode v2 = e.getTarget();

          NetworkNode v1Copy = new NetworkNode(v1.getNumber(), v1.getType());
          NetworkNode v2Copy = new NetworkNode(v2.getNumber(), v2.getType());

          mst.addVertex(v1Copy);
          mst.addVertex(v2Copy);

          if (edgesInMST.contains(e)) {
            NetworkEdge eCopy = new NetworkEdge(e.getNumber(), e.getCost(), e.getProbability());
            mst.addEdge(v1Copy, v2Copy, eCopy);
          }
        }

        return mst;
      }
}
