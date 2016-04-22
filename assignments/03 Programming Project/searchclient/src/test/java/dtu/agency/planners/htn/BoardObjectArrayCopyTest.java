package dtu.agency.planners.htn;

import dtu.agency.board.*;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Created by koeus on 4/22/16.
 */
public class BoardObjectArrayCopyTest {

    private static BoardObject[][] a,b,c,d;

    @BeforeClass
    public static void setUp() throws IOException {
        a = new BoardObject[2][2];
        a[0][0] = new Agent("0");
        a[1][0] = null;
        a[0][1] = new Box("A0");
        a[1][1] = new BoxAndGoal(new Box("B1"), new Goal("b1", new Position(1,1), 0));

        b = new BoardObject[2][2];
        b[0][0] = new Agent("0");
        b[1][0] = null;
        b[0][1] = new Box("A0");
        b[1][1] = new BoxAndGoal(new Box("B1"), new Goal("b1", new Position(1,1), 0));

        c = a.clone();

        d = BoardObject.deepCopy(a);

    }



    @Test
    public void independentTest() {
        System.err.println( "a11 " + a[1][1] + ",b11 " + b[1][1]);
        assertTrue(a[1][1].equals(b[1][1]));

        b[1][1] = new Box("C2"); // was BOX_GOAL in both a and b
        System.err.println( "b11 = box C2");
        System.err.println( "a11 " + a[1][1] + ",b11 " + b[1][1]);
        assertTrue(!(a[1][1].equals(b[1][1])));
    }

    @Test
    public void deepCopyTest() {
        System.err.println("a11 " + a[1][1] + ",d11 " + d[1][1]);
        assertTrue(a[1][1].equals(d[1][1]));

        d[1][1] = new Box("C2"); // was BOX_GOAL in a
        System.err.println( "d11 = box C2");
        System.err.println( "a11 " + a[1][1] + ",d11 " + d[1][1]);
        System.err.println( "a11 == d11: "+ (a[1][1].equals(d[1][1])));
        assertTrue(!(a[1][1].equals(d[1][1])));
    }

    @Test
    public void cloneTest() {
        System.err.println( "a11 " + a[1][1] + ",c11 " + c[1][1]);
        assertTrue(a[1][1].equals(c[1][1]));

        c[1][1] = new Box("C2"); // was BOX_GOAL in a
        System.err.println( "c11 = box C2");
        System.err.println( "a11 " + a[1][1] + ",c11 " + c[1][1]);
        System.err.println( "a11 == c11: "+ (a[1][1].equals(c[1][1])));
        assertTrue(!(a[1][1].equals(c[1][1])));
    }


}
