package com.aegroupw.evolutionary;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.solution.binarysolution.impl.DefaultBinarySolution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

public class CustomGeneticAlgorithm
    extends AbstractGeneticAlgorithm<BinarizedNetworkSolution, List<BinarizedNetworkSolution>> {

  private final SolutionListEvaluator<BinarizedNetworkSolution> evaluator;
  private int maxGenerations;
  private int currentGeneration;

  private NetworkOptimizationProblem problem;

  public CustomGeneticAlgorithm(
      NetworkOptimizationProblem problem,
      int maxGenerations,
      SolutionListEvaluator<BinarizedNetworkSolution> evaluator,
      SelectionOperator<List<BinarizedNetworkSolution>, BinarizedNetworkSolution> selectionOperator,
      CrossoverOperator<BinarizedNetworkSolution> crossoverOperator,
      MutationOperator<BinarizedNetworkSolution> mutationOperator, int maxPopulationSize) {
    // super(problem);
    super(problem);
    this.evaluator = evaluator;
    this.maxGenerations = maxGenerations;
    this.maxPopulationSize = maxPopulationSize;
    this.currentGeneration = 0;
    this.problem = problem;

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
  protected List<BinarizedNetworkSolution> evaluatePopulation(List<BinarizedNetworkSolution> population) {
    return evaluator.evaluate(population, getProblem());
  }

  @Override
  protected List<BinarizedNetworkSolution> replacement(
      List<BinarizedNetworkSolution> population,
      List<BinarizedNetworkSolution> offspringPopulation) {

    List<BinarizedNetworkSolution> mutablePopulation = new ArrayList<>(population);
    mutablePopulation.addAll(offspringPopulation);
    return mutablePopulation.stream()
        .sorted((s1, s2) ->
          Double.compare(problem.solutionWeightedFitness(s1), problem.solutionWeightedFitness(s2)))
        .limit(getMaxPopulationSize())
        .toList();
  }

  @Override
  protected List<BinarizedNetworkSolution> reproduction(List<BinarizedNetworkSolution> population) {
    int numberOfParents = crossoverOperator.numberOfRequiredParents() ;

    checkNumberOfParents(population, numberOfParents);

    List<BinarizedNetworkSolution> offspringPopulation = new ArrayList<>(getMaxPopulationSize());
    for (int i = 0; i < getMaxPopulationSize(); i += numberOfParents) {
      List<BinarizedNetworkSolution> parents = new ArrayList<>(numberOfParents);
      for (int j = 0; j < numberOfParents; j++) {
        parents.add(population.get(i+j));
      }

      List<BinarizedNetworkSolution> offspring = crossoverOperator.execute(parents);
      ListIterator<BinarizedNetworkSolution> it = offspring.listIterator();

      // hack to avoid type error
      while (it.hasNext()) {
        DefaultBinarySolution s = it.next();
        BinarizedNetworkSolution bns = new BinarizedNetworkSolution(
          s.numberOfBitsPerVariable(), 2, problem.getEdgeProbability());
        mutationOperator.execute(bns);
        offspringPopulation.add(bns);
      }
    }
    return offspringPopulation;
  }

  @Override
  public List<BinarizedNetworkSolution> result() {
    return getPopulation().stream()
        .sorted((s1, s2) ->
          Double.compare(problem.solutionWeightedFitness(s1), problem.solutionWeightedFitness(s2)))
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
