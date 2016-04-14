package dtu.agency.planners.htn;

import dtu.agency.ProblemMarshallerTest;
import dtu.agency.board.Level;
import dtu.agency.board.Position;
import dtu.agency.services.PlanningLevelService;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.HashSet;

import static org.junit.Assert.assertTrue;

public class HTNStateTest {

    private static Position agentOne, agentTwo, agentThree;
    private static Position boxOne, boxTwo, boxThree;
    private static HTNState a, b, c;
    private static RelaxationMode mode = RelaxationMode.None;

    @BeforeClass
    public static void setUp() throws IOException {
        agentOne = new Position(1, 1);
        agentTwo = new Position(1, 2);
        agentThree = new Position(1, 2);
        boxOne = new Position(2, 1);
        boxTwo = new Position(2, 2);
        boxThree = new Position(2, 2);
        Level level = ProblemMarshallerTest.marshall("/action_test.lvl");
        PlanningLevelService pls = new PlanningLevelService(level);
        a = new HTNState(agentOne, boxOne, pls, mode);
        b = new HTNState(agentTwo, boxTwo, pls, mode);     // b == c
        c = new HTNState(agentThree, boxThree, pls, mode);
    }

    @Test
    public void equalsTest() {

        System.err.println("Eff a  : " + a.toString());
        System.err.println("Eff b  : " + b.toString());
        System.err.println("Eff c  : " + c.toString());

        assertTrue(!a.equals(b));
        assertTrue(!a.equals(c));
        assertTrue(b.equals(c));

    }

    @Test
    public void hashTest() {
        System.err.println("hash a : " + Integer.toString(a.hashCode()));
        System.err.println("hash b : " + Integer.toString(b.hashCode()));
        System.err.println("hash c : " + Integer.toString(c.hashCode()));

        assertTrue(a.hashCode() != b.hashCode());
        assertTrue(a.hashCode() != c.hashCode());
        assertTrue(b.hashCode() == c.hashCode());

        HashSet<HTNState> hs = new HashSet<>();
        assertTrue(hs.size() == 0);
        hs.add(a);
        hs.add(b);
        hs.add(c);
        System.err.println(hs);

        assertTrue(hs.size() == 2);
    }

    @Test
    public void isLegalTest() {
        // to be written
        assertTrue(1 == 2);
    }

    @Test
    public void concreteActionApplication() {
        // to be written
        assertTrue(1 == 2);
    }

    @Test
    public void abstractActionGoalChecks() {
        // to be written
        assertTrue(1 == 2);
    }

}