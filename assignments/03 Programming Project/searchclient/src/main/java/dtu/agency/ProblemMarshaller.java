package dtu.agency;

import dtu.agency.board.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public class ProblemMarshaller {

    private static final int DEFAULT_WEIGHT = 0;

    /**
     * Take the input file and create a Level object
     *
     * @param fileReader BufferedReader
     * @throws IOException
     */
    public static Level marshall(BufferedReader fileReader) throws IOException {

        int rowCount = 0;
        int columnCount = 0;

        ArrayList<String> lines = new ArrayList<>();

        // read all lines into the lines array
        String fileLine = fileReader.readLine();
        while (fileLine != null && !fileLine.equals("")) {
            rowCount++;
            if (fileLine.length() > columnCount) {
                columnCount = fileLine.length();
            }
            lines.add(fileLine);
            fileLine = fileReader.readLine();
        }

        Map<Character, String> colors = new HashMap<>();

        // Objects we wish to create
        // TODO: Fix board size to match actual board size
        BoardCell[][] boardState = new BoardCell[rowCount][columnCount];
        Hashtable<String, Position> boardObjects = new Hashtable<>();
        PriorityQueue<Goal> goalQueue = new PriorityQueue<>(new GoalComparator());
        List<Agent> agents = new ArrayList<>();
        List<Box> boxes = new ArrayList<>();
        List<Wall> walls = new ArrayList<>();
        List<Goal> goals = new ArrayList<>();

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

        for (int row = 0; row < lines.size(); row++) {
            for (int column = 0; column < lines.get(row).length(); column++) {
                char cell = lines.get(row).charAt(column);
                if ('+' == cell) {
                    // Its a wall cell
                    Wall wall = new Wall();
                    walls.add(wall);
                    boardObjects.put(String.valueOf(cell), new Position(row, column));
                    boardState[row][column] = BoardCell.WALL;
                }
                else if ('0' <= cell && cell <= '9') {
                    // Its an agent cell
                    Agent agent = new Agent(String.valueOf(cell));
                    agents.add(agent);
                    boardObjects.put(String.valueOf(cell), new Position(row, column));
                    boardState[row][column] = BoardCell.AGENT;
                }
                else if ('A' <= cell && cell <= 'Z') {
                    // Its a box cell
                    Box box = new Box();
                    boxes.add(box);
                    boardObjects.put(String.valueOf(cell), new Position(row, column));
                    boardState[row][column] = BoardCell.BOX;
                }
                else if ('a' <= cell && cell <= 'z') {
                    // Its a goal cell
                    boardObjects.put(String.valueOf(cell), new Position(row, column));
                    boardState[row][column] = BoardCell.GOAL;

                    Goal goal = new Goal(String.valueOf(cell), row, column, DEFAULT_WEIGHT);
                    goals.add(goal);
                    goalQueue.add(goal);
                }
            }
        }

        return new Level(
                boardState,
                boardObjects,
                goalQueue,
                goals,
                agents,
                boxes,
                walls
        );
    }
}
