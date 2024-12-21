package com.aegroupw.graphgenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.BFSShortestPath;
import org.jgrapht.graph.SimpleGraph;

import com.aegroupw.network.NetworkEdge;
import com.aegroupw.network.NetworkNode;
import com.aegroupw.network.NetworkNodeType;

public class GraphGenerator {

    public static Graph<NetworkNode, NetworkEdge> generateConnectedGraph(int numServers, int numClients,
            int numComponents, double edgeProbability, double minReliability) {
        Graph<NetworkNode, NetworkEdge> graph = new SimpleGraph<>(NetworkEdge.class);
        Random random = new Random();

        // Create server nodes
        List<NetworkNode> servers = new ArrayList<>();
        for (int i = 0; i < numServers; i++) {
            NetworkNode server = new NetworkNode(i, NetworkNodeType.SERVER);
            servers.add(server);
            graph.addVertex(server);
        }

        // Create client nodes
        List<NetworkNode> clients = new ArrayList<>();
        for (int i = 0; i < numClients; i++) {
            NetworkNode client = new NetworkNode(numServers + i, NetworkNodeType.CLIENT);
            clients.add(client);
            graph.addVertex(client);
        }

        // Create component nodes
        List<NetworkNode> components = new ArrayList<>();
        for (int i = 0; i < numComponents; i++) {
            NetworkNode component = new NetworkNode(numServers + numClients + i, NetworkNodeType.COMPONENT);
            components.add(component);
            graph.addVertex(component);
        }

        // Each client connected to a server
        for (NetworkNode client : clients) {
            NetworkNode connectedServer = servers.get(random.nextInt(servers.size()));

            if (BFSShortestPath.findPathBetween(graph, client, connectedServer) == null) {
                NetworkNode current = client;

                // Add edges through random components to form a path
                for (int j = 0; j < components.size(); j++) {
                    NetworkNode next;
                    do {
                        next = components.get(random.nextInt(components.size()));
                    } while (next.equals(current)); // Ensure no loops

                    if (!graph.containsEdge(current, next)) {
                        double reliability = generateReliability(random, minReliability);
                        double cost = generateCost(reliability, random);
                        NetworkEdge edge = new NetworkEdge(graph.edgeSet().size(), cost, reliability);
                        graph.addEdge(current, next, edge);
                    }

                    current = next;

                    if (BFSShortestPath.findPathBetween(graph, current, connectedServer) != null) {
                        break;
                    }
                }

                if (!graph.containsEdge(current, connectedServer)) {
                    double reliability = generateReliability(random, minReliability);
                    double cost = generateCost(reliability, random);
                    NetworkEdge edge = new NetworkEdge(graph.edgeSet().size(), cost, reliability);
                    graph.addEdge(current, connectedServer, edge);
                }
            }
        }

        // Add random edges between all nodes
        List<NetworkNode> allNodes = new ArrayList<>(graph.vertexSet());
        for (int i = 0; i < allNodes.size(); i++) {
            for (int j = i + 1; j < allNodes.size(); j++) {
                if (random.nextDouble() < edgeProbability) {
                    NetworkNode node1 = allNodes.get(i);
                    NetworkNode node2 = allNodes.get(j);

                    if (!graph.containsEdge(node1, node2)) {
                        double reliability = generateReliability(random, minReliability);
                        double cost = generateCost(reliability, random);
                        NetworkEdge edge = new NetworkEdge(graph.edgeSet().size(), cost, reliability);
                        graph.addEdge(node1, node2, edge);
                    }
                }
            }
        }

        return graph;
    }

    private static double generateReliability(Random random, double minReliability) {
        double rndIncrement = random.nextDouble() * (1 - minReliability);
        return minReliability + rndIncrement;
    }

    private static double generateCost(double reliability, Random random) {
        double baseCost = reliability * 100;
        double randomnessFactor = 1 + (random.nextDouble() - 0.5) * 0.2;
        return baseCost * randomnessFactor;
    }
}
