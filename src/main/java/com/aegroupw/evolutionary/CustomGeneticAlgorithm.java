package com.aegroupw.evolutionary;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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
  private BinarizedNetworkSolution[] bestOfGeneration;
  private BinarizedNetworkSolution[] bestSurvivorOfGeneration;
  private double[] bestFitnessOfGeneration;
  private double[] bestSurvivorFitnessOfGeneration;

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

    this.bestOfGeneration = new BinarizedNetworkSolution[maxGenerations];
    this.bestSurvivorOfGeneration = new BinarizedNetworkSolution[maxGenerations];

    this.bestFitnessOfGeneration = new double[maxGenerations];
    this.bestSurvivorFitnessOfGeneration = new double[maxGenerations];
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
    List<BinarizedNetworkSolution> evaluatedPopulation = evaluator.evaluate(population, getProblem());

    List<BinarizedNetworkSolution> sortedPopulation = evaluatedPopulation.stream()
      .sorted((s1, s2) ->
        Double.compare(problem.solutionWeightedFitness(s1), problem.solutionWeightedFitness(s2)))
      .toList();

    this.bestOfGeneration[currentGeneration] = sortedPopulation.get(0);
    this.bestFitnessOfGeneration[currentGeneration] = problem.solutionWeightedFitness(sortedPopulation.get(0));

    return evaluatedPopulation;
  }

  @Override
  protected List<BinarizedNetworkSolution> replacement(
      List<BinarizedNetworkSolution> population,
      List<BinarizedNetworkSolution> offspringPopulation) {

    List<BinarizedNetworkSolution> mutablePopulation = new ArrayList<>(population);
    mutablePopulation.addAll(offspringPopulation);
    List<BinarizedNetworkSolution> newPopulation =  mutablePopulation.stream()
        .sorted((s1, s2) ->
          Double.compare(problem.solutionWeightedFitness(s1), problem.solutionWeightedFitness(s2)))
        .limit(getMaxPopulationSize())
        .toList();

    this.bestSurvivorOfGeneration[currentGeneration] = newPopulation.get(0);
    this.bestSurvivorFitnessOfGeneration[currentGeneration] = problem.solutionWeightedFitness(newPopulation.get(0));
    return newPopulation;
  }

  @Override
  protected List<BinarizedNetworkSolution> reproduction(List<BinarizedNetworkSolution> population) {
    int numberOfParents = crossoverOperator.numberOfRequiredParents();
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
        BinarizedNetworkSolution bns = new BinarizedNetworkSolution(s.numberOfBitsPerVariable(), 2,
            problem.getEdgeProbability());

        for (int j = 0; j < s.totalNumberOfBits(); j++) {
          if (s.variables().get(j).get(0)) {
            bns.variables().get(j).set(0);
          } else {
            bns.variables().get(j).clear(0);
          }
        }

        bns.objectives()[0] = s.objectives()[0];
        bns.objectives()[1] = s.objectives()[1];

        mutationOperator.execute(bns);
        offspringPopulation.add(bns);
      }
    }

    return offspringPopulation;
  }

  @Override
  public List<BinarizedNetworkSolution> result() {
    try {
      BufferedWriter outputWriter = null;
      outputWriter = new BufferedWriter(new FileWriter("reports/best_of_generations.txt"));
      for (int i = 0; i < bestOfGeneration.length; i++) {
        outputWriter.write(Double.toString(bestOfGeneration[i].objectives()[0]) + " " + Double.toString(bestOfGeneration[i].objectives()[1]));
        outputWriter.newLine();
      }
      outputWriter.flush();
      outputWriter.close();
    } catch (IOException e) {
      System.out.println("Failed to store best solutions");
    }

    try {
      BufferedWriter outputWriter = null;
      outputWriter = new BufferedWriter(new FileWriter("reports/best_survivor_of_generations.txt"));
      for (int i = 0; i < bestSurvivorOfGeneration.length; i++) {
        outputWriter.write(Double.toString(bestSurvivorOfGeneration[i].objectives()[0]) + " " + Double.toString(bestSurvivorOfGeneration[i].objectives()[1]));
        outputWriter.newLine();
      }
      outputWriter.flush();
      outputWriter.close();
    } catch (IOException e) {
      System.out.println("Failed to store best solutions");
    }

    try {
      BufferedWriter outputWriter = null;
      outputWriter = new BufferedWriter(new FileWriter("reports/best_fitness_of_generations.txt"));
      for (int i = 0; i < bestFitnessOfGeneration.length; i++) {
        outputWriter.write(Double.toString(bestFitnessOfGeneration[i]) + ",");
        outputWriter.newLine();
      }
      outputWriter.flush();
      outputWriter.close();
    } catch (IOException e) {
      System.out.println("Failed to store best solutions");
    }

    try {
      BufferedWriter outputWriter = null;
      outputWriter = new BufferedWriter(new FileWriter("reports/best_survivor_fitness_of_generations.txt"));
      for (int i = 0; i < bestSurvivorFitnessOfGeneration.length; i++) {
        outputWriter.write(Double.toString(bestSurvivorFitnessOfGeneration[i]) + ",");
        outputWriter.newLine();
      }
      outputWriter.flush();
      outputWriter.close();
    } catch (IOException e) {
      System.out.println("Failed to store best solutions");
    }

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
