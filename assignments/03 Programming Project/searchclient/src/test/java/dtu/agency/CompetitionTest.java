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

    @Test
    public void test0() throws Exception {
        testLevel("single_agent/SAbotbot.lvl");
    }

    @Test
    public void test1() throws Exception {
        testLevel("single_agent/SANoOp.lvl");
    }

    @Test
    public void test2() throws Exception {
        testLevel("single_agent/SATAIM.lvl");
    }

    @Test
    public void test3() throws Exception {
        testLevel("single_agent/SAAIMuffins.lvl");
    }

    @Test
    public void test4() throws Exception {
        testLevel("single_agent/SATheAgency.lvl");
    }

    @Test
    public void test5() throws Exception {
        testLevel("multi_agent/MATheAgency.lvl");
    }

    @Test
    public void test6() throws Exception {
        testLevel("single_agent/SASojourner.lvl");
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