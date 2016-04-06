package dtu.agency.actions;

/**
 * Created by koeus on 4/1/16.
 */

import dtu.agency.ProblemMarshaller;
import dtu.agency.actions.concreteaction.*;
import dtu.agency.actions.concreteaction.MoveConcreteAction;
import dtu.agency.board.Level;
import dtu.agency.board.Position;
import dtu.agency.planners.htn.HTNState;
import dtu.agency.services.LevelService;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertTrue;

public class ConcreteActionTest {

    /*
    * This test class tests the Primitive actions when the are applied to different states
    * Author: Mads
    * */
    private static HTNState noc,non,nos,noe,now, nbn,nbs,nbe,nbw, bne,bnw,bse,bsw;
    private static Position center, n,s,e,w, ne,se,nw,sw, z;
    private static Level level;

    @BeforeClass
    public static void setUp() throws IOException {

        center = new Position(2,2);
        //z  = new Position(0,0);
        z  = null;

        /*  R
        *  C0 1 2 3
        *   1
        *   2     1
        *   3 0
        *   Position(R,C) -> Agent0 at Position(3,1)
        *                 -> Agent1 at Position(2,3)
        */

        n  = new Position(1,2);
        s  = new Position(3,2);
        e  = new Position(2,3);
        w  = new Position(2,1);
        ne = new Position(1,3);
        nw = new Position(1,1);
        se = new Position(3,3);
        sw = new Position(3,1);

        // states where no box is present
        noc = new HTNState(center,z);
        non = new HTNState(n,z);
        nos = new HTNState(s,z);
        noe = new HTNState(e,z);
        now = new HTNState(w,z);

        // states where box is neighbour to agent
        nbn = new HTNState(center,n);
        nbs = new HTNState(center,s);
        nbe = new HTNState(center,e);
        nbw = new HTNState(center,w);

        // states where box is NOT neighbour to agent
        bne = new HTNState(center,ne);
        bnw = new HTNState(center,nw);
        bse = new HTNState(center,se);
        bsw = new HTNState(center,sw);

        File resourcesDirectory = new File("src/test/resources");
        String levelPath = resourcesDirectory.getAbsolutePath() + "/action_test.lvl";
        FileInputStream inputStream = new FileInputStream(levelPath);
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream));
        level = ProblemMarshaller.marshall(fileReader);

    }

    @Test
    public void nopTest() {
        LevelService.getInstance().setLevel( level );
        ConcreteAction nop = new NoConcreteAction();
        HTNState eff = nbn.applyConcreteAction(nop);
        assertTrue("Effect is not unchanged", eff.equals(nbn));
    }

    @Test
    public void moveTest() {
        LevelService.getInstance().setLevel( level );
        //System.err.println("printing walls");
        //System.err.println(LevelService.getInstance().getLevel().getWalls());
        //System.err.println(LevelService.getInstance().getLevel().notWall(center));

        ConcreteAction goN = new MoveConcreteAction(Direction.NORTH);
        ConcreteAction goS = new MoveConcreteAction(Direction.SOUTH);
        ConcreteAction goE = new MoveConcreteAction(Direction.EAST);
        ConcreteAction goW = new MoveConcreteAction(Direction.WEST);

        // applyTo is the only heuristic in need of testing
        // check that you end up the right place
        assertTrue(non.equals(noc.applyConcreteAction(goN)));
        assertTrue(nos.equals(noc.applyConcreteAction(goS)));
        assertTrue(noe.equals(noc.applyConcreteAction(goE)));
        assertTrue(now.equals(noc.applyConcreteAction(goW)));

        assertTrue(noc.equals(nos.applyConcreteAction(goN)));
        assertTrue(noc.equals(non.applyConcreteAction(goS)));
        assertTrue(noc.equals(now.applyConcreteAction(goE)));
        assertTrue(noc.equals(noe.applyConcreteAction(goW)));

        // check that you do not end up the wrong place...
        assertTrue(!noc.equals(non.applyConcreteAction(goN)));
        assertTrue(!noc.equals(nos.applyConcreteAction(goS)));
        assertTrue(!noc.equals(noe.applyConcreteAction(goE)));
        assertTrue(!noc.equals(now.applyConcreteAction(goW)));
    }

    @Test
    public void pushTest() {
        assertTrue(1==2);
    }

    @Test
    public void pullTest() {
        assertTrue(1==2);
    }
}