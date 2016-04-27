package dtu.agency.services;

import dtu.agency.ProblemMarshaller;
import dtu.agency.actions.ConcreteAction;
import dtu.agency.actions.concreteaction.Direction;
import dtu.agency.actions.concreteaction.MoveConcreteAction;
import dtu.agency.actions.concreteaction.PushConcreteAction;
import dtu.agency.board.Agent;
import dtu.agency.board.Box;
import dtu.agency.board.Level;
import dtu.agency.board.Position;
import dtu.agency.planners.plans.PrimitivePlan;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LevelServiceTest {

    private static File resourcesDirectory = new File("src/test/resources");

    @Before
    public void setUp() throws IOException {
        String levelPath = resourcesDirectory.getAbsolutePath() + "/SAD1_multi_1_agent_wins.lvl";

        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(levelPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream));

        // Parse the level
        Level level = ProblemMarshaller.marshall(fileReader);

        GlobalLevelService.getInstance().setLevel(level);
    }

    @Test
    public void applyAction() {
        System.out.println(GlobalLevelService.getInstance().getLevel());

        boolean successfulMove = GlobalLevelService.getInstance().applyAction(
                new Agent("1"),
                new MoveConcreteAction(Direction.NORTH)
        );
        assertTrue(successfulMove);

        System.out.println(GlobalLevelService.getInstance().getLevel());

        String state = GlobalLevelService.getInstance().getLevel().toString();
        String expectedState =
                "+++++++++++++++++\n" +
                        "+0     + b+     +\n" +
                        "+      + B+     +\n" +
                        "+      +  +     +\n" +
                        "+      +  +aA   +\n" +
                        "+      + 1++    +\n" +
                        "+               +\n" +
                        "+++++++++++++++++\n";
        assertEquals(state, expectedState);
    }

    @Test
    public void testMove() {
        Agent agent = new Agent("1");
        Box box = new Box("B0");
        List<ConcreteAction> actions = new ArrayList<>();

        actions.add(new MoveConcreteAction(Direction.NORTH));
        actions.add(new MoveConcreteAction(Direction.NORTH));
        actions.add(new MoveConcreteAction(Direction.NORTH));
        actions.add(new PushConcreteAction(box, Direction.NORTH, Direction.NORTH));

        System.out.println(GlobalLevelService.getInstance().getLevel());

        actions.forEach(action -> {
            boolean successfulMove = GlobalLevelService.getInstance().applyAction(agent, action);
            assertTrue(successfulMove);

            System.out.println(GlobalLevelService.getInstance().getLevel());
        });

        String state = GlobalLevelService.getInstance().getLevel().toString();
        String expectedState =
                "+++++++++++++++++\n" +
                        "+0     + (B0b0)+     +\n" +
                        "+      + 1+     +\n" +
                        "+      +  +     +\n" +
                        "+      +  +aA   +\n" +
                        "+      +  ++    +\n" +
                        "+               +\n" +
                        "+++++++++++++++++\n";
        assertEquals(state, expectedState);
    }

    @Test
    public void testGetValidNeighbour() {
        Agent agent = new Agent("1");
        Box box = new Box("B0");

        BDIService.setInstance(new BDIService(agent));

        PrimitivePlan plan = new PrimitivePlan();

        plan.pushAction(new MoveConcreteAction(Direction.NORTH));
        plan.pushAction(new MoveConcreteAction(Direction.NORTH));
        plan.pushAction(new MoveConcreteAction(Direction.NORTH));
        plan.pushAction(new PushConcreteAction(box, Direction.NORTH, Direction.NORTH));

        LinkedList<Position> path = GlobalLevelService.getInstance().getOrderedPathWithBox(plan);

        // find 3rd free neighbour to the path
        Position freeNeighbour = GlobalLevelService.getInstance().getFreeNeighbour(path, 3);

        assertEquals(freeNeighbour, new Position(6, 8));
    }

    @Test
    public void testGetOrderedPath() {
        Agent agent = new Agent("1");
        Box box = new Box("B0");

        BDIService.setInstance(new BDIService(agent));

        PrimitivePlan plan = new PrimitivePlan();

        plan.pushAction(new MoveConcreteAction(Direction.NORTH));
        plan.pushAction(new MoveConcreteAction(Direction.NORTH));
        plan.pushAction(new MoveConcreteAction(Direction.NORTH));
        plan.pushAction(new PushConcreteAction(box, Direction.NORTH, Direction.NORTH));

        LinkedList<Position> path = GlobalLevelService.getInstance().getOrderedPath(plan);

        assertEquals(path.getLast(), new Position(2, 9));
    }

    @Test
    public void testPush() {

    }

    @Test
    public void testPull() {

    }
}
