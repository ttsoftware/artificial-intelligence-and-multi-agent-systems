package dtu.agency.planners.htn;

import dtu.agency.board.BoardCell;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Created by koeus on 4/22/16.
 */
public class BoardCellArrayCopyTest {

    private static BoardCell[][] a,b,c,d;

    @BeforeClass
    public static void setUp() throws IOException {
        a = new BoardCell[2][2];
        a[0][0] = BoardCell.AGENT;
        a[1][0] = BoardCell.BOX;
        a[0][1] = BoardCell.FREE_CELL;
        a[1][1] = BoardCell.BOX_GOAL;

        b = new BoardCell[2][2];
        b[0][0] = BoardCell.AGENT;
        b[1][0] = BoardCell.BOX;
        b[0][1] = BoardCell.FREE_CELL;
        b[1][1] = BoardCell.BOX_GOAL;

        c = a.clone();

        d = BoardCell.deepCopy(a);

    }

    @Test
    public void independentTest() {
        // System.err.println( "a11 " + a[1][1] + ",b11 " + b[1][1]);
        assertTrue(a[1][1] == b[1][1]);

        b[1][1] = BoardCell.FREE_CELL; // was BOX_GOAL in both a and b
        // System.err.println( "b11 = FreeCell");
        // System.err.println( "a11 " + a[1][1] + ",b11 " + b[1][1]);
        assertTrue(a[1][1] != b[1][1]);
    }

    @Test
    public void cloneTest() {
        // System.err.println( "a11 " + a[1][1] + ",c11 " + c[1][1]);
        assertTrue(a[1][1] == c[1][1]);

        c[1][1] = BoardCell.FREE_CELL; // was BOX_GOAL in a
        // System.err.println( "c11 = FreeCell");
        // System.err.println( "a11 " + a[1][1] + ",c11 " + c[1][1]);
        assertTrue(a[1][1] != c[1][1]);
    }

    @Test
    public void deepCopyTest() {
        // System.err.println("a11 " + a[1][1] + ",d11 " + d[1][1]);
        assertTrue(a[1][1] == d[1][1]);

        d[1][1] = BoardCell.FREE_CELL; // was BOX_GOAL in a
        // System.err.println( "d11 = FreeCell");
        // System.err.println( "a11 " + a[1][1] + ",d11 " + d[1][1]);
        assertTrue(a[1][1] != d[1][1]);
    }

}
