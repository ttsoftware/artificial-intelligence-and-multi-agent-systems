package dtu.agency.planners.pop;

import dtu.agency.ProblemMarshaller;
import dtu.agency.board.Agent;
import dtu.agency.board.Goal;
import dtu.agency.board.Level;
import dtu.agency.planners.actions.GotoAbstractAction;
import dtu.agency.planners.htn.HTNPlan;
import dtu.agency.planners.htn.HTNPlanner;
import dtu.agency.services.LevelService;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertTrue;

public class GotoPOPTest {

    private static Agent agent;
    private static Goal goal;

    @BeforeClass
    public static void setUp() throws IOException {
        File resourcesDirectory = new File("src/test/resources");
        String levelPath = resourcesDirectory.getAbsolutePath() + "/SAD1.lvl";

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

        HTNPlan htnPlan = new HTNPlanner(agent, goal).plan();
        GotoPOP gotoPlanner = new GotoPOP(agent);

        POPPlan popPlan = gotoPlanner.plan((GotoAbstractAction) htnPlan.getActions().get(0));

        assertTrue(popPlan.getActions().size() > 0);
    }

    @Test
    public void solvePreconditionTest() {

    }

    @Test
    public void getOpenPreconditionsTest() {

    }

    @Test
    public void isOpenPreconditionTest() {

    }
}