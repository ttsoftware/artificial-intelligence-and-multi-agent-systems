package dtu.agency.services;

import dtu.agency.ProblemMarshaller;
import dtu.agency.actions.ConcreteAction;
import dtu.agency.actions.concreteaction.Direction;
import dtu.agency.actions.concreteaction.MoveConcreteAction;
import dtu.agency.actions.concreteaction.PullConcreteAction;
import dtu.agency.actions.concreteaction.PushConcreteAction;
import dtu.agency.board.Agent;
import dtu.agency.board.Box;
import dtu.agency.board.Level;
import dtu.agency.board.Position;
import dtu.agency.planners.plans.PrimitivePlan;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
                BDIService.getInstance().getBDILevelService().getPosition(agent),
                BDIService.getInstance().getBDILevelService().getPosition(box),
                3
        );

        assertEquals(new Position(2, 8), freeNeighbour);
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
        Box boxA = new Box("A2");
        Box boxB = new Box("B0");
        Box boxC = new Box("C1");
        Box boxD = new Box("D3");

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
        plan.addAction(new MoveConcreteAction(Direction.WEST));
        plan.addAction(new MoveConcreteAction(Direction.WEST));
        plan.addAction(new MoveConcreteAction(Direction.WEST));
        plan.addAction(new MoveConcreteAction(Direction.WEST));
        plan.addAction(new MoveConcreteAction(Direction.WEST));
        plan.addAction(new MoveConcreteAction(Direction.WEST));
        plan.addAction(new MoveConcreteAction(Direction.WEST));
        plan.addAction(new MoveConcreteAction(Direction.WEST));
        plan.addAction(new MoveConcreteAction(Direction.WEST));
        plan.addAction(new MoveConcreteAction(Direction.WEST));
        plan.addAction(new MoveConcreteAction(Direction.WEST));
        plan.addAction(new MoveConcreteAction(Direction.WEST));
        plan.addAction(new MoveConcreteAction(Direction.WEST));
        plan.addAction(new MoveConcreteAction(Direction.WEST));

        LinkedList<Position> path = GlobalLevelService.getInstance().getOrderedPathWithBox(plan);

        Position obstacleAPosition = new Position(4, 8);
        Position obstacleBPosition = new Position(4, 1);
        Position obstacleCPosition = new Position(4, 4);
        Position obstacleDPosition = new Position(4, 13);

        LinkedList<Position> obstacleAFreePath = GlobalLevelService.getInstance()
                .getObstacleFreePath(
                        path,
                        BDIService.getInstance().getAgentCurrentPosition(),
                        obstacleAPosition
                );

        LinkedList<Position> expectedASubPath = new LinkedList<>();
        expectedASubPath.addLast(new Position(6, 11));
        expectedASubPath.addLast(new Position(5, 11));
        expectedASubPath.addLast(new Position(4, 11));
        expectedASubPath.addLast(new Position(4, 10));
        expectedASubPath.addLast(new Position(4, 9));
        expectedASubPath.addLast(new Position(4, 8));
        expectedASubPath.addLast(new Position(4, 7));
        expectedASubPath.addLast(new Position(4, 6));
        expectedASubPath.addLast(new Position(4, 5));
        expectedASubPath.addLast(new Position(4, 5));
        expectedASubPath.addLast(new Position(4, 6));
        expectedASubPath.addLast(new Position(4, 7));
        expectedASubPath.addLast(new Position(4, 8));
        expectedASubPath.addLast(new Position(4, 9));
        expectedASubPath.addLast(new Position(4, 10));
        expectedASubPath.addLast(new Position(4, 11));
        expectedASubPath.addLast(new Position(5, 11));
        expectedASubPath.addLast(new Position(4, 11));
        expectedASubPath.addLast(new Position(4, 12));
        expectedASubPath.addLast(new Position(4, 12));
        expectedASubPath.addLast(new Position(4, 11));
        expectedASubPath.addLast(new Position(4, 10));
        expectedASubPath.addLast(new Position(4, 9));
        expectedASubPath.addLast(new Position(4, 8));
        expectedASubPath.addLast(new Position(4, 7));
        expectedASubPath.addLast(new Position(4, 6));
        expectedASubPath.addLast(new Position(4, 5));

        assertEquals(expectedASubPath, obstacleAFreePath);

        // free neighbour for A
        Position freeNeighbourA = GlobalLevelService.getInstance().getFreeNeighbour(
                path,
                BDIService.getInstance().getBDILevelService().getPosition(agent),
                obstacleAPosition,
                4
        );

        assertEquals(new Position(1, 6), freeNeighbourA);

        GlobalLevelService.getInstance().removeBox(boxA);
        GlobalLevelService.getInstance().insertBox(boxA, new Position(1, 6));
        GlobalLevelService.getInstance().removeAgent(agent);
        GlobalLevelService.getInstance().insertAgent(agent, obstacleAPosition);

        BDIService.getInstance().updateBDILevelService();

        LinkedList<Position> obstacleCFreePath = GlobalLevelService.getInstance()
                .getObstacleFreePath(
                        path,
                        BDIService.getInstance().getAgentCurrentPosition(),
                        obstacleCPosition
                );

        assertTrue(obstacleCFreePath.contains(new Position(4, 5)));
        assertTrue(obstacleCFreePath.contains(new Position(4, 6)));

        // free neighbour for C
        Position freeNeighbourC = GlobalLevelService.getInstance().getFreeNeighbour(
                path,
                BDIService.getInstance().getBDILevelService().getPosition(agent),
                obstacleCPosition,
                3
        );

        //assertEquals(new Position(2, 6), freeNeighbourC);

        GlobalLevelService.getInstance().removeBox(boxC);
        GlobalLevelService.getInstance().insertBox(boxC, new Position(2, 6));
        GlobalLevelService.getInstance().removeAgent(agent);
        GlobalLevelService.getInstance().insertAgent(agent, obstacleCPosition);

        BDIService.getInstance().updateBDILevelService();

        // free neighbour for D
        Position freeNeighbourD = GlobalLevelService.getInstance().getFreeNeighbour(
                path,
                BDIService.getInstance().getBDILevelService().getPosition(agent),
                obstacleDPosition,
                2
        );

        assertEquals(new Position(3, 14), freeNeighbourD);

        GlobalLevelService.getInstance().removeBox(boxD);
        GlobalLevelService.getInstance().insertBox(boxD, new Position(3, 14));
        GlobalLevelService.getInstance().removeAgent(agent);
        GlobalLevelService.getInstance().insertAgent(agent, obstacleDPosition);
    }

    @Test
    public void testPush() {

    }

    @Test
    public void testPull() {

    }
}
