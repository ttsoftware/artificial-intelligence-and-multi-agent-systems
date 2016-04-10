package dtu;

import dtu.agency.planners.htn.heuristic.Heuristic;
import dtu.agency.planners.htn.heuristic.ManhattanHeuristic;
import server.Runner;

public class Main {

    // Settings to replace magic numbers in code
    public static int printIterations = 200;   // print status for every x nodes explored
    public static Heuristic heuristicMeasure = new ManhattanHeuristic();

    public static void main(String[] args) {

        // Run the server.jar with the following arguments

        String[] serverArgs = {
                "-l",
                "levels/SAD1.lvl",
                "-g",
                "50",
                "-t",
                "60",
                "-c",
                "java -jar out/artifacts/agency_jar/The_Agency.jar"
        };

        Runner.main(serverArgs);
    }
}