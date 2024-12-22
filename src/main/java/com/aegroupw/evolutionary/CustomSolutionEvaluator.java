package com.aegroupw.evolutionary;

import java.util.List;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.problem.Problem;

public class CustomSolutionEvaluator implements SolutionListEvaluator<BinarizedNetworkSolution> {

    private final NetworkOptimizationProblem problem;

    public CustomSolutionEvaluator(NetworkOptimizationProblem problem) {
        this.problem = problem;
    }

    @Override
    public List<BinarizedNetworkSolution> evaluate(List<BinarizedNetworkSolution> population,
            Problem<BinarizedNetworkSolution> problem) {
        for (BinarizedNetworkSolution solution : population) {
            this.problem.evaluate(solution);
        }
        return population;
    }

    @Override
    public void shutdown() {
        // No es necesario hacer limpieza, a menos que se esté utilizando multihilo.
    }
}
