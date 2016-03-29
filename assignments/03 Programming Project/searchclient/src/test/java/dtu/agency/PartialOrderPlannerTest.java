package dtu.agency;

import dtu.agency.agent.actions.*;
import dtu.agency.board.Agent;
import dtu.agency.board.Box;
import dtu.agency.board.Level;
import dtu.agency.board.Position;
import dtu.agency.planners.Searcher;
import dtu.agency.planners.actions.preconditions.AgentAtPrecondition;
import dtu.agency.planners.actions.preconditions.BoxAtPrecondition;
import dtu.agency.planners.actions.preconditions.FreeCellPrecondition;
import dtu.agency.planners.actions.preconditions.Precondition;
import org.junit.Test;

import java.io.*;
import java.util.List;
import java.util.PriorityQueue;

import static org.junit.Assert.assertEquals;

public class PartialOrderPlannerTest {

    String filePath = "/home/rasmus/MEGA/uni/DTU/Artificial Intelligence and Multiagent Systems/artificial-intelligence-and-multi-agent-systems/assignments/03 Programming Project/searchclient/levels/SAD1.lvl";

    FileInputStream inputStream = new FileInputStream(filePath);
    BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream));

    // Parse the level
    Level level = ProblemMarshaller.marshall(fileReader);
    Agent agent = level.getAgents().get(0);
    Box box = level.getBoxes().get(0);

    Searcher searcher = new Searcher(level, agent);

    Position agentPosition = level.getBoardObjectPositions().get(agent.getLabel());
    Position boxPosition = level.getBoardObjectPositions().get(box.getLabel());

    public PartialOrderPlannerTest() throws IOException {
    }

    @Test
    public void testMovePreconditions() {

        MoveAction moveAction = new MoveAction(Direction.EAST, agent, agentPosition);

        List<Precondition> preconditions = moveAction.getPreconditions();

        assertEquals (preconditions.size(), 1);

        AgentAtPrecondition agentAtPrecondition = (AgentAtPrecondition) preconditions.get(0);

        assertEquals(agentAtPrecondition.getAgent(), agent);

        assertEquals(agentAtPrecondition.getAgentPosition().getRow(), 1);
        assertEquals(agentAtPrecondition.getAgentPosition().getColumn(), 1);
    }

    @Test
    public void testPushPreconditions() {

        PushAction pushAction = new PushAction(box, boxPosition, agent, agentPosition, Direction.EAST, Direction.NORTH);

        List<Precondition> preconditions = pushAction.getPreconditions();

        assertEquals (preconditions.size(), 1);

        BoxAtPrecondition boxAtPrecondition = (BoxAtPrecondition) preconditions.get(0);

        assertEquals(boxAtPrecondition.getBox(), box);
        assertEquals(boxAtPrecondition.getAgent(), agent);

        assertEquals(boxAtPrecondition.getBoxPosition().getRow(), 4);
        assertEquals(boxAtPrecondition.getBoxPosition().getColumn(), 17);
    }

    @Test
    public void testPullPreconditions() {

        PullAction pullAction = new PullAction(box, boxPosition, agent, agentPosition, Direction.EAST, Direction.NORTH);

        List<Precondition> preconditions = pullAction.getPreconditions();

        assertEquals (preconditions.size(), 1);

        BoxAtPrecondition boxAtPrecondition = (BoxAtPrecondition) preconditions.get(0);

        assertEquals(boxAtPrecondition.getBox(), box);
        assertEquals(boxAtPrecondition.getAgent(), agent);

        assertEquals(boxAtPrecondition.getBoxPosition().getRow(), 4);
        assertEquals(boxAtPrecondition.getBoxPosition().getColumn(), 17);
    }

    @Test
    public void testSolveAgentAtPrecondition() {
        AgentAtPrecondition agentAtPrecondition = new AgentAtPrecondition(agent, new Position(5, 5));

        PriorityQueue<Action> actions = searcher.solvePrecondition(agentAtPrecondition);

        assertEquals(3, actions.size());

        for (Action action : actions) {
            System.out.println(action.toString());
            System.out.println(action.getHeuristic());
        }
    }

    @Test
    public void testSolveBoxAtPrecondition() {
        BoxAtPrecondition boxAtPrecondition = new BoxAtPrecondition(box, agent, new Position(5, 5));

        PriorityQueue<Action> actions = searcher.solvePrecondition(boxAtPrecondition);

        for (Action action : actions) {
            System.out.println(action.toString());
            System.out.println(action.getHeuristic());
        }
    }
}
