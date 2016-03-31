package dtu.agency.planners.htn;

import dtu.agency.ProblemMarshaller;
import dtu.agency.board.Agent;
import dtu.agency.board.Goal;
import dtu.agency.board.Level;
import dtu.agency.services.LevelService;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;

public class HTNPlannerTest {

    @Test
    public void testPlan() throws IOException {

        File resourcesDirectory = new File("src/test/resources");
        String levelPath = resourcesDirectory.getAbsolutePath() + "/SAD1_goto_box.lvl";

        FileInputStream inputStream = new FileInputStream(levelPath);
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream));

        Level level = ProblemMarshaller.marshall(fileReader);
        LevelService.getInstance().setLevel(level);

        // We take the first agent
        Agent agent = LevelService.getInstance().getLevel().getAgents().get(0);

        // We take the first goal
        Goal goal = LevelService.getInstance().getLevel().getGoals().get(0);

        HTNPlanner planner = new HTNPlanner(agent, goal);

        HTNPlan plan = planner.plan();

        assertEquals(plan.totalEstimatedDistance(), 20);
    }
}
