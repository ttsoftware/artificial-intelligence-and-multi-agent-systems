package dtu.agency;

import dtu.agency.actions.ConcreteAction;
import dtu.agency.actions.concreteaction.Direction;
import dtu.agency.actions.concreteaction.MoveConcreteAction;
import dtu.agency.actions.concreteaction.PullConcreteAction;
import dtu.agency.actions.concreteaction.PushConcreteAction;
import dtu.agency.board.Box;
import dtu.agency.planners.plans.ConcretePlan;
import dtu.agency.planners.plans.PrimitivePlan;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class PlannerClientTest {

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
        String path = resourcesDirectory.getAbsolutePath() + "/SApushing_2.lvl";
        // String path = resourcesDirectory.getAbsolutePath() + "/SAD1_multi_1_agent_wins.lvl";
        // String path = resourcesDirectory.getAbsolutePath() + "/SAboxesOfHanoi.lvl";
        FileInputStream inputStream = new FileInputStream(path);

        System.setIn(inputStream);

        PlannerClient.main(new String[]{});
    }

    @Test
    public void testBuildActionSet() {

        int numberOfAgents = 4;

        HashMap<Integer, ConcreteAction> agentsActions = new HashMap<>();
        // pop the next action from each plan
        agentsPlans.forEach((integer, concretePlan) -> agentsActions.put(integer, concretePlan.popAction()));

        String toServer = PlannerClient.buildActionSet(agentsActions, numberOfAgents);

        assertEquals(toServer, "[Move(E),Move(W),Move(N),Move(S)]");

        agentsPlans.forEach((integer, concretePlan) -> agentsActions.put(integer, concretePlan.popAction()));

        toServer = PlannerClient.buildActionSet(agentsActions, numberOfAgents);

        assertEquals(toServer, "[Push(E,E),Move(W),Pull(W,S),Move(S)]");
    }
}