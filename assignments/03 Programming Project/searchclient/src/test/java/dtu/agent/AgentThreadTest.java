package dtu.agent;

import dtu.board.Agent;
import org.junit.Test;

public class AgentThreadTest {

    @Test(expected = IllegalArgumentException.class)
    public void testRun() {

        Agent agent = new Agent();

        AgentThread thread = new AgentThread(agent);
        // thread.run();
    }
}
