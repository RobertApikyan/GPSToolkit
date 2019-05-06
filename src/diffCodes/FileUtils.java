package diffCodes;

import java.io.*;

public class FileUtils {

    public static void writeCodes(CodeIndividual individual) {
        try (PrintWriter writer = new PrintWriter(new File("C:\\codes.txt"))) {
            for (int codeNumber = 0; codeNumber < individual.getCodesCount(); codeNumber++) {
                double[] codes = individual.getSingleCodeChunk(codeNumber);


                for (int i = 0; i < codes.length; i++) {
                    writer.print(codes[i]);

                    if (i != codes.length - 1) {
                        writer.print(", ");
                    }
                }
                writer.println();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
