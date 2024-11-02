package com.aegroupw;

import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleGraph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GraphParser {
    public static Graph<NetworkNode, NetworkEdge> parseGraphFromFile(String edgesFilename, String vertexFilename) {
        Graph<NetworkNode, NetworkEdge> graph = new SimpleGraph<>(NetworkEdge.class);

        List<NetworkNode> vertices = new ArrayList<NetworkNode>();
        try (BufferedReader vertexFile = new BufferedReader(new FileReader(vertexFilename))) {
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

        try (BufferedReader edgesFile = new BufferedReader(new FileReader(edgesFilename))) {
            String line;
            while ((line = edgesFile.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length == 4) {
                    Integer vertex1 = Integer.parseInt(parts[0]);
                    Integer vertex2 = Integer.parseInt(parts[1]);

                    double cost = Double.parseDouble(parts[2]);
                    double probability = Double.parseDouble(parts[3]);

                    NetworkNode v1 = vertices.get(vertex1);
                    NetworkNode v2 = vertices.get(vertex2);

                    // Add vertices to the graph
                    graph.addVertex(v1);
                    graph.addVertex(v2);

                    // Add edge with weight
                    NetworkEdge edge = new NetworkEdge(cost, probability);
                    graph.addEdge(v1, v2, edge);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.err.println("Invalid weight format: " + e.getMessage());
        }

        return graph;
    }
}
