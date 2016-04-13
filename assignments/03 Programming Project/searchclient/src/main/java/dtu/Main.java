package dtu;

import dtu.agency.planners.heuristics.Heuristic;
import dtu.agency.planners.heuristics.ManhattanHeuristic;
import server.Runner;

public class Main {

    // Settings to replace magic numbers in code
    public static int printIterations = 200;   // print status for every x nodes explored
    public static Heuristic heuristicMeasure = new ManhattanHeuristic();

    public static void main(String[] args) {

        // Run the server.jar with the following arguments

        String[] serverArgs = {
                "-l",
                "levels/SAD1_multi.lvl",
                "-g",
                "100",
                "-t",
                "60",
                "-c",
                "java -jar out/artifacts/agency_jar/The_Agency.jar"
        };

        Runner.main(serverArgs);
    }
}