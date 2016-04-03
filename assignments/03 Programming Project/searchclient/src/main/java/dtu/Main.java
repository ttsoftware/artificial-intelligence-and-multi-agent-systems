package dtu;

import server.Runner;

public class Main {

    // Settings to replace magic numbers in code
    public static int printIterations = 200;   // print status for every x nodes explored
    public static int timeOut = 300;           // seconds to timeout

    public static void main(String[] args) {

        // Run the server.jar with the following arguments

        String[] serverArgs = {
                "-l",
                "levels/SAD2.lvl",
                "-g",
                "50",
                "-t",
                "60",
                "-c",
                "java -jar out/artifacts/The_Agency_jar/The_Agency.jar"
        };

        Runner.main(serverArgs);
    }
}