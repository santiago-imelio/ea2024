package com.aegroupw;

import org.jgrapht.graph.DefaultWeightedEdge;

public class NetworkEdge extends DefaultWeightedEdge {
  private double cost;
  private double probability;

  public NetworkEdge(double cost, double probability) {
      this.cost = cost;
      this.probability = probability;
  }

  public double getCost() {
      return cost;
  }

  public double getProbability() {
      return probability;
  }

  @Override
  public String toString() {
      return "{" +
              "cost=" + cost +
              ", probability=" + probability +
              '}';
  }
}

