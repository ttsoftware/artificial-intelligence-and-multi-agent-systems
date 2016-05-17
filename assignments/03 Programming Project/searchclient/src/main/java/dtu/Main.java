package dtu;

import server.Runner;

public class Main {

    public static void main(String[] args) {

        // Run the server.jar with the following arguments
        String[] serverArgs = {
                "-l",
                // MULTI AGENT LEVELS
                // "levels/MArecursion_and_friends.lvl",
                // "levels/MArecursion_and_friends_color.lvl",
                // "levels/MArecursion_and_friends_color_big.lvl",
                // "levels/MAbispebjerg.lvl", // TODO: Does not work - need conflict resolution
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
                // "levels/MA_help_henning.lvl",
                // "levels/MA_help_henning_3.lvl",
                // "levels/MA_help_henning_4.lvl",
                // "levels/MAconflicts.lvl", // TODO: deadlock
                // "levels/MAconflicts2.lvl", // TODO: deadlock
                // "levels/MAconflicts_simple.lvl",
                // "levels/MAconflicts_simple2.lvl",
                // "levels/MAconflicts_simple3.lvl",
                // "levels/MAmultiagentSort.lvl", // TODO: deadlock
                //"levels/MAtbsAppartment.lvl", // TODO: deadlock

                // SINGLE AGENT LEVELS
                // "levels/SAFirefly.lvl",
                // "levels/SACrunch.lvl", // TODO: Independent goals are not independent
                // "levels/SAfriendofDFS.lvl",
                // "levels/SAfriendofBFS.lvl",
                // "levels/SAsokobanLevel96.lvl",
                // "levels/SAHateful_Three.lvl",
                // "levels/SAHateful_Eight.lvl",
                // "levels/SALongJourney.lvl",
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

                // COMPETITION LEVELS - SINGLE AGENT LEVELS
                // "competition_levels/single_agent/SAAIMuffins.lvl", // TODO: Goal weighing prevents this
                // "competition_levels/single_agent/SAbotbot.lvl", // TODO: Works! (sometimes)
                // "competition_levels/single_agent/SAboXboXboX.lvl", // TODO: goal weighing
                // "competition_levels/single_agent/SAButterBot.lvl", // TODO: goal weighing
                // "competition_levels/single_agent/SADangerBot.lvl", // TODO: Works!
                // "competition_levels/single_agent/SAextra2.lvl", // TODO: Works!
                // "competition_levels/single_agent/SAFortyTwo.lvl", // TODO: Works! (sometimes)
                // "competition_levels/single_agent/SALazarus.lvl", // TODO: Works!
                // "competition_levels/single_agent/SANoOp.lvl", // TODO: We cannot solve this
                // "competition_levels/single_agent/SAOptimal.lvl", // TODO: Infinite loop?
                // "competition_levels/single_agent/SASojourner.lvl", // TODO: Works!
                // "competition_levels/single_agent/SASolo.lvl", // TODO: Works!
                "competition_levels/single_agent/SATAIM.lvl", // TODO: Works!
                // "competition_levels/single_agent/SAteamhal.lvl", // TODO: Works!
                // "competition_levels/single_agent/SATheAgency.lvl", // TODO: Works!
                // "competition_levels/single_agent/SATheRedDot.lvl", // TODO: Works!

                // COMPETITION LEVELS - SINGLE AGENT LEVELS
                // "competition_levels/multi_agent/MAAIMuffins.lvl", // TODO: Not even close
                // "competition_levels/multi_agent/MAbotbot.lvl", // TODO: Deadlock?
                // "competition_levels/multi_agent/MAboXboXboX.lvl", // TODO: what even?
                // "competition_levels/multi_agent/MAButterBot.lvl", // TODO: Conflict resolution
                // "competition_levels/multi_agent/MADangerBot.lvl", // TODO: Agents just stop?
                // "competition_levels/multi_agent/MAextra1.lvl", // TODO: Agents just stop?
                // "competition_levels/multi_agent/MALazarus.lvl", // TODO: Works!
                // "competition_levels/multi_agent/MANoOp.lvl", // TODO: Agents just stop?
                // "competition_levels/multi_agent/MAOptimal.lvl", // TODO: Agents cannot help each other
                // "competition_levels/multi_agent/MASojourner.lvl", // TODO: Just stops...
                // "competition_levels/multi_agent/MASolo.lvl", // TODO: Conflict resolution
                // "competition_levels/multi_agent/MATAIM.lvl", // TODO: Conflict resolution
                // "competition_levels/multi_agent/MAteamhal.lvl", // TODO: Goal weighing
                // "competition_levels/multi_agent/MATheAgency.lvl", // TODO: Works!
                // "competition_levels/multi_agent/MATheRedDot.lvl", // TODO: Goal weighing
                // "competition_levels/multi_agent/MAWallE.lvl", // TODO: Just stops...

                "-g",
                "200",
                "-t",
                "300",
                "-c",
                "java -jar out/artifacts/agency_jar/The_Agency.jar"
        };

        Runner.main(serverArgs);
    }
}