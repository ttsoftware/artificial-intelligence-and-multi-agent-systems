package dtu;

import server.Runner;

public class Main {

    public static void main(String[] args) {

        // Run the server.jar with the following arguments

        String[] serverArgs = {
                "-l",
                "levels/Crunch.lvl",
                "-g",
                "50",
                "-t",
                "60",
                "-c",
                "java -jar out/artifacts/SearchClient_jar/searchclient.jar"
                //"java -jar out/artifacts/PlannerClient_jar/searchclient.jar"
        };

        Runner.main(serverArgs);
    }
}