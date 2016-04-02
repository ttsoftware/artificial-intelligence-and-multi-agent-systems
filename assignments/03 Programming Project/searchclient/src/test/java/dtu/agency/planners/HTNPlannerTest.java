package dtu.agency.planners;

/**
 * Created by mads on 4/1/16.
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

    private Agent agent;
    private Goal goal;
    private String s;
    private static Level sad1Goto;
    private static Level sad1Move;

    @BeforeClass
    public static void setUp() throws IOException {
        File resourcesDirectory = new File("src/test/resources");
        String levelPath;
        FileInputStream inputStream;
        BufferedReader fileReader;

        levelPath = resourcesDirectory.getAbsolutePath() + "/SAD1_goto_box.lvl";
        inputStream = new FileInputStream(levelPath);
        fileReader = new BufferedReader(new InputStreamReader(inputStream));
        sad1Goto = ProblemMarshaller.marshall(fileReader);

        levelPath = resourcesDirectory.getAbsolutePath() + "/SAD1_move_box.lvl";
        inputStream = new FileInputStream(levelPath);
        fileReader = new BufferedReader(new InputStreamReader(inputStream));
        sad1Move = ProblemMarshaller.marshall(fileReader);
    }

    public void levelTest(Level level, int maxSolutionLength) {
        LevelService.getInstance().setLevel(level);

        agent = LevelService.getInstance().getLevel().getAgents().get(0);
        goal = LevelService.getInstance().getLevel().getGoals().get(0);

        HTNPlanner htn = new HTNPlanner(agent, goal);
        HTNPlan htnPlan = htn.getBestPlan();
        PrimitivePlan plan = htn.plan();

        System.err.println("");
        if (htnPlan!=null) System.err.println(htnPlan.toString());
        if (plan!=null) System.err.println(plan.toString());

        assertTrue("htnPlan does not exist", htnPlan!=null);
        assertTrue("htnPlan is empty", !htnPlan.isEmpty());
        assertTrue("primitivePlan is not found", plan!=null);
        assertTrue("primitivePlan is empty", !plan.isEmpty());
        s  = "primitivePlan is longer than " + Integer.toString(maxSolutionLength);
        s += " steps, it is " + plan.getActions().size();
        assertTrue(s, plan.getActions().size()<=maxSolutionLength);
    }

    @Test
    public void sad1MoveTest() {
        int maxSolutionLength = 1; // minSolutionLength = 1
        levelTest(sad1Move, maxSolutionLength);
    }

    @Test
    public void sad1GotoTest() {
        int maxSolutionLength = 21; // minSolutionLength = 19
        levelTest(sad1Goto, maxSolutionLength);
    }
}