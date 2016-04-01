package dtu.agency;

import dtu.agency.board.BoardCell;
import dtu.agency.board.Level;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;

public class ProblemMarshallerTest {

    @Test
    public void testMarshall() throws IOException {

        File resourcesDirectory = new File("src/test/resources");
        String levelPath = resourcesDirectory.getAbsolutePath() + "/SAD1_goto_box.lvl";

        FileInputStream inputStream = new FileInputStream(levelPath);
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream));

        // Parse the level
        Level level = ProblemMarshaller.marshall(fileReader);

        assertEquals(level.getBoardState().length, 7);
        assertEquals(level.getBoardState()[0].length, 19);
        assertEquals(level.getBoardState()[0][0], BoardCell.WALL);
        assertEquals(level.getBoardState()[1][1], BoardCell.AGENT);

    }
}