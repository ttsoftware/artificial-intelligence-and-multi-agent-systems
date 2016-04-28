package dtu.agency.services;

import dtu.agency.ProblemMarshaller;
import dtu.agency.actions.ConcreteAction;
import dtu.agency.actions.concreteaction.Direction;
import dtu.agency.actions.concreteaction.MoveConcreteAction;
import dtu.agency.actions.concreteaction.PullConcreteAction;
import dtu.agency.actions.concreteaction.PushConcreteAction;
import dtu.agency.board.*;
import dtu.agency.planners.plans.PrimitivePlan;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LevelServiceTest {

    private static File resourcesDirectory = new File("src/test/resources");

    @Test
    public void applyAction() throws IOException {

        String levelPath = resourcesDirectory.getAbsolutePath() + "/SAD1_multi_1_agent_wins.lvl";

        FileInputStream inputStream = new FileInputStream(levelPath);
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream));

        // Parse the level
        Level level = ProblemMarshaller.marshall(fileReader);

        GlobalLevelService.getInstance().setLevel(level);

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
    public void testMove() throws IOException {

        String levelPath = resourcesDirectory.getAbsolutePath() + "/SAD1_multi_1_agent_wins.lvl";

        FileInputStream inputStream = new FileInputStream(levelPath);
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream));

        // Parse the level
        Level level = ProblemMarshaller.marshall(fileReader);

        GlobalLevelService.getInstance().setLevel(level);

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
    public void testGetValidNeighbour() throws IOException {

        String levelPath = resourcesDirectory.getAbsolutePath() + "/SAD1_multi_1_agent_wins.lvl";

        FileInputStream inputStream = new FileInputStream(levelPath);
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream));

        // Parse the level
        Level level = ProblemMarshaller.marshall(fileReader);

        GlobalLevelService.getInstance().setLevel(level);

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
        Position freeNeighbour = GlobalLevelService.getInstance().getFreeNeighbour(
                path,
                BDIService.getInstance().getBDILevelService().getPosition(box),
                3
        );

        assertEquals(new Position(4, 8), freeNeighbour);
    }

    @Test
    public void testGetOrderedPath() throws IOException {
        String levelPath = resourcesDirectory.getAbsolutePath() + "/SAD1_multi_1_agent_wins.lvl";

        FileInputStream inputStream = new FileInputStream(levelPath);
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream));

        // Parse the level
        Level level = ProblemMarshaller.marshall(fileReader);

        GlobalLevelService.getInstance().setLevel(level);

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
    public void testGetObstacleFreePath() throws IOException {

        String levelPath = resourcesDirectory.getAbsolutePath() + "/obstaclePathTestLevel.lvl";

        FileInputStream inputStream = inputStream = new FileInputStream(levelPath);
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream));

        // Parse the level
        Level level = ProblemMarshaller.marshall(fileReader);

        GlobalLevelService.getInstance().setLevel(level);

        Agent agent = new Agent("0");
        Box boxB = new Box("B0");

        BDIService.setInstance(new BDIService(agent));

        PrimitivePlan plan = new PrimitivePlan();

        plan.addAction(new MoveConcreteAction(Direction.NORTH));
        plan.addAction(new MoveConcreteAction(Direction.NORTH));
        plan.addAction(new MoveConcreteAction(Direction.WEST));
        plan.addAction(new MoveConcreteAction(Direction.WEST));
        plan.addAction(new MoveConcreteAction(Direction.WEST));
        plan.addAction(new MoveConcreteAction(Direction.WEST));
        plan.addAction(new MoveConcreteAction(Direction.WEST));
        plan.addAction(new MoveConcreteAction(Direction.WEST));
        plan.addAction(new MoveConcreteAction(Direction.WEST));
        plan.addAction(new MoveConcreteAction(Direction.WEST));
        plan.addAction(new MoveConcreteAction(Direction.WEST));
        plan.addAction(new PullConcreteAction(boxB, Direction.EAST, Direction.WEST));
        plan.addAction(new PullConcreteAction(boxB, Direction.EAST, Direction.WEST));
        plan.addAction(new PullConcreteAction(boxB, Direction.EAST, Direction.WEST));
        plan.addAction(new PullConcreteAction(boxB, Direction.EAST, Direction.WEST));
        plan.addAction(new PullConcreteAction(boxB, Direction.EAST, Direction.WEST));
        plan.addAction(new PullConcreteAction(boxB, Direction.EAST, Direction.WEST));
        plan.addAction(new PullConcreteAction(boxB, Direction.EAST, Direction.WEST));
        plan.addAction(new PullConcreteAction(boxB, Direction.EAST, Direction.WEST));
        plan.addAction(new PullConcreteAction(boxB, Direction.EAST, Direction.WEST));
        plan.addAction(new PullConcreteAction(boxB, Direction.SOUTH, Direction.WEST));
        plan.addAction(new PushConcreteAction(boxB, Direction.NORTH, Direction.EAST));
        plan.addAction(new PushConcreteAction(boxB, Direction.EAST, Direction.EAST));
        plan.addAction(new PushConcreteAction(boxB, Direction.EAST, Direction.EAST));
        plan.addAction(new PushConcreteAction(boxB, Direction.EAST, Direction.EAST));
        plan.addAction(new PushConcreteAction(boxB, Direction.EAST, Direction.EAST));

        LinkedList<Position> path = GlobalLevelService.getInstance().getOrderedPath(plan);

        Position obstaclePosition = new Position(4, 8);

        LinkedList<Position> obstacleFreePath = GlobalLevelService.getInstance()
                .getObstacleFreePath(
                        path,
                        BDIService.getInstance().getAgentCurrentPosition(),
                        obstaclePosition
                );

        LinkedList<Position> expectedSubPath = new LinkedList<>();
        expectedSubPath.addLast(new Position(6, 11));
        expectedSubPath.addLast(new Position(5, 11));
        expectedSubPath.addLast(new Position(4, 11));
        expectedSubPath.addLast(new Position(4, 10));
        expectedSubPath.addLast(new Position(4, 9));
        expectedSubPath.addLast(new Position(4, 8));
        expectedSubPath.addLast(new Position(4, 7));
        expectedSubPath.addLast(new Position(4, 6));
        expectedSubPath.addLast(new Position(4, 5));
        expectedSubPath.addLast(new Position(4, 5));
        expectedSubPath.addLast(new Position(4, 6));
        expectedSubPath.addLast(new Position(4, 7));
        expectedSubPath.addLast(new Position(4, 8));
        expectedSubPath.addLast(new Position(4, 9));
        expectedSubPath.addLast(new Position(4, 10));
        expectedSubPath.addLast(new Position(4, 11));
        expectedSubPath.addLast(new Position(5, 11));
        expectedSubPath.addLast(new Position(4, 11));
        expectedSubPath.addLast(new Position(4, 12));

        assertEquals(obstacleFreePath, expectedSubPath);

        PriorityQueue<Position> weightSubPath = GlobalLevelService.getInstance()
                .weightedObstacleSubPath(obstacleFreePath, obstaclePosition);

        assertEquals(obstaclePosition, weightSubPath.poll());

        Position freeNeighbour = GlobalLevelService.getInstance().getFreeNeighbour(
                path,
                obstaclePosition,
                3
        );

        assertEquals(new Position(1, 6), freeNeighbour);
    }

    @Test
    public void testPush() {

    }

    @Test
    public void testPull() {

    }
}
