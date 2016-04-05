package dtu;

import dtu.agency.planners.htn.heuristic.Method;
import server.Runner;

public class Main {

    // Settings to replace magic numbers in code
    public static int printIterations = 200;   // print status for every x nodes explored
    public static int timeOut = 300;           // seconds to timeout
    public static Method heuristicMeasure = Method.MANHATTAN;

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