package com.aegroupw.evolutionary;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.solution.binarysolution.BinarySolution;

public class CustomGeneticAlgorithm
    extends AbstractGeneticAlgorithm<BinarySolution, List<BinarySolution>> {

  private final SolutionListEvaluator<BinarySolution> evaluator;
  private int maxGenerations;
  private int currentGeneration;
  private int maxPopulationSize;

  public CustomGeneticAlgorithm(
      Problem<BinarySolution> problem,
      int maxGenerations,
      SolutionListEvaluator<BinarySolution> evaluator,
      SelectionOperator<List<BinarySolution>, BinarySolution> selectionOperator,
      CrossoverOperator<BinarySolution> crossoverOperator,
      MutationOperator<BinarySolution> mutationOperator, int maxPopulationSize) {
    super(problem);
    this.evaluator = evaluator;
    this.maxGenerations = maxGenerations;
    this.maxPopulationSize = maxPopulationSize;
    this.currentGeneration = 0;

    // Configura los operadores
    this.selectionOperator = selectionOperator;
    this.crossoverOperator = crossoverOperator;
    this.mutationOperator = mutationOperator;
    setMaxPopulationSize(maxPopulationSize);
  }

  @Override
  protected void initProgress() {
    currentGeneration = 0;
  }

  @Override
  protected void updateProgress() {
    currentGeneration++;
  }

  @Override
  protected boolean isStoppingConditionReached() {
    return currentGeneration >= maxGenerations;
  }

  @Override
  protected List<BinarySolution> evaluatePopulation(List<BinarySolution> population) {
    return evaluator.evaluate(population, getProblem());
  }

  @Override
  protected List<BinarySolution> replacement(
      List<BinarySolution> population,
      List<BinarySolution> offspringPopulation) {

    List<BinarySolution> mutablePopulation = new ArrayList<>(population);
    mutablePopulation.addAll(offspringPopulation);
    return mutablePopulation.stream()
        .sorted((s1, s2) -> Double.compare(s1.objectives()[0], s2.objectives()[0]))
        .limit(getMaxPopulationSize())
        .toList();
  }

  @Override
  public List<BinarySolution> result() {
    return getPopulation().stream()
        .map(solution -> (BinarySolution) solution)
        .sorted((s1, s2) -> Double.compare(s1.objectives()[0], s2.objectives()[0]))
        .limit(1)
        .toList();
  }

  @Override
  public String name() {
    return "Custom Genetic Algorithm";
  }

  @Override
  public String description() {
    return "Custom implementation of a genetic algorithm for network optimization";
  }
}
