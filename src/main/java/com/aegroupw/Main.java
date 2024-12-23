package com.aegroupw;

import com.aegroupw.experiments.GreedyExperiment;
import com.aegroupw.experiments.FullyConnectedNSGAII;
import com.aegroupw.experiments.FullyConnectedCustom;

public class Main {
    public static void main(String[] args) {

        // Run Fully Connected Custom Experiment
        System.out.println("\nRunning FullyConnectedCustom...");
        System.out.println(FullyConnectedCustom.run(null));

    }
}
