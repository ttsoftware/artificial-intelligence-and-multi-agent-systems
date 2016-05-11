package dtu.agency.actions;

/**
 * Class to test concrete actions
 */

import dtu.agency.ProblemMarshaller;
import dtu.agency.ProblemMarshallerTest;
import dtu.agency.actions.concreteaction.*;
import dtu.agency.board.Agent;
import dtu.agency.board.Box;
import dtu.agency.board.Level;
import dtu.agency.board.Position;
import dtu.agency.planners.htn.HTNState;
import dtu.agency.planners.htn.RelaxationMode;
import dtu.agency.services.BDIService;
import dtu.agency.services.GlobalLevelService;
import dtu.agency.services.PlanningLevelService;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertTrue;

public class ConcreteActionTest {

    /*
    * This test class tests the Primitive actions when the are applied to different states
    * Author: Mads
    * */
    private static HTNState noc, non, nos, noe, now, acbn, acbs, acbe, acbw, anbc, asbc, aebc, awbc;
    private static HTNState anbne, anbnw, aebne, aebse, asbse, asbsw, awbnw, awbsw, aswbs, aswbw, anebn, anebe, acbne, acbnw, acbse, acbsw, asebe;
    private static Position pc, pn, ps, pe, pw, pne, pse, pnw, psw, z;
    private static Direction n, s, e, w;
    private static Box box;
    private static Level level;
    private static RelaxationMode mode = RelaxationMode.None;

