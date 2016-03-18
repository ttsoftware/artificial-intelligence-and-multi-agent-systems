package dtu;

import dtu.board.Level;
import org.junit.Test;

import java.io.*;

public class ProblemMarshallerTest {

    @Test
    public void testMarshall() throws IOException {

        String absolutePath = "/home/troels/Studie/DTU/artificial intelligence and multi-agent systems/assignments/03 Programming Project/searchclient/levels/SAD1.lvl";

        FileInputStream inputStream = new FileInputStream(absolutePath);
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream));

        // Use stderr to print to console
        System.err.println("PlannerClient initializing. I am sending this using the error output stream.");

        // Parse the level
        Level level = ProblemMarshaller.marshall(fileReader);

        System.out.println(level);
    }
}
