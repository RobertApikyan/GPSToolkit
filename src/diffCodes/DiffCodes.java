package diffCodes;

public class DiffCodes {


    public static void main(String[] args) {

        int populationSize = 100;

        double mutationRate = 0.001;

        double crossoverRate = 0.95;

        int elitismCount = 2;

        int singleCodeLength = 5;

        int codesCount = 10;

        int generation = 0;

        GeneticAlgorithm ga = new GeneticAlgorithm(populationSize,mutationRate,crossoverRate,elitismCount);

        CodesPopulation population = ga.initPopulation(singleCodeLength,codesCount);

        ga.evaluate(population);

        while (!ga.willTerminate(population)){
            population = ga.crossover(population);

            population = ga.mutate(population);

            ga.evaluate(population);

            generation++;
        }
    }
}
