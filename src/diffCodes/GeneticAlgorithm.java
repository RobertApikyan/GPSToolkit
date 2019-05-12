package diffCodes;


import java.awt.*;
import java.util.Arrays;

public class GeneticAlgorithm {

    private int populationSize;
    private double mutationRate;
    private double crossoverRate;
    private int elitismCount;
    private int effectiveGenerationOffset;
    private int currentEffectiveGenerationCounter = 0;
    private double currentEffectiveGenerationFitness = -1.0;


    public GeneticAlgorithm(int populationSize, double mutationRate, double crossoverRate, int elitismCount, int effectiveGenerationOffset) {
        this.populationSize = populationSize;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
        this.elitismCount = elitismCount;
        this.effectiveGenerationOffset = effectiveGenerationOffset;
    }


    public CodesPopulation initPopulation(int singleCodeLength, int codesCount) {
        return new CodesPopulation(populationSize, singleCodeLength, codesCount);
    }

    public CodeIndividual selectParent(CodesPopulation population) {
        CodeIndividual[] individuals = population.getCodes();

        double populationFitness = population.getPopulationFitness();
        double rouletteStopPosition = populationFitness * Math.random();

        double roulettePosition = 0.0;

        for (CodeIndividual individual : individuals) {
            roulettePosition += individual.getFitness();

            if (roulettePosition >= rouletteStopPosition) {
                return individual;
            }
        }

        return individuals[individuals.length - 1];
    }

    public void evaluate(CodesPopulation population) {
        double populationFitness = 0.0;
        for (int i = 0; i < population.getSize(); i++) {
            CodeIndividual codeIndividual = population.getCode(i);
            double fitness = calculateFitness(codeIndividual);
            codeIndividual.setFitness(fitness);
            populationFitness += fitness;
        }
        population.setPopulationFitness(populationFitness);
    }

    public boolean willTerminate(CodesPopulation population) {
        CodeIndividual fittest = population.getFittest(0);
        double fitness = fittest.getFitness();

//        if (fitness > 0.95){
//            Toolkit.getDefaultToolkit().beep();
//            FileUtils.writeCodes(fittest);
//            return true;
//        }

        if (currentEffectiveGenerationFitness != fitness) {
            currentEffectiveGenerationFitness = fitness;
            currentEffectiveGenerationCounter = effectiveGenerationOffset;
            return false;
        } else {
            currentEffectiveGenerationCounter--;
            if (currentEffectiveGenerationCounter == 0) {
                Toolkit.getDefaultToolkit().beep();
                FileUtils.writeCodes(fittest);
            }
            return currentEffectiveGenerationCounter == 0;
        }
    }

    public CodesPopulation crossover(CodesPopulation population) {

        CodesPopulation newPopulation = new CodesPopulation(population.getSize());

        for (int firstParentIndex = 0; firstParentIndex < population.getSize(); firstParentIndex++) {
            CodeIndividual firstParent = population.getFittest(firstParentIndex);

            if (firstParentIndex >= elitismCount && crossoverRate > Math.random()) {
                // crossover
                CodeIndividual secondParent = selectParent(population);

                CodeIndividual childIndividual = cross(firstParent, secondParent);

                newPopulation.setCode(firstParentIndex, childIndividual);
            } else {
                newPopulation.setCode(firstParentIndex, firstParent);
            }
        }
        return newPopulation;
    }

    private CodeIndividual cross(CodeIndividual firstParent, CodeIndividual secondParent) {
        double firstFitness = firstParent.getFitness();
        double secondFitness = secondParent.getFitness();

        CodeIndividual child = new CodeIndividual(firstParent.getSingleCodeLength(), firstParent.getCodesCount());

        for (int codeIndex = 0; codeIndex < child.getTotalLength(); codeIndex++) {
            double firstRand = firstFitness * Math.random();
            double secondRand = secondFitness * Math.random();
            if (firstRand >= secondRand) {
                child.setCode(codeIndex, firstParent.getCode(codeIndex));
            } else {
                child.setCode(codeIndex, secondParent.getCode(codeIndex));
            }
        }

        return child;
    }

    public CodesPopulation mutate(CodesPopulation population) {
        CodesPopulation newPopulation = new CodesPopulation(population.getSize());

        for (int populationIndex = 0; populationIndex < population.getSize(); populationIndex++) {

            CodeIndividual individual = population.getFittest(populationIndex);

            if (populationIndex >= elitismCount) {
                // mutate
                for (int individualIndex = 0; individualIndex < individual.getTotalLength(); individualIndex++) {
                    if (mutationRate > Math.random()) {
                        //mutate
                        double newGene = 1.0;
                        if (individual.getCode(individualIndex) == 1.0) {
                            newGene = -1.0;
                        }
                        individual.setCode(individualIndex, newGene);

                        newPopulation.setCode(populationIndex, individual);
                    } else {
                        newPopulation.setCode(populationIndex, individual);
                    }
                }
            } else {
                newPopulation.setCode(populationIndex, individual);
            }

        }
        return newPopulation;
    }

    private double calculateFitness(CodeIndividual individual) {
        double overallFitness = 0.0;

        for (int sourceNumber = 0; sourceNumber < individual.getCodesCount(); sourceNumber++) {

            double[] source = individual.getSingleCodeChunk(sourceNumber);

            double sourceFitness = 0.0;

            for (int sampleNumber = 0; sampleNumber < individual.getCodesCount(); sampleNumber++) {

                if (sampleNumber != sourceNumber) {
                    double[] sample = individual.getSingleCodeChunk(sampleNumber);
                    double coefficient = Correlation.coefficient(source, sample);
                    double corrValue = Correlation.value(0, source.length - 1, source, sample);

                    // maximum correlation value
                    sourceFitness += (1 - corrValue/coefficient) / individual.getCodesCount();

                }
            }

            overallFitness += sourceFitness;
        }

        overallFitness = overallFitness / individual.getCodesCount();

        return overallFitness;
    }
}
