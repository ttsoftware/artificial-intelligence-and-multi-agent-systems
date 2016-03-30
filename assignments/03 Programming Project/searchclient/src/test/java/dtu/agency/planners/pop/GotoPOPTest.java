package dtu.agency.planners.pop;

import dtu.agency.ProblemMarshaller;
import dtu.agency.board.Agent;
import dtu.agency.board.Box;
import dtu.agency.board.Goal;
import dtu.agency.board.Level;
import dtu.agency.planners.actions.GotoAction;
import dtu.agency.planners.htn.HTNPlan;
import dtu.agency.planners.htn.HTNPlanner;
import dtu.agency.services.LevelService;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertTrue;

public class GotoPOPTest {

    static Level level;
    static Agent agent;
    static Goal goal;
    static Box box;

    @BeforeClass
    public static void setUp() throws IOException {
        File resourcesDirectory = new File("src/test/resources");
        String levelPath = resourcesDirectory.getAbsolutePath() + "/SAD1.lvl";

        FileInputStream inputStream = new FileInputStream(levelPath);
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream));

        // Parse the level
        level = ProblemMarshaller.marshall(fileReader);
        LevelService.getInstance().setLevel(level);

        agent = level.getAgents().get(0);
        goal = level.getGoals().get(0);
        box = level.getBoxes().get(0);
    }

    @Test
    public void planTest() {

        HTNPlan htnPlan = new HTNPlanner(agent, goal).plan();
        GotoPOP gotoPlanner = new GotoPOP(agent);
        
        POPPlan popPlan = gotoPlanner.plan((GotoAction) htnPlan.getActions().get(0));

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
