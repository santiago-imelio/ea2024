package com.aegroupw.experiments;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

import org.jgrapht.Graph;

import com.aegroupw.network.NetworkEdge;
import com.aegroupw.network.NetworkNode;
import com.aegroupw.utils.GraphParser;

public final class Utils {
  private Utils() {}

  public static final String experimentsDir =
    Paths.get("").toAbsolutePath().toString() + "/reports";

    public static void saveExperimentDOTGraph(String experimentName, Graph<NetworkNode, NetworkEdge> graph) {
      String outDir = Utils.experimentsDir + "/" + experimentName;

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
