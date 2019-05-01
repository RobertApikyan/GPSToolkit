package diffCodes;

public class CodesPopulation {
    private int populationFitness = -1;
    private int size;
    private CodeIndividual[] codes;

    public CodesPopulation(int size, int singleCodeLength, int codesCount){
        this.size = size;
        codes = new CodeIndividual[size];
        for (int i = 0; i < size; i++) {
            codes[i] = new CodeIndividual(singleCodeLength,codesCount);
        }
    }

    public void setCode(int offset, CodeIndividual code){
        codes[offset] = code;
    }

    public CodeIndividual getCode(int offset){
        return codes[offset];
    }

    public void setPopulationFitness(int populationFitness) {
        this.populationFitness = populationFitness;
    }

    public int getPopulationFitness() {
        return populationFitness;
    }

    public int getSize() {
        return size;
    }
}
