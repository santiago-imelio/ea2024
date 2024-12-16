package com.aegroupw.evolutionary;
import java.util.List;

import org.uma.jmetal.solution.binarysolution.impl.DefaultBinarySolution;
import org.uma.jmetal.util.binarySet.BinarySet;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

public class BinarizedNetworkSolution extends DefaultBinarySolution {
  public BinarizedNetworkSolution(List<Integer> bitsPerVariable, int numberOfObjectives, double proba) {
    super(bitsPerVariable, numberOfObjectives, 0);

    initializeBinaryVariables(JMetalRandom.getInstance(), proba);
  }

  private static BinarySet createNewBinarySet(int numberOfBits, double proba, JMetalRandom randomGenerator) {
    BinarySet bitSet = new BinarySet(numberOfBits);

    for (int i = 0; i < numberOfBits; i++) {
      double rnd = randomGenerator.nextDouble();
      if (rnd < proba) {
        bitSet.set(i);
      } else {
        bitSet.clear(i);
      }
    }
    return bitSet;
  }

  private void initializeBinaryVariables(JMetalRandom randomGenerator, double proba) {
    for (int i = 0; i < variables().size(); i++) {
      variables().set(i, createNewBinarySet(numberOfBitsPerVariable.get(i), proba, randomGenerator));
    }
  }
}
