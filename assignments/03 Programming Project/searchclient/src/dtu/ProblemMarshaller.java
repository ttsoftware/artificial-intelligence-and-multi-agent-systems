package dtu;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProblemMarshaller {

    /**
     * Take the input file and create a Level object
     *
     * @param inputFile BufferedReader
     * @throws IOException
     */
    public static Level marshall(BufferedReader inputFile) throws IOException {

        Map<Character, String> colors = new HashMap<>();
        String line, color;

        int agentCol = -1, agentRow = -1;
        int colorLines = 0, levelLines = 0;

        // Read lines specifying colors
        while ((line = inputFile.readLine()).matches("^[a-z]+:\\s*[0-9A-Z](,\\s*[0-9A-Z])*\\s*$")) {
            line = line.replaceAll("\\s", "");
            String[] colonSplit = line.split(":");
            color = colonSplit[0].trim();

            for (String id : colonSplit[1].split(",")) {
                colors.put(id.trim().charAt(0), color);
            }
            colorLines++;
        }

        throw new UnsupportedOperationException("Create a level object from input file");
    }
}
