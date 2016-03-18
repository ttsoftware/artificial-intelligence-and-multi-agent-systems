package dtu;

import dtu.board.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public class ProblemMarshaller {

    /**
     * Take the input file and create a Level object
     *
     * @param fileReader BufferedReader
     * @throws IOException
     */
    public static Level marshall(BufferedReader fileReader) throws IOException {

        Map<Character, String> colors = new HashMap<>();

        // Objects we wish to create
        BoardCell[][] BoardState = {{}};
        BoardObject[][] BoardObjects = {{}};
        PriorityQueue<Goal> goalQueue = new PriorityQueue<>(new GoalComparator());
        List<Agent> agents = new ArrayList<>();
        List<Box> boxes = new ArrayList<>();
        List<Box> walls = new ArrayList<>();
        List<Goal> goals = new ArrayList<>();

        ArrayList<String> lines = new ArrayList<>();

        // read all lines into the lines array
        String fileLine = fileReader.readLine();
        while (fileLine != null && !fileLine.equals("")) {
            lines.add(fileLine);
            fileLine = fileReader.readLine();
        }

        // Read lines specifying colors
        for (String line : lines) {
            if (line.matches("^[a-z]+:\\s*[0-9A-Z](,\\s*[0-9A-Z])*\\s*$")) {
                line.replaceAll("\\s", "");
                String[] colonSplit = line.split(":");
                String color = colonSplit[0].trim();

                for (String id : colonSplit[1].split(",")) {
                    colors.put(id.trim().charAt(0), color);
                }
            }
        }

        for (String line : lines) {
            for (int i = 0; i < line.length(); i++) {
                char chr = line.charAt(i);
                if ('+' == chr) {
                    // Its a wall cell
                } else if ('0' <= chr && chr <= '9') {
                    // Its an agent cell
                } else if ('A' <= chr && chr <= 'Z') {
                    // Its a box cell
                } else if ('a' <= chr && chr <= 'z') {
                    // Its a goal cell
                }
            }
        }

        throw new UnsupportedOperationException("Create a level object from input file");
    }
}
