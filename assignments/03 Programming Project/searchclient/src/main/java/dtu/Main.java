package dtu;

import server.Runner;

public class Main {

    public static void main(String[] args) {

        // Run the server.jar with the following arguments
        String[] serverArgs = {
                "-l",
                // "levels/SAhlplan.lvl",
                // "levels/MA_out_of_my_way_Henning.lvl",
                // "levels/ClearPathTest.lvl",
                "levels/SApushing.lvl",
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