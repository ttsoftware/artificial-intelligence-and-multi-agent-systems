package dtu.agency;

import dtu.agency.board.*;
import dtu.agency.services.GlobalLevelService;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CompetitionTest {

    private static File resourcesDirectory = new File("competition_levels/");

    @Test // Assumption of independant goal ques not fulfilled - semantic error
    public void test0() throws Exception {
        testLevel("single_agent/SAbotbot.lvl");
    }

    // The function making the tests run, checking the results
    public void testLevel(String level) throws Exception {

        String path = resourcesDirectory.getAbsolutePath() + "/" + level;
        FileInputStream inputStream = new FileInputStream(path);

        System.setIn(inputStream);

        PlannerClient.main(new String[]{});

        List<Goal> goals = GlobalLevelService.getInstance().getLevel().getGoals();
        goals.forEach(goal -> {
            Position goalPosition = GlobalLevelService.getInstance().getPosition(goal);
            BoardObject positionObject = GlobalLevelService.getInstance().getObject(goalPosition);
            BoardCell positionCell = GlobalLevelService.getInstance().getCell(goalPosition);

            assertEquals(BoardCell.BOX_GOAL, positionCell);

            BoxAndGoal boxAndGoal = (BoxAndGoal) positionObject;

            assertTrue(boxAndGoal.isSolved());
        });
    }
}