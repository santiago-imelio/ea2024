package com.aegroupw;

import com.aegroupw.experiments.GreedyExperiment;
import com.aegroupw.experiments.FullyConnectedNSGAII;
import com.aegroupw.experiments.FullyConnectedCustom;

public class Main {
    public static void main(String[] args) {
        // Run NSGAII Algorithm Experiment
        System.out.println("\nRunning CustomGeneticAlgorithm...");
        System.out.println(FullyConnectedNSGAII.run(null));

        // Run Greedy Experiment
        System.out.println("\nRunning GreedyExperiment...");
        System.out.println(GreedyExperiment.run(null));

        // Run Fully Connected Custom Experiment
        System.out.println("\nRunning FullyConnectedCustom...");
        System.out.println(FullyConnectedCustom.run(null));

    }
}
