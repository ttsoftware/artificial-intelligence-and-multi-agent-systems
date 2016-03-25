package dtu.agency;

import dtu.agency.agent.actions.Direction;
import dtu.agency.agent.actions.MoveAction;
import dtu.agency.agent.actions.PushAction;
import dtu.agency.board.Agent;
import dtu.agency.board.Box;
import dtu.agency.board.Level;
import dtu.agency.board.Position;
import dtu.agency.planners.actions.preconditions.AgentAtPrecondition;
import dtu.agency.planners.actions.preconditions.BoxAtPrecondition;
import dtu.agency.planners.actions.preconditions.FreeCellPrecondition;
import dtu.agency.planners.actions.preconditions.Precondition;
import org.junit.Test;

import java.io.*;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PartialOrderPlannerTest {

    String filePath = "/home/rasmus/MEGA/uni/DTU/Artificial Intelligence and Multiagent Systems/artificial-intelligence-and-multi-agent-systems/assignments/03 Programming Project/searchclient/levels/SAD1.lvl";

    FileInputStream inputStream = new FileInputStream(filePath);
    BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream));

    // Parse the level
    Level level = ProblemMarshaller.marshall(fileReader);

    Agent agent = level.getAgents().get(0);
    Box box = level.getBoxes().get(0);

    Position agentPosition = level.getBoardObjectPositions().get(agent.getLabel());
    Position boxPosition = level.getBoardObjectPositions().get(box.getLabel());

    public PartialOrderPlannerTest() throws IOException {
    }

    @Test
    public void testMovePreconditions() {

        MoveAction moveAction = new MoveAction(Direction.EAST, agent, agentPosition);

        List<Precondition> preconditions = moveAction.getPreconditions();

        assertEquals (preconditions.size(), 2);

        FreeCellPrecondition freeCellPrecondition = (FreeCellPrecondition) preconditions.get(0);
        AgentAtPrecondition agentAtPrecondition = (AgentAtPrecondition) preconditions.get(1);

        assertEquals(freeCellPrecondition.getPosition().getRow(), 1);
        assertEquals(freeCellPrecondition.getPosition().getColumn(), 2);

        assertEquals(agentAtPrecondition.getAgent(), agent);

        assertEquals(agentAtPrecondition.getAgentPosition().getRow(), 1);
        assertEquals(agentAtPrecondition.getAgentPosition().getColumn(), 1);
    }

    @Test
    public void testPushPreconditions() {

        PushAction pushAction = new PushAction(box, boxPosition, agent, agentPosition, Direction.EAST, Direction.NORTH);

        List<Precondition> preconditions = pushAction.getPreconditions();

        assertEquals (preconditions.size(), 3);

        FreeCellPrecondition freeCellPrecondition = (FreeCellPrecondition) preconditions.get(0);
        BoxAtPrecondition boxAtPrecondition = (BoxAtPrecondition) preconditions.get(1);
        AgentAtPrecondition agentAtPrecondition = (AgentAtPrecondition) preconditions.get(2);

        assertEquals(freeCellPrecondition.getPosition().getRow(), 3);
        assertEquals(freeCellPrecondition.getPosition().getColumn(), 17);

        assertEquals(boxAtPrecondition.getBox(), box);
        assertEquals(agentAtPrecondition.getAgent(), agent);

        assertEquals(boxAtPrecondition.getBoxPosition().getRow(), 4);
        assertEquals(boxAtPrecondition.getBoxPosition().getColumn(), 17);

        assertEquals(agentAtPrecondition.getAgentPosition().getRow(), 3);
        assertEquals(agentAtPrecondition.getAgentPosition().getColumn(), 17);
    }
}
