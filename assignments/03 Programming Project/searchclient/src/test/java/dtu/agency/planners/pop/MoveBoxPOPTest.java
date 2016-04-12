package dtu.agency.planners.pop;

import dtu.agency.ProblemMarshaller;
import dtu.agency.board.Agent;
import dtu.agency.board.Goal;
import dtu.agency.board.Level;
import dtu.agency.services.GlobalLevelService;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;

public class MoveBoxPOPTest {

    private static Agent agent;
    private static Goal goal;

    @BeforeClass
    public static void setUp() throws IOException {
        File resourcesDirectory = new File("src/test/resources");
        String levelPath = resourcesDirectory.getAbsolutePath() + "/SAD1_move_box.lvl";

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
        /*
        HTNPlan htnPlan = new HTNPlanner(agent, goal).plan();

        MoveBoxPOP moveBoxPlanner = new MoveBoxPOP(agent);

        POPPlan popPlan = moveBoxPlanner.plan((MoveBoxAbstractAction) htnPlan.getActions().get(1));

        assertTrue(popPlan.getActions().size() > 0);
        */
    }
}
