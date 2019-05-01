package diffCodes;

import java.util.Arrays;

public class CodeIndividual {
    private Double[] codes;
    private int totalLength;
    private int codesCount;
    private int singleCodeLength;
    private Double fitness = -1.0;

    public CodeIndividual(int singleCodeLength, int codesCount) {
        this.singleCodeLength = singleCodeLength;
        this.codesCount = codesCount;
        this.totalLength = singleCodeLength * codesCount;
        this.codes = new Double[totalLength];
        for (int i = 0; i < codes.length; i++) {
            Double value = Math.random() > 0.5 ? 1.0 : 0.0;
            codes[i] = value;
        }
    }

    public void setFitness(Double fitness) {
        this.fitness = fitness;
    }

    public Double getFitness() {
        return fitness;
    }

    public int getTotalLength() {
        return totalLength;
    }

    public int getCodesCount() {
        return codesCount;
    }

    public Double[] getCodes() {
        return codes;
    }

    public int getSingleCodeLength() {
        return singleCodeLength;
    }

    public void setCode(int offset, Double value){
        codes[offset] = value;
    }

    public double getCode(int offset){
        return codes[offset];
    }

    public Double[] getSingleCodeChunk(int codeNumber){
        int from = codeNumber*singleCodeLength;
        int to = codeNumber*singleCodeLength + singleCodeLength;
        return Arrays.copyOfRange(codes,from,to);
    }

    public void setSingleCode(int codeNumber,int offset,Double value){
        setCode(codeNumber * singleCodeLength + offset,value);
    }

    public double getSingleCode(int codeNumber,int offset){
        return codes[codeNumber * singleCodeLength + offset];
    }
}
