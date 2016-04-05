package dtu.agency.planners.htn;

import dtu.agency.ProblemMarshaller;
import dtu.agency.board.Agent;
import dtu.agency.board.Goal;
import dtu.agency.board.Level;
import dtu.agency.planners.HTNPlan;
import dtu.agency.planners.PrimitivePlan;
import dtu.agency.services.LevelService;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertTrue;

public class HTNPlannerTest {

    private Agent agent;
    private Goal goal;
    private String s;
    private static File resourcesDirectory;
    private static Level sad1Goto, sad1Move;
    private static Level lvl001, lvl002, lvl003, lvl004, lvl005, lvl006, lvl007, lvl008, lvl009;

    @BeforeClass
    public static void setUp() throws IOException {
        resourcesDirectory = new File("src/test/resources");

        sad1Goto = marshall("/SAD1_goto_box.lvl");
        sad1Move = marshall("/SAD1_move_box.lvl");
        lvl001 = marshall("/001.lvl"); // Fine plan - will solve the problem
        lvl002 = marshall("/002.lvl"); // Fine plan - will solve the problem
        lvl003 = marshall("/003.lvl"); // Fine plan - it will not solve the problem though :-)
        lvl004 = marshall("/004.lvl"); // Fine plan - it will not solve the problem
        lvl005 = marshall("/005.lvl"); // placeholder - implement level first
        lvl006 = marshall("/006.lvl"); // placeholder - implement level first
        lvl007 = marshall("/007.lvl"); // placeholder - implement level first
        lvl008 = marshall("/008.lvl"); // placeholder - implement level first
        lvl009 = marshall("/009.lvl"); // placeholder - implement level first
    }

    private static Level marshall(String path) throws IOException {
        String levelPath = resourcesDirectory.getAbsolutePath() + path;
        FileInputStream inputStream = new FileInputStream(levelPath);
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream));
        return ProblemMarshaller.marshall(fileReader);
    }

    public void levelTest(Level level, int maxSolutionLength) {
        LevelService.getInstance().setLevel(level);

        agent = LevelService.getInstance().getLevel().getAgents().get(0);
        goal = LevelService.getInstance().getLevel().getGoals().get(0);

        HTNPlanner htn = new HTNPlanner(agent, goal);
        //System.err.println(htn.toString());

        //HTNPlan htnPlan = htn.getBestPlan(); // no longer needed??
        //System.err.println(htnPlan.toString());

        int stepsApproximation = htn.getBestPlanApproximation();
        PrimitivePlan plan = htn.plan();
        //System.err.println(plan.toString());

        System.err.println("");
        //if (htnPlan != null) System.err.println(htnPlan.toString());

        //assertTrue("htnPlan does not exist", htnPlan != null);
        //assertTrue("htnPlan is empty", !htnPlan.isEmpty());

        assertTrue("Heuristic Approximation should be non-negative", stepsApproximation>=0);
        assertTrue("primitivePlan is not found", plan != null);
        assertTrue("primitivePlan is empty", !plan.isEmpty());
        System.err.println("Heuristic approximation: " + Integer.toString(stepsApproximation));
        System.err.println(plan.toString());

        s = "primitivePlan is longer than " + Integer.toString(maxSolutionLength);
        s += " steps, it is " + plan.getActions().size();
        assertTrue(s, plan.getActions().size() <= maxSolutionLength);
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

    @Test
    public void level001Test() {
        int maxSolutionLength = 3; // minSolutionLength = 3
        levelTest(lvl001, maxSolutionLength);
    }

    @Test
    public void level002Test() {
        int maxSolutionLength = 5; // minSolutionLength = 5
        levelTest(lvl002, maxSolutionLength);
    }

    @Test
    public void level003Test() {
        int maxSolutionLength = 7; // minSolutionLength = 7
        levelTest(lvl003, maxSolutionLength);
    }

    @Test
    public void level004Test() {
        int maxSolutionLength = 13; // minSolutionLength = 13
        levelTest(lvl004, maxSolutionLength);
    }

    @Test
    public void level005Test() {
        int maxSolutionLength = 1; // minSolutionLength = 1
        levelTest(lvl005, maxSolutionLength);
    }

    @Test
    public void level006Test() {
        int maxSolutionLength = 1; // minSolutionLength = 1
        levelTest(lvl006, maxSolutionLength);
    }

    @Test
    public void level007Test() {
        int maxSolutionLength = 1; // minSolutionLength = 1
        levelTest(lvl007, maxSolutionLength);
    }

    @Test
    public void level008Test() {
        int maxSolutionLength = 1; // minSolutionLength = 1
        levelTest(lvl008, maxSolutionLength);
    }

    @Test
    public void level009Test() {
        int maxSolutionLength = 1; // minSolutionLength = 1
        levelTest(lvl009, maxSolutionLength);
    }

}