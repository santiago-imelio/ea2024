package com.aegroupw.evolutionary;

import java.util.List;

import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;

public class CustomGeneticAlgorithm extends AbstractGeneticAlgorithm {
  public CustomGeneticAlgorithm(NetworkOptimizationProblem problem) {
    super(problem);

    this.crossoverOperator = crossoverOperator;
    this.mutationOperator = mutationOperator;
    this.selectionOperator = selectionOperator;
  }

  @Override
  public Object result() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'result'");
  }

  @Override
  public String name() {
    return "Custom GA";
  }

  @Override
  public String description() {
    return "";
  }

  @Override
  protected void initProgress() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'initProgress'");
  }

  @Override
  protected void updateProgress() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'updateProgress'");
  }

  @Override
  protected boolean isStoppingConditionReached() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'isStoppingConditionReached'");
  }

  @Override
  protected List<BinarizedNetworkSolution> evaluatePopulation(List population) {
    population = evaluator.evaluate(population, getProblem());

    return population;
  }

  @Override
  protected List replacement(List population, List offspringPopulation) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'replacement'");
  }
}
