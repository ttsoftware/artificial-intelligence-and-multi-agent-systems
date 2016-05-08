package dtu.agency;

import dtu.agency.actions.ConcreteAction;
import dtu.agency.actions.concreteaction.Direction;
import dtu.agency.actions.concreteaction.MoveConcreteAction;
import dtu.agency.actions.concreteaction.PullConcreteAction;
import dtu.agency.actions.concreteaction.PushConcreteAction;
import dtu.agency.board.*;
import dtu.agency.planners.plans.ConcretePlan;
import dtu.agency.planners.plans.PrimitivePlan;
import dtu.agency.services.GlobalLevelService;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PlannerClientThreadTest {

    private static File resourcesDirectory = new File("src/test/resources");
    private HashMap<Integer, ConcretePlan> agentsPlans = new HashMap<>();

    @Before
    public void setUp() {
        PrimitivePlan plan0 = new PrimitivePlan();
        PrimitivePlan plan1 = new PrimitivePlan();
        PrimitivePlan plan2 = new PrimitivePlan();
        PrimitivePlan plan3 = new PrimitivePlan();

        plan0.pushAction(new PushConcreteAction(new Box("A0"), Direction.EAST, Direction.EAST));
        plan1.pushAction(new MoveConcreteAction(Direction.WEST));
        plan2.pushAction(new PullConcreteAction(new Box("B0"), Direction.WEST, Direction.SOUTH));
        plan3.pushAction(new MoveConcreteAction(Direction.SOUTH));

        plan0.pushAction(new MoveConcreteAction(Direction.EAST));
        plan1.pushAction(new MoveConcreteAction(Direction.WEST));
        plan2.pushAction(new MoveConcreteAction(Direction.NORTH));
        plan3.pushAction(new MoveConcreteAction(Direction.SOUTH));

        agentsPlans.put(0, plan0);
        agentsPlans.put(1, plan1);
        agentsPlans.put(2, plan2);
        agentsPlans.put(3, plan3);
    }

    @Test
    public void testMain() throws Exception {

        // String path = resourcesDirectory.getAbsolutePath() + "/SApushing.lvl";
        // String path = resourcesDirectory.getAbsolutePath() + "/SApushing_2.lvl";
        // String path = resourcesDirectory.getAbsolutePath() + "/SAD1_multi_1_agent_wins.lvl";
        // String path = resourcesDirectory.getAbsolutePath() + "/SAboxesOfHanoi.lvl";
        // String path = resourcesDirectory.getAbsolutePath() + "/SAhlplan_old.lvl";
        // String path = resourcesDirectory.getAbsolutePath() + "/SAboxesOfHanoi_simple.lvl";
        String path = resourcesDirectory.getAbsolutePath() + "/obstaclePathTestLevel.lvl";
        FileInputStream inputStream = new FileInputStream(path);

        System.setIn(inputStream);

        PlannerClient.main(new String[]{});
    }

    @Test
    public void test0() throws Exception {
        testLevel("obstaclePathTestLevel.lvl");
    }

    @Test
    public void test1() throws Exception {
        testLevel("SApushing.lvl");
    }

    @Test
    public void test2() throws Exception {
        testLevel("SApushing_2.lvl");
    }

    @Test
    public void test3() throws Exception {
        testLevel("SAD1_multi_1_agent_wins.lvl");
    }

    @Test
    public void test3_1() throws Exception {
        testLevel("SAD1_multi.lvl");
    }

    @Test
    public void test4() throws Exception {
        testLevel("SAboxesOfHanoi.lvl");
    }

    @Test
    public void test4_1() throws Exception {
        testLevel("SAboxesOfHanoi_simple.lvl");
    }

    @Test
    public void test5() throws Exception {
        testLevel("SAhlplan.lvl");
    }

    @Test
    public void test6() throws Exception {
        testLevel("SAhlplan_old.lvl");
    }

    @Test
    public void test7() throws Exception {
        testLevel("MAsimple1.lvl");
    }

    @Test
    public void test8() throws Exception {
        testLevel("MAchallenge.lvl");
    }

    @Test
    public void test9() throws Exception {
        testLevel("MA_out_of_my_way_Henning.lvl");
    }

    @Test
    public void test10() throws Exception {
        testLevel("MA_help_henning.lvl");
    }

    @Test
    public void test11() throws Exception {
        testLevel("MA_help_henning_3.lvl");
    }

    public void testLevel(String level) throws Exception {

        String path = resourcesDirectory.getAbsolutePath() + "/" + level;
        FileInputStream inputStream = new FileInputStream(path);

        System.setIn(inputStream);

        PlannerClient.main(new String[]{});

        List<Goal> goals = GlobalLevelService.getInstance().getLevel().getGoals();
        goals.forEach(goal -> {
            Position goalPosition = GlobalLevelService.getInstance().getPosition(goal);
            BoardObject positionObject = GlobalLevelService.getInstance().getObject(goalPosition);
            BoardCell positionCell = GlobalLevelService.getInstance().getCell(goalPosition);

            assertEquals(BoardCell.BOX_GOAL, positionCell);

            BoxAndGoal boxAndGoal = (BoxAndGoal) positionObject;

            assertTrue(boxAndGoal.isSolved());
        });
    }

    @Test
    public void testBuildActionSet() {

        int numberOfAgents = 4;

        HashMap<Integer, ConcreteAction> agentsActions = new HashMap<>();
        // pop the next action from each plan
        agentsPlans.forEach((integer, concretePlan) -> agentsActions.put(integer, concretePlan.popAction()));

        String toServer = new PlannerClientThread().buildActionSet(agentsActions, numberOfAgents);

        assertEquals(toServer, "[Move(E),Move(W),Move(N),Move(S)]");

        agentsPlans.forEach((integer, concretePlan) -> agentsActions.put(integer, concretePlan.popAction()));

        toServer = new PlannerClientThread().buildActionSet(agentsActions, numberOfAgents);

        assertEquals(toServer, "[Push(E,E),Move(W),Pull(W,S),Move(S)]");
    }
}