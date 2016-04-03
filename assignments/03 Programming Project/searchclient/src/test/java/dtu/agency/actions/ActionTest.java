package dtu.agency.actions;

/**
 * Created by koeus on 4/1/16.
 */

import dtu.agency.ProblemMarshaller;
import dtu.agency.agent.actions.Action;
import dtu.agency.agent.actions.Direction;
import dtu.agency.agent.actions.MoveAction;
import dtu.agency.agent.actions.NoAction;
import dtu.agency.board.Agent;
import dtu.agency.board.Goal;
import dtu.agency.board.Level;
import dtu.agency.board.Position;
import dtu.agency.planners.actions.effects.HTNEffect;
import dtu.agency.services.LevelService;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertTrue;

public class ActionTest {

    /*
    * This test class tests the Primitive actions when the are applied to different states
    * Author: Mads
    * */
    private static HTNEffect noc,non,nos,noe,now, nbn,nbs,nbe,nbw, bne,bnw,bse,bsw;
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
        noc = new HTNEffect(center,z);
        non = new HTNEffect(n,z);
        nos = new HTNEffect(s,z);
        noe = new HTNEffect(e,z);
        now = new HTNEffect(w,z);

        // states where box is neighbour to agent
        nbn = new HTNEffect(center,n);
        nbs = new HTNEffect(center,s);
        nbe = new HTNEffect(center,e);
        nbw = new HTNEffect(center,w);

        // states where box is NOT neighbour to agent
        bne = new HTNEffect(center,ne);
        bnw = new HTNEffect(center,nw);
        bse = new HTNEffect(center,se);
        bsw = new HTNEffect(center,sw);

        File resourcesDirectory = new File("src/test/resources");
        String levelPath = resourcesDirectory.getAbsolutePath() + "/action_test.lvl";
        FileInputStream inputStream = new FileInputStream(levelPath);
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream));
        level = ProblemMarshaller.marshall(fileReader);

    }

    @Test
    public void nopTest() {
        LevelService.getInstance().setLevel( level );
        Action nop = new NoAction();
        HTNEffect eff = nop.applyTo(nbn);
        assertTrue("Effect is not unchanged", eff.equals(nbn));
    }


    @Test
    public void moveTest() {
        LevelService.getInstance().setLevel( level );
        //System.err.println("printing walls");
        //System.err.println(LevelService.getInstance().getLevel().getWalls());
        //System.err.println(LevelService.getInstance().getLevel().notWall(center));

        Action goN = new MoveAction(Direction.NORTH);
        Action goS = new MoveAction(Direction.SOUTH);
        Action goE = new MoveAction(Direction.EAST);
        Action goW = new MoveAction(Direction.WEST);

        // applyTo is the only method in need of testing
        // check that you end up the right place
        assertTrue(non.equals(goN.applyTo(noc)));
        assertTrue(nos.equals(goS.applyTo(noc)));
        assertTrue(noe.equals(goE.applyTo(noc)));
        assertTrue(now.equals(goW.applyTo(noc)));

        assertTrue(noc.equals(goN.applyTo(nos)));
        assertTrue(noc.equals(goS.applyTo(non)));
        assertTrue(noc.equals(goE.applyTo(now)));
        assertTrue(noc.equals(goW.applyTo(noe)));

        // check that you do not end up the wrong place...
        assertTrue(!noc.equals(goN.applyTo(non)));
        assertTrue(!noc.equals(goS.applyTo(nos)));
        assertTrue(!noc.equals(goE.applyTo(noe)));
        assertTrue(!noc.equals(goW.applyTo(now)));
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