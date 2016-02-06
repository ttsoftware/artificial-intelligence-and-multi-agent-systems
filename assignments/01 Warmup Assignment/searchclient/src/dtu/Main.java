package dtu;

import server.Runner;

public class Main {

    public static void main(String[] args) {

        // Run the server.jar with the following arguments

        String[] serverArgs = {
                "-l",
                "levels/friendOfBFS.lvl",
                "-g",
                "50",
                "-c",
                "java -jar out/artifacts/searchclient_jar/searchclient.jar"
        };

        Runner.main(serverArgs);
    }
}