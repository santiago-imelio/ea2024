package com.aegroupw.utils;

import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;

import com.aegroupw.network.NetworkEdge;
import com.aegroupw.network.NetworkNode;
import com.aegroupw.network.NetworkNodeType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
            int i = 0;
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
                    NetworkEdge edge = new NetworkEdge(i, cost, probability);
                    graph.addEdge(v1, v2, edge);

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

    public static String networkToDOT(Graph<NetworkNode, NetworkEdge> net) {
        DOTExporter<NetworkNode, NetworkEdge> exporter =
            new DOTExporter<>(v ->Integer.toString(v.getNumber()));

        exporter.setVertexAttributeProvider((v) -> {
            Map<String, Attribute> map = new LinkedHashMap<>();

            if (v.getType() != NetworkNodeType.COMPONENT) {
                String label = v.getType().toString() + ' ' + Integer.toString(v.getNumber());
                map.put("label", DefaultAttribute.createAttribute(label));
            }

            return map;
        });

        exporter.setEdgeAttributeProvider((e) -> {
            Map<String, Attribute> map = new LinkedHashMap<>();

            String label =
                "{"
                + Double.toString(e.getCost())
                + " "
                + "//"
                + " "
                + Double.toString(e.getProbability())
                + "}";

            map.put("label", DefaultAttribute.createAttribute(label));
            return map;
        });

        Writer writer = new StringWriter();
        exporter.exportGraph(net, writer);

        return writer.toString();
    }
}
