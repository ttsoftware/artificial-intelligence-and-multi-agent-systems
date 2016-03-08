package dtu.searchclient;

import dtu.searchclient.heuristic.AStarHeuristic;
import dtu.searchclient.heuristic.GreedyHeuristic;
import dtu.searchclient.heuristic.WeightedAStarHeuristic;
import dtu.searchclient.strategy.Strategy;
import dtu.searchclient.strategy.StrategyBFS;
import dtu.searchclient.strategy.StrategyBestFirst;
import dtu.searchclient.strategy.StrategyDFS;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class SearchClient {

    // Auxiliary static classes
    public static void error(String msg) throws Exception {
        throw new Exception("GSCError: " + msg);
    }

    public Node initialState = null;

    public SearchClient(BufferedReader serverMessages) throws Exception {
        Map<Character, String> colors = new HashMap<Character, String>();
        String line, color;

        int agentCol = -1, agentRow = -1;
        int colorLines = 0, levelLines = 0;

        // Read lines specifying colors
        while ((line = serverMessages.readLine()).matches("^[a-z]+:\\s*[0-9A-Z](,\\s*[0-9A-Z])*\\s*$")) {
            line = line.replaceAll("\\s", "");
            String[] colonSplit = line.split(":");
            color = colonSplit[0].trim();

            for (String id : colonSplit[1].split(",")) {
                colors.put(id.trim().charAt(0), color);
            }
            colorLines++;
        }

        if (colorLines > 0) {
            error("Box colors not supported");
        }

        ArrayList<String> lines = new ArrayList<>();

        int maxColumn = 0;

        while (!line.equals("")) {
            lines.add(line);
            line = serverMessages.readLine();

            if (line.length() > maxColumn) {
                maxColumn = line.length();
            }
        }

        Node.maxRow = lines.size();
        Node.maxColumn = maxColumn;

        Node.goals = (new char[Node.maxRow][Node.maxColumn]);
        Node.walls = (new boolean[Node.maxRow][Node.maxColumn]);

        initialState = new Node(null);

        for (String levelLine : lines) {
            for (int i = 0; i < levelLine.length(); i++) {
                char chr = levelLine.charAt(i);
                if ('+' == chr) { // Walls
                    Node.walls[levelLines][i] = true;
                } else if ('0' <= chr && chr <= '9') { // Agents
                    if (agentCol != -1 || agentRow != -1) {
                        error("Not a single agent level");
                    }
                    initialState.setAgentRow(levelLines);
                    initialState.setAgentCol(i);
                } else if ('A' <= chr && chr <= 'Z') { // Boxes
                    initialState.getBoxes()[levelLines][i] = chr;
                    Node.boxCount++;
                } else if ('a' <= chr && chr <= 'z') { // Goal cells
                    Node.goals[levelLines][i] = chr;
                    Node.goalLocations.add(new Pair<>(levelLines, i));
                }
            }
            levelLines++;
        }
    }

    public LinkedList<Node> search(Strategy strategy) throws IOException {
        System.err.format("Search starting with strategy %s\n", strategy);
        strategy.addToFrontier(this.initialState);

        int iterations = 0;
        while (true) {
            if (iterations % 200 == 0) {
                System.err.println(strategy.searchStatus());
            }
            if (Memory.shouldEnd()) {
                System.err.format("Memory limit almost reached, terminating search %s\n", Memory.stringRep());
                return null;
            }
            if (strategy.timeSpent() > 300) { // Minutes timeout
                System.err.format("Time limit reached, terminating search %s\n", Memory.stringRep());
                return null;
            }

            if (strategy.frontierIsEmpty()) {
                return null;
            }

            Node leafNode = strategy.getAndRemoveLeaf();

            if (leafNode.isGoalState()) {
                return leafNode.extractPlan();
            }

            strategy.addToExplored(leafNode);
            for (Node n : leafNode.getExpandedNodes()) {
                // The list of expanded nodes is shuffled randomly; see Node.java
                if (!strategy.isExplored(n) && !strategy.inFrontier(n)) {
                    strategy.addToFrontier(n);
                }
            }
            iterations++;
        }
    }

    public static void main(String[] args) throws Exception {
        BufferedReader serverMessages = new BufferedReader(new InputStreamReader(System.in));

        // Use stderr to print to console
        System.err.println("SearchClient initializing. I am sending this using the error output stream.");

        // Read level and create the initial state of the problem
        SearchClient client = new SearchClient(serverMessages);

        Strategy strategy = null;
        // strategy = new StrategyBFS();
        // Ex 1:
        // strategy = new StrategyDFS();

        // Ex 3:
        //strategy = new StrategyBestFirst(new AStarHeuristic(client.initialState));
        strategy = new StrategyBestFirst(new WeightedAStarHeuristic(client.initialState));
        //strategy = new StrategyBestFirst(new GreedyHeuristic(client.initialState));

        LinkedList<Node> solution = client.search(strategy);

        if (solution == null) {
            System.err.println("Unable to solve level");
            System.exit(0);
        } else {
            System.err.println("\nSummary for " + strategy);
            System.err.println("Found solution of length " + solution.size());
            System.err.println(strategy.searchStatus());

            for (Node n : solution) {
                String act = n.getAction().toActionString();
                System.out.println(act);
                String response = serverMessages.readLine();
                if (response.contains("false")) {
                    System.err.format("Server responsed with %s to the inapplicable action: %s\n", response, act);
                    System.err.format("%s was attempted in \n%s\n", act, n);
                    break;
                }
            }
        }
    }
}
