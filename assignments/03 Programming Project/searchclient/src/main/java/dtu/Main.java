package dtu;

import server.Runner;

public class Main {

    public static void main(String[] args) {

        // Run the server.jar with the following arguments
        String[] serverArgs = {
                "-l",
                // "levels/recursion_and_friends.lvl",
                // "levels/recursion_and_friends_color.lvl",
                // "levels/MAsimple5.lvl", // TODO: Does not work - planner error
                // "levels/MAsimple4.lvl", // TODO: Does not work - planner error
                // "levels/MAsimple3.lvl", // TODO: Does not work - Independent goals are not independent?
                // "levels/MAsimple2.lvl", // TODO: Why do agents wait for each other to finish?
                // "levels/MAsimple1.lvl", // TODO: Why do agents wait for each other to finish?
                // "levels/MApacman.lvl", // TODO: Agents do not go back to old plans after resolving conflict!
                // "levels/MApacman_easy.lvl",
                // "levels/MAmultiagentSort.lvl", // TODO: Does not work - null pointer in planner
                // "levels/MAchallenge.lvl", // TODO: Does not work - Many different reasons
                // "levels/MAtest.lvl",
                // "levels/MA_out_of_my_way_Henning.lvl", // TODO: Does not work
                // "levels/MA_help_henning.lvl",
                // "levels/MA_help_henning_3.lvl",
                // "levels/MA_help_henning_4.lvl",
                // "levels/Firefly.lvl",
                // "levels/Crunch.lvl", // TODO: Independent goals are not independent?
                // "levels/friendofDFS.lvl",
                // "levels/friendofBFS.lvl",
                // "levels/SAD1.lvl",
                // "levels/SAD2.lvl",
                // "levels/SAD1_multi.lvl",
                // "levels/SAD1_multi_conflict.lvl", // TODO: Nullpointer exception in planner
                // "levels/SAD1_multi_1_agent_wins.lvl",
                // "levels/SAhlplan.lvl",
                // "levels/SAhlplan_old.lvl",
                // "levels/ClearPathTest.lvl",
                // "levels/obstaclePathTestLevel.lvl",
                // "levels/SApushing.lvl",
                // "levels/SAboxesOfHanoi.lvl",
                // "levels/SAboxesOfHanoi_simple.lvl",
                // "levels/MAconflicts_simple.lvl",
                // "levels/MAconflicts_simple2.lvl",
                "levels/MAconflicts_simple3.lvl",
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