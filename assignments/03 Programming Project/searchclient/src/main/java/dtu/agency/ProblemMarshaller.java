package dtu.agency;

import dtu.agency.board.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.stream.Collectors;

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
        BoardObject[][] boardObjects = new BoardObject[rowCount][columnCount];
        ConcurrentHashMap<String, Position> boardObjectPositions = new ConcurrentHashMap<>();
        PriorityBlockingQueue<Goal> goalQueue = new PriorityBlockingQueue<>();
        ConcurrentHashMap<String, List<Goal>> boxesGoals = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, List<Box>> goalsBoxes = new ConcurrentHashMap<>();
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

        int wallCount = 0;
        int boxCount = 0;
        int goalCount = 0;

        for (int row = 0; row < lines.size(); row++) {
            for (int column = 0; column < lines.get(row).length(); column++) {
                char cell = lines.get(row).charAt(column);
                if ('+' == cell) {
                    // Its a wall cell
                    String label = String.valueOf(cell) + Integer.toString(wallCount);
                    Wall wall = new Wall(label, new Position(row, column));
                    walls.add(wall);
                    boardObjectPositions.put(label, new Position(row, column));
                    boardState[row][column] = BoardCell.WALL;
                    boardObjects[row][column] = wall;
                    wallCount++;
                }
                else if ('0' <= cell && cell <= '9') {
                    // Its an agency cell
                    String label = String.valueOf(cell);
                    Agent agent = new Agent(label, new Position(row, column));
                    agents.add(agent);
                    boardObjectPositions.put(label, new Position(row, column));
                    boardState[row][column] = BoardCell.AGENT;
                    boardObjects[row][column] = agent;
                }
                else if ('A' <= cell && cell <= 'Z') {
                    // Its a box cell
                    String label = String.valueOf(cell) + Integer.toString(boxCount);
                    Box box = new Box(label, new Position(row, column));
                    boxes.add(box);
                    boardObjectPositions.put(label, new Position(row, column));
                    boardState[row][column] = BoardCell.BOX;
                    boardObjects[row][column] = box;
                    boxCount++;
                }
                else if ('a' <= cell && cell <= 'z') {
                    // Its a goal cell
                    String label = String.valueOf(cell) + Integer.toString(goalCount);
                    Goal goal = new Goal(label, new Position(row, column), DEFAULT_WEIGHT);
                    boardObjectPositions.put(label, new Position(row, column));
                    boardState[row][column] = BoardCell.GOAL;
                    boardObjects[row][column] = goal;
                    goals.add(goal);
                    goalQueue.offer(goal);
                    goalCount++;
                }
                else {
                    boardState[row][column] = BoardCell.FREE_CELL;
                }
            }
        }

        // Assign box goals
        for (Box box : boxes) {
            List<Goal> boxGoals = goals.stream().filter(
                    // If goal matches box
                    goal -> goal.getLabel().startsWith(box.getLabel().substring(0, 1).toLowerCase())
            ).collect(Collectors.toList());
            boxesGoals.put(box.getLabel(), boxGoals);
        }

        // Assign goal boxes
        for (Goal goal : goals) {
            List<Box> goalBoxes = boxes.stream().filter(
                    // If box matches goal
                    box -> box.getLabel().startsWith(goal.getLabel().substring(0, 1).toUpperCase())
            ).collect(Collectors.toList());
            goalsBoxes.put(goal.getLabel(), goalBoxes);
        }

        return new Level(
                boardState,
                boardObjects,
                boardObjectPositions,
                goalQueue,
                boxesGoals,
                goalsBoxes,
                goals,
                agents,
                boxes,
                walls
        );
    }
}
