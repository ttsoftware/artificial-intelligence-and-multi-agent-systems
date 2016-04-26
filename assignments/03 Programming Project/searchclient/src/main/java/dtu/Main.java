package dtu;

import server.Runner;

public class Main {

    public static void main(String[] args) {

        // Run the server.jar with the following arguments
        String[] serverArgs = {
                "-l",
                // "levels/MAtest.lvl",
                //"levels/SAhlplan.lvl",
                // "levels/MA_out_of_my_way_Henning.lvl",
                // "levels/ClearPathTest.lvl",
                // "levels/SAD1_multi.lvl",
                // "levels/SApushing.lvl",
                "levels/SApushing_2.lvl",
                // "levels/SAboxesOfHanoi.lvl",
                // "levels/SAD1_multi_1_agent_wins.lvl",
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