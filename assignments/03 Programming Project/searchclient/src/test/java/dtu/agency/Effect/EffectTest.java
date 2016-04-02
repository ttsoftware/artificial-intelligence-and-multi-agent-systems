package dtu.agency.Effect;

/**
 * Created by koeus on 4/1/16.
 */

import dtu.agency.board.Agent;
import dtu.agency.board.Goal;
import dtu.agency.board.Position;
import dtu.agency.planners.actions.effects.HTNEffect;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.HashSet;

import static org.junit.Assert.assertTrue;

public class EffectTest {

    private static Position agentOne, agentTwo, agentThree;
    private static Position boxOne, boxTwo, boxThree;
    private static HTNEffect a, b, c;

    @BeforeClass
    public static void setUp() throws IOException {
        agentOne = new Position(1, 1);
        agentTwo = new Position(1, 2);
        agentThree = new Position(1, 2);
        boxOne = new Position(2, 1);
        boxTwo = new Position(2, 2);
        boxThree = new Position(2, 2);

        a = new HTNEffect(agentOne, boxOne);
        b = new HTNEffect(agentTwo, boxTwo);     // b == c
        c = new HTNEffect(agentThree, boxThree);
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

        HashSet<HTNEffect> hs = new HashSet<>();
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



}