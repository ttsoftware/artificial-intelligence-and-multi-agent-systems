package dtu.agency;

import dtu.agency.board.BoardCell;
import dtu.agency.board.Level;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;

public class ProblemMarshallerTest {
    private static File resourcesDirectory = new File("src/test/resources");

    public static Level marshall(String path) throws IOException {
        String levelPath = resourcesDirectory.getAbsolutePath() + path;
        FileInputStream inputStream = new FileInputStream(levelPath);
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream));
        return ProblemMarshaller.marshall(fileReader);
    }

    @Test
    public void testMarshall() throws IOException {
        String levelPath = resourcesDirectory.getAbsolutePath() + "/SAD1_goto_box.lvl";

        FileInputStream inputStream = new FileInputStream(levelPath);
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream));

        // Parse the level
        Level level = ProblemMarshaller.marshall(fileReader);

        assertEquals(level.getBoardState().length, 7);
        assertEquals(level.getBoardState()[0].length, 19);
        assertEquals(level.getBoardState()[0][0], BoardCell.WALL);
        assertEquals(level.getBoardState()[1][1], BoardCell.AGENT);
        assertEquals(level.getBoxesGoals().get("A0").get(0).getLabel(), "a0");
        assertEquals(level.getGoalsBoxes().get("a0").get(0).getLabel(), "A0");
    }
}