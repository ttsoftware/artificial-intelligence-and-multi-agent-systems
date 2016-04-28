package dtu;

import server.Runner;

public class Main {

    public static void main(String[] args) {

        // Run the server.jar with the following arguments
        String[] serverArgs = {
                "-l",
                // "levels/recursion_and_friends.lvl", // TODO: Does not work
                // "levels/MAsimple5.lvl", // TODO: Does not work - StackOverflowError in POP
                // "levels/MAsimple4.lvl", // TODO: Does not work - StackOverflowError in POP
                // "levels/MAsimple3.lvl", // TODO: Does not work - StackOverflowError in POP
                // "levels/MAsimple2.lvl", // TODO: Does not work - StackOverflowError in POP
                // "levels/MAsimple1.lvl", // TODO: Does not work - StackOverflowError in POP
                // "levels/MApacman.lvl", // TODO: Does not work
                // "levels/MAmultiagentSort.lvl", // TODO: Does not work
                // "levels/MAchallenge.lvl", // TODO: Does not work - StackOverflowError in POP
                // "levels/Firefly.lvl",
                // "levels/Crunch.lvl", // TODO: Does not work (Class cast exception - POP)
                // "levels/friendofDFS.lvl",
                // "levels/friendofBFS.lvl",
                // "levels/SAD1.lvl",
                // "levels/SAD2.lvl",
                // "levels/MAtest.lvl",
                "levels/SAhlplan.lvl", // TODO: Does not work anymore :'(
                // "levels/SAhlplan_old.lvl",
                // "levels/MA_out_of_my_way_Henning.lvl", // TODO: Does not work
                // "levels/ClearPathTest.lvl",
                // "levels/SAD1_multi.lvl", // TODO: Problem with bidding
                // "levels/obstaclePathTestLevel.lvl", // TODO: Works with disappointing placement of (C)
                // "levels/SApushing.lvl",
                // "levels/SAboxesOfHanoi.lvl",
                // "levels/SAboxesOfHanoi_simple.lvl",
                // "levels/SAD1_multi_1_agent_wins.lvl",
                "-g",
                "150",
                "-t",
                "60",
                "-c",
                "java -jar out/artifacts/agency_jar/The_Agency.jar"
        };

        Runner.main(serverArgs);
    }
}