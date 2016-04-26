package dtu;

import server.Runner;

public class Main {

    public static void main(String[] args) {

        // Run the server.jar with the following arguments
        String[] serverArgs = {
                "-l",
                // "levels/MAtest.lvl",
                // "levels/SAhlplan_old.lvl", // TODO: Does not work
                // "levels/MA_out_of_my_way_Henning.lvl", // TODO: Does not work
                // "levels/ClearPathTest.lvl",
                // "levels/SAD1_multi.lvl",
                // "levels/SApushing.lvl",
                "levels/SAboxesOfHanoi.lvl", // TODO: Does not work
                // "levels/SAboxesOfHanoi_simple.lvl",
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