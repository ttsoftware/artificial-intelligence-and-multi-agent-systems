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
                // "competition_levels/combined/SAAIMuffins.lvl", // TODO: Goal weighing prevents this
                // "competition_levels/combined/SAbotbot.lvl", // TODO: Works! (sometimes)
                // "competition_levels/combined/SAboXboXboX.lvl", // TODO: goal weighing
                // "competition_levels/combined/SAButterBot.lvl", // TODO: goal weighing
                // "competition_levels/combined/SADangerBot.lvl", // TODO: Works!
                // "competition_levels/combined/SAextra2.lvl", // TODO: Works!
                // "competition_levels/combined/SAFortyTwo.lvl", // TODO: Works! (sometimes)
                // "competition_levels/combined/SALazarus.lvl", // TODO: Works!
                // "competition_levels/combined/SANoOp.lvl", // TODO: We cannot solve this
                // "competition_levels/combined/SAOptimal.lvl", // TODO: Infinite loop?
                // "competition_levels/combined/SASojourner.lvl", // TODO: Works!
                // "competition_levels/combined/SASolo.lvl", // TODO: Infinite loop?
                // "competition_levels/combined/SATAIM.lvl", // TODO: NullPointerException in POP
                // "competition_levels/combined/SAteamhal.lvl", // TODO: Works!
                // "competition_levels/combined/SATheAgency.lvl", // TODO: Works!
                // "competition_levels/combined/SATheRedDot.lvl", // TODO: Works!

                // COMPETITION LEVELS - SINGLE AGENT LEVELS
                // "competition_levels/combined/MAAIMuffins.lvl", // TODO: Not even close
                // "competition_levels/combined/MAbotbot.lvl", // TODO: Deadlock?
                // "competition_levels/combined/MAboXboXboX.lvl", // TODO: what even?
                 "competition_levels/combined/MAButterBot.lvl", // TODO: Conflict resolution
                // "competition_levels/combined/MADangerBot.lvl", // TODO: Agents just stop?
                // "competition_levels/combined/MAextra1.lvl", // TODO: Agents just stop?
                // "competition_levels/combined/MALazarus.lvl", // TODO: Works!
                // "competition_levels/combined/MANoOp.lvl", // TODO: Agents just stop?
                // "competition_levels/combined/MAOptimal.lvl", // TODO: Agents cannot help each other
                // "competition_levels/combined/MASojourner.lvl", // TODO: Just stops...
                // "competition_levels/combined/MASolo.lvl", // TODO: Conflict resolution
                // "competition_levels/combined/MATAIM.lvl", // TODO: Conflict resolution
                // "competition_levels/combined/MAteamhal.lvl", // TODO: Goal weighing
                // "competition_levels/combined/MATheAgency.lvl", // TODO: Works!
                // "competition_levels/combined/MATheRedDot.lvl", // TODO: Goal weighing
                // "competition_levels/combined/MAWallE.lvl", // TODO: Just stops...

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