package com.aegroupw.network;

import org.jgrapht.graph.DefaultWeightedEdge;

public class NetworkEdge extends DefaultWeightedEdge {
    private double cost;
    private double probability;
    private int number;

    public NetworkEdge(int number, double cost, double probability) {
        this.number = number;
        this.cost = cost;
        this.probability = probability;
    }

    public double getCost() {
        return cost;
    }

    public double getProbability() {
        return probability;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return "{" +
                "cost=" + cost +
                ", probability=" + probability +
                '}';
    }
}
