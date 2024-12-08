package com.aegroupw.evolutionary;
import java.util.List;

import org.uma.jmetal.solution.binarysolution.impl.DefaultBinarySolution;
import org.uma.jmetal.util.binarySet.BinarySet;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

public class BinarizedNetworkSolution extends DefaultBinarySolution {
  public BinarizedNetworkSolution(List<Integer> bitsPerVariable, int numberOfObjectives) {
    super(bitsPerVariable, numberOfObjectives, 0);

    initializeBinaryVariables(JMetalRandom.getInstance());
  }

  private static BinarySet createNewBinarySet(int numberOfBits, JMetalRandom randomGenerator) {
    BinarySet bitSet = new BinarySet(numberOfBits);

    for (int i = 0; i < numberOfBits; i++) {
      double rnd = randomGenerator.nextDouble();
      if (rnd < 0.95) { // TODO: parameterize this probability
        bitSet.set(i);
      } else {
        bitSet.clear(i);
      }
    }
    return bitSet;
  }

  private void initializeBinaryVariables(JMetalRandom randomGenerator) {
    for (int i = 0; i < variables().size(); i++) {
      variables().set(i, createNewBinarySet(numberOfBitsPerVariable.get(i), randomGenerator));
    }
  }
}
