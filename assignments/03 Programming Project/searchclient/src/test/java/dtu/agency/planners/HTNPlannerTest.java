package dtu.agency.planners;

/**
 * Created by koeus on 4/1/16.
 */

import dtu.agency.ProblemMarshaller;
import dtu.agency.board.Agent;
import dtu.agency.board.Goal;
import dtu.agency.board.Level;
import dtu.agency.planners.htn.HTNPlanner;
import dtu.agency.services.LevelService;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertTrue;

public class HTNPlannerTest {

    private static Agent agent;
    private static Goal goal;

    @BeforeClass
    public static void setUp() throws IOException {
        File resourcesDirectory = new File("src/test/resources");
        String levelPath = resourcesDirectory.getAbsolutePath() + "/SAD1_goto_box.lvl";

        FileInputStream inputStream = new FileInputStream(levelPath);
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream));

        // Parse the level
        Level level = ProblemMarshaller.marshall(fileReader);
        LevelService.getInstance().setLevel(level);

        agent = level.getAgents().get(0);
        goal = level.getGoals().get(0);
    }

    @Test
    public void planTest() {
        HTNPlanner htn = new HTNPlanner(agent, goal);
        HTNPlan htnPlan = htn.getBestPlan();
        PrimitivePlan plan = htn.plan();

        System.err.println("");
        System.err.println(htnPlan.toString());
        System.err.println(plan.toString());

        assertTrue(!htnPlan.isEmpty());
        assertTrue(!htnPlan.isEmpty());
    }
}