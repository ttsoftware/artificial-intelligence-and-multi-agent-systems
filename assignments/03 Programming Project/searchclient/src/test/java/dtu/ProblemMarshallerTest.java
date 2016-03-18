package dtu;

import dtu.board.BoardCell;
import dtu.board.Level;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.junit.Assert.assertEquals;

public class ProblemMarshallerTest {

    @Test
    public void testMarshall() throws IOException {

        String filePath = "/home/troels/Studie/DTU/artificial intelligence and multi-agent systems/assignments/03 Programming Project/searchclient/levels/SAD1.lvl";

        FileInputStream inputStream = new FileInputStream(filePath);
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream));

        // Parse the level
        Level level = ProblemMarshaller.marshall(fileReader);

        assertEquals(level.getBoardState().length, 7);
        assertEquals(level.getBoardState()[0].length, 19);
        assertEquals(level.getBoardState()[0][0], BoardCell.WALL);
    }
}
