package dtu.agency.agent;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import dtu.agency.agent.bdi.Ideas;
import dtu.agency.board.Agent;
import dtu.agency.board.BoardObject;
import dtu.agency.board.Goal;
import dtu.agency.board.Position;
import dtu.agency.events.agency.GoalAssignmentEvent;
import dtu.agency.events.agency.GoalOfferEvent;
import dtu.agency.events.agency.MoveObstacleAssignmentEvent;
import dtu.agency.events.agency.MoveObstacleOfferEvent;
import dtu.agency.events.agent.GoalEstimationEvent;
import dtu.agency.events.agent.MoveObstacleEstimationEvent;
import dtu.agency.planners.plans.PrimitivePlan;
import dtu.agency.services.BDIService;
import dtu.agency.services.EventBusService;

import java.util.LinkedList;

public class AgentThread implements Runnable {

    @Override
    public void run() {
        // register all events handled by this class
        EventBusService.register(this);
        System.err.println(Thread.currentThread().getName() + ": Started agent: " + BDIService.getInstance().getAgent().getLabel());
    }

    /**
     * The Agency offered a goal - The agents bid on it
     *
     * @param event
     */
    @Subscribe
    public void goalOfferEventSubscriber(GoalOfferEvent event) {
        // update BDI level
        BDIService.getInstance().updateBDILevelService();

        // setup local variables
        Agent agent = BDIService.getInstance().getAgent();

        // calculate the best bid of solving this goal
        Goal goal = event.getGoal();

        // use agents mind to calculate bid
        Ideas ideas = BDIService.getInstance().thinkOfIdeas(goal);
        boolean successful = BDIService.getInstance().filterIdeas(ideas, goal); // the intention are automatically stored in BDIService

        if (!successful) {
            // System.err.println(Thread.currentThread().getName() + ": Agent " + agent + ": Failed to find a valid box that solves: " + goal);
            // We cannot solve this goal, so we return a ridiculously high estimate
            EventBusService.getEventBus().post(new GoalEstimationEvent(agent, goal, Integer.MAX_VALUE));
        }
        else {
            // We return the sum of all intentions so far
            /*
            int totalSteps = bdi.getAgentIntentions()
                    .stream()
                    .mapToInt(GoalIntention::getApproximateSteps)
                    .sum();
            */
            // We return the approximate steps for this goal only
            int totalSteps = BDIService.getInstance().getIntention(goal).getApproximateSteps();

            System.err.println(Thread.currentThread().getName()
                    + ": Agent " + BDIService.getInstance().getAgent().getLabel()
                    + ": received a goaloffer " + goal.getLabel()
                    + " event and returned approximation: " + Integer.toString(totalSteps) + " steps");

            EventBusService.getEventBus().post(new GoalEstimationEvent(agent, goal, totalSteps));
        }
    }

    /**
     * The Agency assigned someone a goal - The agent solve it
     *
     * @param event
     */
    @Subscribe
    @AllowConcurrentEvents
    public void goalAssignmentEventSubscriber(GoalAssignmentEvent event) {
        if (event.getAgent().getLabel().equals(BDIService.getInstance().getAgent().getLabel())) {

            // update BDI level
            BDIService.getInstance().updateBDILevelService();

            // setup local variables
            Agent agent = BDIService.getInstance().getAgent();

            // We won the bid for this goal!
            Goal goal = event.getGoal();
            System.err.println(Thread.currentThread().getName() + ": Agent " + agent + ": I won the bidding for: " + goal);

            // the intention are automatically stored in BDIService
            Ideas ideas = BDIService.getInstance().thinkOfIdeas(goal);
            boolean successful = BDIService.getInstance().filterIdeas(ideas, goal);

            // use the agent's mind / BDI Service to solve the task
            successful &= BDIService.getInstance().solveGoal(goal); // generate a plan internal in the agents consciousness.

            if (!successful) {
                // TODO: We post a planning error event
                System.err.println(Thread.currentThread().getName() + ": Agent " + agent + ": Failed to find a valid HLPlan for: " + goal);
            }
            else {
                // retrieves the list of primitive actions to execute (blindly)
                PrimitivePlan plan = BDIService.getInstance().getPrimitivePlan();

                // print status and communicate with agency
                System.err.println(Thread.currentThread().getName()
                        + ": Agent " + BDIService.getInstance().getAgent().getLabel()
                        + ": Using Concrete Plan: " + plan.toString());

                // Send the response back
                event.setResponse(plan);
            }
        }
    }

    /**
     * Provide an estimation of how many steps we need for moving the obstacle
     * @param event
     */
    @Subscribe
    @AllowConcurrentEvents
    public void moveObstacleOfferEventSubscriber(MoveObstacleOfferEvent event) {

        // update BDI level
        BDIService.getInstance().updateBDILevelService();

        // setup local variables
        Agent agent = BDIService.getInstance().getAgent();

        LinkedList<Position> path = event.getPath();
        BoardObject obstacle = event.getObstacle();

        // TODO: Think of ideas for moving this obstacle?
        // Ideas ideas = BDIService.getInstance().thinkOfIdeas(goal);
        // boolean successful = BDIService.getInstance().filterIdeas(ideas, goal);

        int totalSteps = 0;

        System.err.println(Thread.currentThread().getName()
                + ": Agent " + BDIService.getInstance().getAgent().getLabel()
                + ": received a task offer for moving " + event.getObstacle().getLabel()
                + " and returned approximation: " + Integer.toString(totalSteps) + " steps");

        EventBusService.getEventBus().post(
                new MoveObstacleEstimationEvent(agent, obstacle, path, false)
        );
    }

    /**
     * Create a plan for moving the obstacle
     * @param event
     */
    @Subscribe
    @AllowConcurrentEvents
    public void moveObstacleAssignmentEventSubscriber(MoveObstacleAssignmentEvent event) {
        if (event.getAgent().getLabel().equals(BDIService.getInstance().getAgent().getLabel())) {
            // We have been assigned the task of moving this obstacle

            // update BDI level
            BDIService.getInstance().updateBDILevelService();

            // setup local variables
            Agent agent = BDIService.getInstance().getAgent();

            // TODO: Think of ideas for moving this obstacle
            //Ideas ideas = BDIService.getInstance().thinkOfIdeas(goal);
            //boolean successful = BDIService.getInstance().filterIdeas(ideas, goal);

            // Create plan for moving object
            BDIService.getInstance().solveMoveObstacle(event.getPath(), event.getObstacle());

            // retrieve the list of primitive actions to execute (blindly)
            PrimitivePlan plan = BDIService.getInstance().getPrimitivePlan();

            event.setResponse(plan);
        }
    }
}