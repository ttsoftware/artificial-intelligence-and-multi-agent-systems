package dtu;

import dtu.agency.planners.heuristics.Heuristic;
import dtu.agency.planners.heuristics.ManhattanHeuristic;
import server.Runner;

public class Main {

    public static void main(String[] args) {

        // Run the server.jar with the following arguments

        String[] serverArgs = {
                "-l",
                "levels/SAD1_multi.lvl",
//                "levels/ClearPathTest.lvl",
//                "levels/SAD1.lvl",
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