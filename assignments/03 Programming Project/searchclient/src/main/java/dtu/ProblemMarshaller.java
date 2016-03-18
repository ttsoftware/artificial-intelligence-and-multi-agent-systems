package dtu;

import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProblemMarshaller {

    /**
     * Take the input file and create a Level object
     *
     * @param levelFile BufferedReader
     * @throws IOException
     */
    public static Level marshall(BufferedReader levelFile) throws IOException {

        Map<Character, String> colors = new HashMap<>();
        String line, color;

        int agentCol = -1, agentRow = -1;
        int colorLines = 0, levelLines = 0;

        // Read lines specifying colors
        while ((line = levelFile.readLine()).matches("^[a-z]+:\\s*[0-9A-Z](,\\s*[0-9A-Z])*\\s*$")) {
            line = line.replaceAll("\\s", "");
            String[] colonSplit = line.split(":");
            color = colonSplit[0].trim();

            for (String id : colonSplit[1].split(",")) {
                colors.put(id.trim().charAt(0), color);
            }
            colorLines++;
        }

        ArrayList<String> lines = new ArrayList<>();

        int maxColumn = 0;

        while (!line.equals("")) {
            lines.add(line);
            line = levelFile.readLine();

            if (line.length() > maxColumn) {
                maxColumn = line.length();
            }
        }

        int maxRow = lines.size();

        int boxCount = 0;
        char[][] goals = (new char[maxRow][maxColumn]);
        boolean[][] walls = (new boolean[maxRow][maxColumn]);

        List<Pair<Integer, Integer>> goalLocations = new ArrayList<>();

        for (String levelLine : lines) {
            for (int i = 0; i < levelLine.length(); i++) {
                char chr = levelLine.charAt(i);
                if ('+' == chr) { // Walls
                    walls[levelLines][i] = true;
                } else if ('0' <= chr && chr <= '9') { // Agents
                    if (agentCol != -1 || agentRow != -1) {
                        // Not a single agent level
                    }
                } else if ('A' <= chr && chr <= 'Z') { // Boxes
                    boxCount++;
                } else if ('a' <= chr && chr <= 'z') { // Goal cells
                    goals[levelLines][i] = chr;
                    goalLocations.add(new Pair<>(levelLines, i));
                }
            }
            levelLines++;
        }

        throw new UnsupportedOperationException("Create a level object from input file");
    }
}
