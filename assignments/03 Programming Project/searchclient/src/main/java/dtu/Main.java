package dtu;

import server.Runner;

public class Main {

    public static void main(String[] args) {

        // Run the server.jar with the following arguments
        String[] serverArgs = {
                "-l",
                // "levels/recursion_and_friends.lvl",
                // "levels/recursion_and_friends_color.lvl", // TODO: Does not work
                // "levels/MAsimple5.lvl", // TODO: Does not work - need conflict resolution
                // "levels/MAsimple4.lvl", // TODO: Does not work
                // "levels/MAsimple3.lvl", // TODO: Does not work - Agents need to communicate
                // "levels/MAsimple2.lvl", // TODO: Why do agents wait for each other to finish?
                // "levels/MAsimple1.lvl", // TODO: Why do agents wait for each other to finish?
                // "levels/MApacman.lvl", // TODO: Does not work
                // "levels/MAmultiagentSort.lvl", // TODO: Does not work
                // "levels/MAchallenge.lvl", // TODO: Does not work - Many different reasons
                // "levels/MAtest.lvl",
                // "levels/MA_out_of_my_way_Henning.lvl", // TODO: Does not work
                // "levels/MA_help_henning.lvl", // TODO: Does not work
                // "levels/MA_help_henning_3.lvl", // TODO: Does not work
                // "levels/MA_help_henning_4.lvl",
                // "levels/Firefly.lvl",
                // "levels/Crunch.lvl",
                // "levels/friendofDFS.lvl",
                // "levels/friendofBFS.lvl",
                // "levels/SAD1.lvl",
                // "levels/SAD2.lvl",
                // "levels/SAD1_multi.lvl",
                // "levels/SAD1_multi_conflict.lvl",
                // "levels/SAD1_multi_1_agent_wins.lvl",
                // "levels/SAhlplan.lvl",
                // "levels/SAhlplan_old.lvl",
                // "levels/ClearPathTest.lvl",
                "levels/obstaclePathTestLevel.lvl",
                // "levels/SApushing.lvl",
                // "levels/SAboxesOfHanoi.lvl",
                // "levels/SAboxesOfHanoi_simple.lvl",
                // "levels/MAconflicts_simple.lvl", // no longer works
                // "levels/MAconflicts_simple2.lvl",
                // "levels/MAconflicts_simple3.lvl",
                "-g",
                "200",
                "-t",
                "60",
                "-c",
                "java -jar out/artifacts/agency_jar/The_Agency.jar"
        };

        Runner.main(serverArgs);
    }
}