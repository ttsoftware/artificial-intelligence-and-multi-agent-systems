package dtu;

import server.Runner;

public class Main {

    public static void main(String[] args) {

        // Run the server.jar with the following arguments
        String[] serverArgs = {
                "-l",
                // MULTI AGENT LEVELS
                // "levels/MArecursion_and_friends.lvl", // TODO: Does not work - need conflict resolution
                // "levels/MArecursion_and_friends_color.lvl", // TODO: Does not work
                // "levels/MAsimple5.lvl", // TODO: Does not work - need conflict resolution
                // "levels/MAsimple4.lvl", // TODO: Does not work
                // "levels/MAsimple3.lvl", // TODO: Does not work - Agents need to communicate
                // "levels/MAsimple2.lvl", // TODO: Why do agents wait for each other to finish?
                // "levels/MAsimple1.lvl", // TODO: Why do agents wait for each other to finish?
                // "levels/MApacman.lvl", // TODO: Does not work
                // "levels/MApacman_easy.lvl",
                // "levels/MAmultiagentSort.lvl", // TODO: Does not work - null pointer in planner
                // "levels/MAchallenge.lvl", // TODO: Does not work - Many different reasons
                // "levels/MAtest.lvl",
                // "levels/MA_out_of_my_way_Henning.lvl", // TODO: Does not work
                // "levels/MA_help_henning.lvl", // TODO: Does not work - not right colour? so, red != red??
                // "levels/MA_help_henning_3.lvl", // TODO: Does not work
                // "levels/MA_help_henning_4.lvl",
                // "levels/MAconflicts_simple.lvl", // TODO: no longer works
                // "levels/MAconflicts_simple2.lvl", // TODO: does not work
                // "levels/MAconflicts_simple3.lvl",  // TODO: does not work
                // SINGLE AGENT LEVELS
                // "levels/SAFirefly.lvl",
                // "levels/SACrunch.lvl", // TODO: Independent goals are not independent
                // "levels/SAfriendofDFS.lvl",
                // "levels/SAfriendofBFS.lvl",
                // "levels/SAsokobanLevel96.lvl",
                // "levels/SAHateful_Three.lvl", // TODO: Does not work
                // "levels/SAHateful_Eight.lvl", // TODO: Does not work - AssertionError: Cannot insert box on any cell but FREE or GOAL cells
                // TODO: - WORKS SOMETIMES!?! - but not if a box is parked on top of a goal
                "levels/SALongJourney.lvl", // TODO: Does not work
                // "levels/SAD1.lvl",
                // "levels/SAD2.lvl",
                // "levels/SAD1_multi.lvl",
                // "levels/SAD1_multi_conflict.lvl",
                // "levels/SAD1_multi_1_agent_wins.lvl",
                // "levels/SAhlplan.lvl",
                // "levels/SAhlplan_old.lvl",
                // "levels/SAClearPathTest.lvl",
                // "levels/SAobstaclePathTestLevel.lvl",
                // "levels/SApushing.lvl",
                // "levels/SAboxesOfHanoi.lvl",
                // "levels/SAboxesOfHanoi_simple.lvl",
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