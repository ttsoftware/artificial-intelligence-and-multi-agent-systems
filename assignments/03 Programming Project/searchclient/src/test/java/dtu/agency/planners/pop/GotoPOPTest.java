package dtu.agency.planners.pop;

import dtu.agency.ProblemMarshaller;
import dtu.agency.board.Agent;
import dtu.agency.board.Goal;
import dtu.agency.board.Level;
import dtu.agency.services.GlobalLevelService;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

public class GotoPOPTest {

    private static Agent agent;
    private static Goal goal;

    @BeforeClass
    public static void setUp() throws IOException {
        File resourcesDirectory = new File("src/test/resources");
        String levelPath = resourcesDirectory.getAbsolutePath() + "/competition_levels/single_agent/SAteamhal.lvl";

        FileInputStream inputStream = new FileInputStream(levelPath);
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream));

        // Parse the level
        Level level = ProblemMarshaller.marshall(fileReader);
        GlobalLevelService.getInstance().setLevel(level);

        agent = level.getAgents().get(0);
        goal = level.getGoals().get(0);
    }

    @Test
    public void planTest() {
        GotoPOP gotoPlanner = new GotoPOP();

        List<PriorityBlockingQueue<Goal>> goalQueueList = gotoPlanner.getWeighedGoals();

        assert(goalQueueList.size() > 0);
    }

    @Test
    public void solvePreconditionTest() {

    }

    @Test
    public void getOpenPreconditionsTest() {

    }

    @Test
    public void isOpenPreconditionTest() {

    }
}
