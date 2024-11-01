package com.aegroupw;

import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleGraph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class GraphParser {
    public static Graph<String, NetworkEdge> parseGraphFromFile(String filename) {
        Graph<String, NetworkEdge> graph = new SimpleGraph<>(NetworkEdge.class);

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length == 4) {
                    String vertex1 = parts[0];
                    String vertex2 = parts[1];
                    double cost = Double.parseDouble(parts[2]);
                    double probability = Double.parseDouble(parts[3]);

                    // Add vertices to the graph
                    graph.addVertex(vertex1);
                    graph.addVertex(vertex2);

                    // Add edge with weight
                    NetworkEdge edge = new NetworkEdge(cost, probability);
                    graph.addEdge(vertex1, vertex2, edge);
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
