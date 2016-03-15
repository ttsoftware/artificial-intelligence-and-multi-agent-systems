package dtu;

import dtu.agent.AgentThread;
import dtu.board.Level;
import dtu.planners.HTNPlan;
import dtu.planners.HTNPlanner;
import dtu.planners.Plan;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class PlannerClient {

    public static void main(String[] args) throws Exception {

        BufferedReader serverMessages = new BufferedReader(new InputStreamReader(System.in));

        // Use stderr to print to console
        System.err.println("PlannerClient initializing. I am sending this using the error output stream.");

        // Create the level
        Level level = ProblemMarshaller.marshall(serverMessages);

        HTNPlanner planner = new HTNPlanner(level);
        List<Plan> plans = planner.plan();

        plans.forEach(plan -> {
            // Start a new thread (agent) for each plan
            new Thread(new AgentThread((HTNPlan) plan)).start();
        });
    }
}