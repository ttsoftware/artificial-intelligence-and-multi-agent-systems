package dtu;

import dtu.board.Level;
import dtu.planners.HTNPlanner;
import dtu.planners.Plan;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

        List<Thread> agentThreads = new ArrayList<>();

        throw new Exception("Concurrency not yet implemented");

        /*
        plans.forEach(plan -> {
            // Start a new thread (agent) for each plan
            Thread t = new Thread(new AgentThread((HTNPlan) plan));
            agentThreads.add(t);
            t.start();
        });

        // wait for all threads to finish
        agentThreads.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        */
    }
}