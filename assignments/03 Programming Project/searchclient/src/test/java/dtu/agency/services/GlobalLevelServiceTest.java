package dtu.agency.services;

import dtu.agency.ProblemMarshaller;
import dtu.agency.actions.concreteaction.Direction;
import dtu.agency.actions.concreteaction.MoveConcreteAction;
import dtu.agency.board.Agent;
import dtu.agency.board.Level;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GlobalLevelServiceTest {

    private static File resourcesDirectory = new File("src/test/resources");

    @Before
    public void setUp() throws IOException {
        String levelPath = resourcesDirectory.getAbsolutePath() + "/SAD1_multi_1_agent_wins.lvl";

        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(levelPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream));

        // Parse the level
        Level level = ProblemMarshaller.marshall(fileReader);

        GlobalLevelService.getInstance().setLevel(level);
    }

    @Test
    public void applyAction() {
        System.out.println(GlobalLevelService.getInstance().getLevel());

        boolean successfullMove = GlobalLevelService.getInstance().applyAction(
                new Agent("1"),
                new MoveConcreteAction(Direction.NORTH)
        );
        assertTrue(successfullMove);

        System.out.println(GlobalLevelService.getInstance().getLevel());

        String state = GlobalLevelService.getInstance().getLevel().toString();
        String expectedState =
            "+++++++++++++++++\n" +
            "+0     + b+     +\n" +
            "+      + B+     +\n" +
            "+      +  +     +\n" +
            "+      +  +aA   +\n" +
            "+      + 1++    +\n" +
            "+               +\n" +
            "+++++++++++++++++\n";
        assertEquals(state, expectedState);
    }

    @Test
    public void testMove() {

    }

    @Test
    public void testPush() {

    }

    @Test
    public void testPull() {

    }
}
