package dtu.agency.planners.htn;

import dtu.agency.ProblemMarshallerTest;
import dtu.agency.agent.bdi.Ideas;
import dtu.agency.board.Agent;
import dtu.agency.board.Goal;
import dtu.agency.board.Level;
import dtu.agency.planners.plans.PrimitivePlan;
import dtu.agency.services.BDIService;
import dtu.agency.services.GlobalLevelService;
import dtu.agency.services.PlanningLevelService;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertTrue;

public class HTNPlannerTest {

    private Agent agent;
    private Goal goal;
    private String s;
    private static Level sad1Goto, sad1Move;
    private static Level lvl001, lvl002, lvl003, lvl004, lvl005, lvl006, lvl007, lvl008, lvl009;

    @BeforeClass
    public static void setUp() throws IOException {
        sad1Goto = marshall("/SAD1_goto_box.lvl");
        sad1Move = marshall("/SAD1_move_box.lvl");
        lvl001 = marshall("/001.lvl"); // Fine plan - will solve the problem
        lvl002 = marshall("/002.lvl"); // Fine plan - will solve the problem
        lvl003 = marshall("/003.lvl"); // Fine plan - it will not solve the problem though :-)
        lvl004 = marshall("/004.lvl"); // Fine plan - it will not solve the problem
        lvl005 = marshall("/005.lvl"); // placeholder - implement level first
        lvl006 = marshall("/006.lvl"); // placeholder - implement level first
        lvl007 = marshall("/007.lvl"); // placeholder - implement level first
        lvl008 = marshall("/SALabyrinth.lvl"); // placeholder - implement level first
        lvl009 = marshall("/SAHateful_Eight.lvl"); // placeholder - implement level first
    }

    private static Level marshall(String path) throws IOException {
        return ProblemMarshallerTest.marshall(path);
    }

    public void levelTest(Level level, int maxSolutionLength) {
        GlobalLevelService.getInstance().setLevel(level);

        agent = GlobalLevelService.getInstance().getLevel().getAgents().get(0);
        goal = GlobalLevelService.getInstance().getLevel().getGoals().get(0);

        BDIService.setInstance(new BDIService(agent));

        BDIService mind = BDIService.getInstance();
        // Planner initialization
        Ideas ideas = mind.thinkOfIdeas(goal);
        // System.err.println("Ideas: " + ideas.toString() + "\n");

        PlanningLevelService pls1 = new PlanningLevelService(BDIService.getInstance().getBDILevelService().getLevel());
        PlanningLevelService pls2 = new PlanningLevelService(BDIService.getInstance().getBDILevelService().getLevel());
        HTNPlanner htn1 = new HTNPlanner(pls1, ideas.getBest(), RelaxationMode.NoAgentsNoBoxes);
        HTNPlanner htn2 = new HTNPlanner(pls2, ideas.getBest(), RelaxationMode.NoAgentsNoBoxes);
        // System.err.println("HTNPlannerTest: " + htn1.toString() + "\n");
        // System.err.println("HTNPlannerTest: " + htn2.toString() + "\n");

        // Does heuristics calculation work
        int stepsApproximation = htn2.getBestPlanApproximation();
        assertTrue("HTNPlannerTest: Heuristic Approximation should be non-negative", stepsApproximation>=0);
        // System.err.println("Heuristic approximation: " + Integer.toString(stepsApproximation) + "\n");

        // does it find a plan? Maybe we would like to debug this area of the code
        PrimitivePlan plan = htn2.plan();

        assertTrue("HTNPlannerTest: primitivePlan is not found", plan != null);
        // System.err.println("HTNPlannerTest: " + plan.toString() + "\n");
        assertTrue("HTNPlannerTest: primitivePlan is empty", !plan.isEmpty());

        // is the plan within expected length?
        s = "primitivePlan is longer than " + Integer.toString(maxSolutionLength);
        s += " steps, it is " + plan.getActions().size() + "\n";
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