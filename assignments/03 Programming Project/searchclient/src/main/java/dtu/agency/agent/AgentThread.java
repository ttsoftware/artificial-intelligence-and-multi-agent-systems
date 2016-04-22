package dtu.agency.agent;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import dtu.agency.agent.bdi.Ideas;
import dtu.agency.board.Agent;
import dtu.agency.board.Goal;
import dtu.agency.events.agency.GoalAssignmentEvent;
import dtu.agency.events.agency.GoalOfferEvent;
import dtu.agency.events.agent.GoalEstimationEvent;
import dtu.agency.planners.plans.PrimitivePlan;
import dtu.agency.services.BDIService;
import dtu.agency.services.EventBusService;

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
        // setup local variables
        BDIService mind = BDIService.getInstance();
        Agent agent = mind.getAgent();

        // calculate the best bid of solving this goal
        Goal goal = event.getGoal();

        // use agents mind to calculate bid
        Ideas ideas = mind.thinkOfIdeas(goal);
        boolean successful = mind.filter(ideas, goal); // the intention are automatically stored in BDIService

        if (!successful) {
            // TODO: failed what to do... - respond with failure??
            System.err.println(Thread.currentThread().getName() + ": Agent " + agent + ": Failed to find a valid box that solves: " + goal );
        }

        int remainingSteps = mind.remainingConcreteActions();
        int approximation  = mind.getIntention(goal).getApproximateSteps();
        int totalSteps = remainingSteps + approximation;

        // print status and communicate with agency
        System.err.println(Thread.currentThread().getName()
                + ": Agent " + BDIService.getInstance().getAgent().getLabel()
                + ": received a goaloffer " + goal.getLabel()
                + " event and returned approximation: " + Integer.toString(totalSteps) + " steps");

        EventBusService.getEventBus().post(new GoalEstimationEvent(
                        agent,
                        goal,
                        totalSteps
                )
        );
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
            // setup local variables
            BDIService mind = BDIService.getInstance();
            Agent agent = mind.getAgent();

            // We won the bid for this goal!
            Goal goal = event.getGoal();
            System.err.println(Thread.currentThread().getName() + ": Agent " + agent + ": I won the bidding for: " + goal );

            // use the agent's mind / BDI Service to solve the task
            boolean successful = mind.solveGoal(goal); // generate a plan internal in the agents consciousness.

            if (!successful) {
                // TODO: failed what to do... - respond with failure??
                System.err.println(Thread.currentThread().getName() + ": Agent " + agent + ": Failed to find a valid HLPlan for: " + goal );
            }

            // retrieves the list of primitive actions to execute (blindly)
            PrimitivePlan plan = mind.getStepsToBeExecuted();

            // print status and communicate with agency
            System.err.println(Thread.currentThread().getName()
                    + ": Agent " + BDIService.getInstance().getAgent().getLabel()
                    + ": Using Concrete Plan: " + plan.toString());

            // Send the response back
            event.setResponse(plan);
        }
    }

}