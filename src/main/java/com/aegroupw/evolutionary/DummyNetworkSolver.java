package com.aegroupw.evolutionary;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.BFSShortestPath;

import com.aegroupw.network.NetworkEdge;
import com.aegroupw.network.NetworkNode;
import com.aegroupw.network.NetworkNodeType;

public class DummyNetworkSolver {
    public static Set<String> findDummySolution(Graph<NetworkNode, NetworkEdge> network) {
        Set<String> solutionEdges = new HashSet<>();

        Set<NetworkNode> servers = findNodesByType(network, NetworkNodeType.SERVER);
        Set<NetworkNode> clients = findNodesByType(network, NetworkNodeType.CLIENT);

        Set<NetworkNode> connectedClients = new HashSet<>();

        for (NetworkNode client : clients) {
            if (connectedClients.contains(client)) {
                continue;
            }

            List<NetworkEdge> path = null;

            for (NetworkNode server : servers) {
                GraphPath<NetworkNode, NetworkEdge> graphPath = BFSShortestPath.findPathBetween(network, client,
                        server);
                List<NetworkEdge> currentPath = graphPath.getEdgeList();
                if (currentPath != null && (path == null || currentPath.size() < path.size())) {
                    path = currentPath;
                }
            }

            if (path != null) {
                for (NetworkEdge edge : path) {
                    solutionEdges.add("Edge: " + edge + " connects " + edge.getSource() + " -> " + edge.getTarget());
                }

                connectedClients.add(client);
            } else {
                throw new IllegalStateException("No path found for client: " + client);
            }
        }

        return solutionEdges;
    }

    private static Set<NetworkNode> findNodesByType(Graph<NetworkNode, NetworkEdge> network, NetworkNodeType type) {
        Set<NetworkNode> nodes = new HashSet<>();

        for (NetworkNode node : network.vertexSet()) {
            if (node.getType() == type) {
                nodes.add(node);
            }
        }

        return nodes;
    }

}