    @BeforeClass
    public static void setUp() throws IOException {

        Level level = ProblemMarshallerTest.marshall("/action_test.lvl");

        GlobalLevelService.getInstance().setLevel(level);

        BDIService bdiService = new BDIService(new Agent("0"));
        BDIService.setInstance(bdiService);

        PlanningLevelService pls = new PlanningLevelService(level);

        //z  = new Position(0,0);
        z = null;

        /*  R
        *  C0 1 2 3
        *   1
        *   2     1
        *   3 0
        *   Position(R,C) -> Agent0 at Position(3,1)
        *                 -> Agent1 at Position(2,3)
        */

        n = Direction.NORTH;
        s = Direction.SOUTH;
        e = Direction.EAST;
        w = Direction.WEST;

        pc = new Position(2, 2);
        pn = new Position(1, 2);
        ps = new Position(3, 2);
        pe = new Position(2, 3);
        pw = new Position(2, 1);
        pne = new Position(1, 3);
        pnw = new Position(1, 1);
        pse = new Position(3, 3);
        psw = new Position(3, 1);

        // states where no box is present
        noc = new HTNState(pc, z, pls, mode);
        non = new HTNState(pn, z, pls, mode);
        nos = new HTNState(ps, z, pls, mode);
        noe = new HTNState(pe, z, pls, mode);
        now = new HTNState(pw, z, pls, mode);

        // states where box is neighbour to agent
        acbn = new HTNState(pc, pn, pls, mode);
        acbs = new HTNState(pc, ps, pls, mode);
        acbe = new HTNState(pc, pe, pls, mode);
        acbw = new HTNState(pc, pw, pls, mode);
        anbc = new HTNState(pn, pc, pls, mode);
        asbc = new HTNState(ps, pc, pls, mode);
        aebc = new HTNState(pe, pc, pls, mode);
        awbc = new HTNState(pw, pc, pls, mode);

        // states where box is NOT neighbour to agent
        acbne = new HTNState(pc, pne, pls, mode);
        acbnw = new HTNState(pc, pnw, pls, mode);
        acbse = new HTNState(pc, pse, pls, mode);
        acbsw = new HTNState(pc, psw, pls, mode);

        // other states
        anbne = new HTNState(pn, pne, pls, mode);
        anbnw = new HTNState(pn, pnw, pls, mode);
        aebne = new HTNState(pe, pne, pls, mode);
        aebse = new HTNState(pe, pse, pls, mode);
        asbse = new HTNState(ps, pse, pls, mode);
        asbsw = new HTNState(ps, psw, pls, mode);
        awbnw = new HTNState(pw, pnw, pls, mode);
        awbsw = new HTNState(pw, psw, pls, mode);

        aswbs = new HTNState(psw, ps, pls, mode);
        aswbw = new HTNState(psw, pw, pls, mode);
        anebn = new HTNState(pne, pn, pls, mode);
        anebe = new HTNState(pne, pe, pls, mode);
        asebe = new HTNState(pse, pe, pls, mode);

        box = new Box("A");

        File resourcesDirectory = new File("src/test/resources");
        String levelPath = resourcesDirectory.getAbsolutePath() + "/action_test.lvl";
        FileInputStream inputStream = new FileInputStream(levelPath);
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream));
        level = ProblemMarshaller.marshall(fileReader);

    }

    @Test
    public void nopTest() {
        GlobalLevelService.getInstance().setLevel(level);
        ConcreteAction nop = new NoConcreteAction();
        HTNState eff = acbn.applyConcreteAction(nop);
        assertTrue("Effect is not unchanged", eff.equals(acbn));
    }

    @Test
    public void moveTest() {
        GlobalLevelService.getInstance().setLevel(level);
        //System.err.println("printing walls");
        //System.err.println(GlobalLevelService.getInstance().getLevel().getWalls());
        //System.err.println(GlobalLevelService.getInstance().getLevel().notWall(pc));

        ConcreteAction goN = new MoveConcreteAction(n);
        ConcreteAction goS = new MoveConcreteAction(s);
        ConcreteAction goE = new MoveConcreteAction(e);
        ConcreteAction goW = new MoveConcreteAction(w);

        // applyTo is the only heuristic in need of testing
        // check that you end up the right place
        //System.err.println(noc.toString());
        //System.err.println(non.toString());
        //System.err.println(goN.toString());

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
        GlobalLevelService.getInstance().setLevel(level);
        // acbn - neighbor box to north - nb+ N S E W
        // acbne - box at north east - not neighbor b+ NE SE NW SW
        ConcreteAction pushNN = new PushConcreteAction(box, n, n);
        ConcreteAction pushNE = new PushConcreteAction(box, n, e);
        ConcreteAction pushNW = new PushConcreteAction(box, n, w);
        ConcreteAction pushNS = new PushConcreteAction(box, n, s); // invalid

        ConcreteAction pushSS = new PushConcreteAction(box, s, s);
        ConcreteAction pushSE = new PushConcreteAction(box, s, e);
        ConcreteAction pushSW = new PushConcreteAction(box, s, w);
        ConcreteAction pushSN = new PushConcreteAction(box, s, n); // invalid

        ConcreteAction pushEE = new PushConcreteAction(box, e, e);
        ConcreteAction pushEN = new PushConcreteAction(box, e, n);
        ConcreteAction pushES = new PushConcreteAction(box, e, s);
        ConcreteAction pushEW = new PushConcreteAction(box, e, w); // invalid

        ConcreteAction pushWW = new PushConcreteAction(box, w, w);
        ConcreteAction pushWN = new PushConcreteAction(box, w, n);
        ConcreteAction pushWS = new PushConcreteAction(box, w, s);
        ConcreteAction pushWE = new PushConcreteAction(box, w, e); // invalid

        // pushing straight
        assertTrue("pushing straight EE", aswbs.applyConcreteAction(pushEE).equals(asbse));
        assertTrue("pushing straight NN", aswbw.applyConcreteAction(pushNN).equals(awbnw));
        assertTrue("pushing straight WW", anebn.applyConcreteAction(pushWW).equals(anbnw));
        assertTrue("pushing straight SS", anebe.applyConcreteAction(pushSS).equals(aebse));

        // round corners
        assertTrue("pushing box NE:", acbn.applyConcreteAction(pushNE).equals(anbne));
        assertTrue("pushing box NW:", acbn.applyConcreteAction(pushNW).equals(anbnw));
        assertTrue("pushing box SE:", acbs.applyConcreteAction(pushSE).equals(asbse));
        assertTrue("pushing box SW:", acbs.applyConcreteAction(pushSW).equals(asbsw));
        assertTrue("pushing box EN:", acbe.applyConcreteAction(pushEN).equals(aebne));
        assertTrue("pushing box ES:", acbe.applyConcreteAction(pushES).equals(aebse));
        assertTrue("pushing box WN:", acbw.applyConcreteAction(pushWN).equals(awbnw));
        assertTrue("pushing box WS:", acbw.applyConcreteAction(pushWS).equals(awbsw));

        // illegal into walls
        assertTrue("pushing box into N wall", acbn.applyConcreteAction(pushNN) == null);
        assertTrue("pushing box into S wall", acbs.applyConcreteAction(pushSS) == null);
        assertTrue("pushing box into E wall", acbe.applyConcreteAction(pushEE) == null);
        assertTrue("pushing box into W wall", acbw.applyConcreteAction(pushWW) == null);

        // illegal diagonal (box out of reach)
        assertTrue("pushing box diagonal", acbnw.applyConcreteAction(pushNW) == null);
        assertTrue("pushing box diagonal", acbnw.applyConcreteAction(pushWN) == null);
        assertTrue("pushing box diagonal", acbne.applyConcreteAction(pushNE) == null);
        assertTrue("pushing box diagonal", acbne.applyConcreteAction(pushEN) == null);
        assertTrue("pushing box diagonal", acbsw.applyConcreteAction(pushWS) == null);
        assertTrue("pushing box diagonal", acbsw.applyConcreteAction(pushSW) == null);
        assertTrue("pushing box diagonal", acbse.applyConcreteAction(pushSE) == null);
        assertTrue("pushing box diagonal", acbse.applyConcreteAction(pushES) == null);

        // pushing illegal as pull
        assertTrue("pushing in a pull manor NS", acbs.applyConcreteAction(pushNS) == null);
        assertTrue("pushing in a pull manor SN", acbn.applyConcreteAction(pushSN) == null);
        assertTrue("pushing in a pull manor EW", acbw.applyConcreteAction(pushEW) == null);
        assertTrue("pushing in a pull manor WE", acbe.applyConcreteAction(pushWE) == null);

    }

    @Test
    public void pullTest() {
        GlobalLevelService.getInstance().setLevel(level);
        // acbn - neighbor box to north - nb+ N S E W
        // acbne - box at north east - not neighbor b+ NE SE NW SW
        ConcreteAction pullNN = new PullConcreteAction(box, n, n); // invalid
        ConcreteAction pullNE = new PullConcreteAction(box, n, e);
        ConcreteAction pullNW = new PullConcreteAction(box, n, w);
        ConcreteAction pullNS = new PullConcreteAction(box, n, s);

        ConcreteAction pullSS = new PullConcreteAction(box, s, s); // invalid
        ConcreteAction pullSE = new PullConcreteAction(box, s, e);
        ConcreteAction pullSW = new PullConcreteAction(box, s, w);
        ConcreteAction pullSN = new PullConcreteAction(box, s, n);

        ConcreteAction pullEE = new PullConcreteAction(box, e, e); // invalid
        ConcreteAction pullEN = new PullConcreteAction(box, e, n);
        ConcreteAction pullES = new PullConcreteAction(box, e, s);
        ConcreteAction pullEW = new PullConcreteAction(box, e, w);

        ConcreteAction pullWW = new PullConcreteAction(box, w, w); // invalid
        ConcreteAction pullWN = new PullConcreteAction(box, w, n);
        ConcreteAction pullWS = new PullConcreteAction(box, w, s);
        ConcreteAction pullWE = new PullConcreteAction(box, w, e);

        //System.err.println( acbe.toString() + " <-> " + acbe.applyConcreteAction(pullWE).toString() + " = " + awbc.toString() );

        // pulling straight
        assertTrue("pulling straight EW", acbw.applyConcreteAction(pullEW).equals(aebc));
        assertTrue("pulling straight NS", acbs.applyConcreteAction(pullNS).equals(anbc));
        assertTrue("pulling straight WE", acbe.applyConcreteAction(pullWE).equals(awbc));
        assertTrue("pulling straight SN", acbn.applyConcreteAction(pullSN).equals(asbc));

        // round corners 8 over the centre
        assertTrue("pulling box NE:", acbe.applyConcreteAction(pullNE).equals(anbc));
        assertTrue("pulling box NW:", acbw.applyConcreteAction(pullNW).equals(anbc));

        assertTrue("pulling box SE:", acbe.applyConcreteAction(pullSE).equals(asbc));
        assertTrue("pulling box SW:", acbw.applyConcreteAction(pullSW).equals(asbc));

        assertTrue("pulling box EN:", acbn.applyConcreteAction(pullEN).equals(aebc));
        assertTrue("pulling box ES:", acbs.applyConcreteAction(pullES).equals(aebc));

        assertTrue("pulling box WN:", acbn.applyConcreteAction(pullWN).equals(awbc));
        assertTrue("pulling box WS:", acbs.applyConcreteAction(pullWS).equals(awbc));

        // illegal into walls
        assertTrue("pulling box into N wall", anbc.applyConcreteAction(pullNS) == null);
        assertTrue("pulling box into S wall", asbc.applyConcreteAction(pullSN) == null);
        assertTrue("pulling box into E wall", aebc.applyConcreteAction(pullEW) == null);
        assertTrue("pulling box into W wall", awbc.applyConcreteAction(pullWE) == null);

        // illegal diagonal (box out of reach)
        assertTrue("pulling box diagonal", acbnw.applyConcreteAction(pullNW) == null);
        assertTrue("pulling box diagonal", acbnw.applyConcreteAction(pullNE) == null);
        assertTrue("pulling box diagonal", acbnw.applyConcreteAction(pullNS) == null);
        assertTrue("pulling box diagonal", acbnw.applyConcreteAction(pullSN) == null);
        assertTrue("pulling box diagonal", acbnw.applyConcreteAction(pullSE) == null);
        assertTrue("pulling box diagonal", acbnw.applyConcreteAction(pullSW) == null);
        assertTrue("pulling box diagonal", acbnw.applyConcreteAction(pullEW) == null);
        assertTrue("pulling box diagonal", acbnw.applyConcreteAction(pullEN) == null);
        assertTrue("pulling box diagonal", acbnw.applyConcreteAction(pullES) == null);
        assertTrue("pulling box diagonal", acbnw.applyConcreteAction(pullWE) == null);
        assertTrue("pulling box diagonal", acbnw.applyConcreteAction(pullWN) == null);
        assertTrue("pulling box diagonal", acbnw.applyConcreteAction(pullWS) == null);

        // illegal opposite as push
        assertTrue("pulling opposite push fashion EE", acbe.applyConcreteAction(pullEE) == null);
        assertTrue("pulling opposite push fashion NN", acbn.applyConcreteAction(pullNN) == null);
        assertTrue("pulling opposite push fashion WW", acbw.applyConcreteAction(pullWW) == null);
        assertTrue("pulling opposite push fashion SS", acbw.applyConcreteAction(pullSS) == null);

        // illegal ??
        assertTrue("pulling specific", aebc.applyConcreteAction(pullSE) == null);
        assertTrue("pulling specific", aebc.applyConcreteAction(pullSW).equals(asebe));

    }

    @Test
    public void directionTests() {
        assertTrue("North Failed", acbn.getDirectionToBox() == Direction.NORTH);
        assertTrue("South Failed", acbs.getDirectionToBox() == Direction.SOUTH);
        assertTrue("East Failed", acbe.getDirectionToBox() == Direction.EAST);
        assertTrue("West Failed", acbw.getDirectionToBox() == Direction.WEST);
    }

}