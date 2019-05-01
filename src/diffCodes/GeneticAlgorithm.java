package diffCodes;

import correlation.Correlation;

import java.util.Arrays;
import java.util.Collections;

public class GeneticAlgorithm {

    private int populationSize;
    private double mutationRate;
    private double crossoverRate;
    private int elitismCount;

    public GeneticAlgorithm(int populationSize, double mutationRate, double crossoverRate, int elitismCount) {
        this.populationSize = populationSize;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
        this.elitismCount = elitismCount;
    }


    public CodesPopulation initPopulation(int singleCodeLength, int codesCount) {
        return new CodesPopulation(populationSize, singleCodeLength, codesCount);
    }

    public void evaluate(CodesPopulation population) {

    }

    public boolean willTerminate(CodesPopulation population) {
        return false;
    }

    public CodesPopulation crossover(CodesPopulation population) {
        return null;
    }

    public CodesPopulation mutate(CodesPopulation population) {
        return null;
    }

    private double calculateFitness(CodeIndividual individual) {
        double overallFitness = 0.0;

        for (int sourceNumber = 0; sourceNumber < individual.getCodesCount(); sourceNumber++) {

            Double[] source = individual.getSingleCodeChunk(sourceNumber);

            double sourceFitness = 0.0;

            for (int sampleNumber = 0; sampleNumber < individual.getTotalLength(); sampleNumber += individual.getSingleCodeLength()) {

                if (sampleNumber != sourceNumber) {
                    Double[] sample = individual.getSingleCodeChunk(sampleNumber);
                    Double[] values = Correlation.INSTANCE.crossValues(source, sample, 0.0, Correlation.INSTANCE.coefficient(source, sample));

                    Arrays.sort(values);

                    // maximum correlation value
                    sourceFitness += values[values.length - 1];
                }

            }

            overallFitness += 1-sourceFitness/(individual.getCodesCount() - 1);
        }

        overallFitness = overallFitness / individual.getCodesCount();

        return overallFitness;
    }
}
