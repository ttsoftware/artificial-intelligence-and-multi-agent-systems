package dtu.agency;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class PlannerClientTest {

    @Test
    public void testMain() throws IOException {

        String filePath = "/home/troels/Studie/DTU/artificial intelligence and multi-agent systems/assignments/03 Programming Project/searchclient/levels/SAD1.lvl";

        FileInputStream inputStream = new FileInputStream(filePath);
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream));

        
    }
}