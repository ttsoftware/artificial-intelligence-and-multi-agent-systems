package dtu.agency.agent;

import com.google.common.eventbus.Subscribe;
import dtu.agency.actions.ConcreteAction;
import dtu.agency.actions.concreteaction.ConcreteActionType;
import dtu.agency.agent.bdi.Ideas;
import dtu.agency.agent.bdi.Intention;
import dtu.agency.board.Agent;
import dtu.agency.board.Box;
import dtu.agency.board.Goal;
import dtu.agency.board.Position;
import dtu.agency.conflicts.ResolvedConflict;
import dtu.agency.events.agency.GoalAssignmentEvent;
import dtu.agency.events.agency.GoalOfferEvent;
import dtu.agency.events.agency.MoveObstacleAssignmentEvent;
import dtu.agency.events.agency.MoveObstacleOfferEvent;
import dtu.agency.events.agent.GoalEstimationEvent;
import dtu.agency.events.agent.MoveObstacleEstimationEvent;
import dtu.agency.events.client.ConflictResolutionEvent;
import dtu.agency.planners.plans.PrimitivePlan;
import dtu.agency.services.AgentService;
import dtu.agency.services.BDIService;
import dtu.agency.services.ConflictService;
import dtu.agency.services.EventBusService;

import java.util.Iterator;
import java.util.LinkedList;

public class AgentThread implements Runnable {

    private Agent agent;

    public AgentThread() {
    }

    public AgentThread(Agent agent) {
        this.agent = agent;
    }

    @Override
    public void run() {
        prepareSubscriber();
        // register all events handled by this class
        EventBusService.register(this);
        System.err.println(Thread.currentThread().getName() + ": Started agent: " + BDIService.getInstance().getAgent().getLabel());
        finishSubscriber();
    }

    /**
     * Prepare the BDIService for incoming events
     */
    private void prepareSubscriber() {
        /*try {
            // get the current agent
            agent = AgentService.getInstance().take();
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }*/
        // get the threadLocal instance
        BDIService bdiService = AgentService.getInstance().getBDIServiceInstance(agent);
        BDIService.setInstance(bdiService);
        // update BDI level
        BDIService.getInstance().updateBDILevelService();
    }

    private void finishSubscriber() {
        // add the agent and BDI back
        AgentService.getInstance().addAgent(
                agent,
                BDIService.getInstance()
        );
        // agent = null;
    }

    /**
     * The Agency offered a goal - The agents bid on it
     *
     * @param event
     */
    @Subscribe
    public void goalOfferEventSubscriber(GoalOfferEvent event) {
        prepareSubscriber();

        // calculate the best bid of solving this goal
        Goal goal = event.getGoal();

        // use agents mind to calculate bid
        Ideas ideas = BDIService.getInstance().thinkOfIdeas(goal);
        boolean successful = BDIService.getInstance().findGoalIntention(ideas, goal); // the intention are automatically stored in BDIService

        if (!successful) {
            // System.err.println(Thread.currentThread().getName() + ": Agent " + agent + ": Failed to find a valid box that solves: " + goal);
            // We cannot solve this goal, so we return a ridiculously high estimate

            System.err.println(Thread.currentThread().getName()
                    + ": Agent " + BDIService.getInstance().getAgent().getLabel()
                    + ": received a goaloffer " + goal.getLabel()
                    + " event but is not the right colour");

            EventBusService.getEventBus().post(new GoalEstimationEvent(agent, goal, Integer.MAX_VALUE));
        } else {
            // We return the approximate steps for this goal only
            int totalSteps = BDIService.getInstance().getIntention(goal.getLabel()).getApproximateSteps();

            System.err.println(Thread.currentThread().getName()
                    + ": Agent " + BDIService.getInstance().getAgent().getLabel()
                    + ": received a goaloffer for " + goal.getLabel()
                    + " and returned " + Integer.toString(totalSteps) + " steps");

            EventBusService.getEventBus().post(new GoalEstimationEvent(agent, goal, totalSteps));
        }

        finishSubscriber();
    }

    /**
     * The Agency assigned someone a goal - The agent solve it
     *
     * @param event
     */
    @Subscribe
    public void goalAssignmentEventSubscriber(GoalAssignmentEvent event) {
        prepareSubscriber();

        if (event.getAgent().equals(BDIService.getInstance().getAgent())) {
            // We won the bid for this goal!
            Goal goal = event.getGoal();
            System.err.println(Thread.currentThread().getName() + ": Agent " + agent + ": I won the bidding for: " + goal);

            // the intention are automatically stored in BDIService
            Ideas ideas = BDIService.getInstance().thinkOfIdeas(goal);
            boolean successful = BDIService.getInstance().findGoalIntention(ideas, goal);

            // use the agent's mind / BDI Service to solve the task
            successful &= BDIService.getInstance().solveGoal(goal); // generate a plan internal in the agents consciousness.

            if (!successful) {
                System.err.println(Thread.currentThread().getName() + ": Agent " + agent + ": Failed to find a valid HLPlan for: " + goal);
                event.setResponse(new PrimitivePlan());
            } else {
                // retrieves the list of primitive actions to execute (blindly)
                PrimitivePlan plan = BDIService.getInstance().getPrimitivePlan();

                plan = removeGoBack(plan);

                // print status and communicate with agency
                System.err.println(Thread.currentThread().getName()
                        + ": Agent " + BDIService.getInstance().getAgent().getLabel()
                        + ": Using Concrete Plan: " + plan.toString());


                // Send the response back
                event.setResponse(plan);
            }
        }

        finishSubscriber();
    }

    /**
     * Provide an estimation of how many steps we need for moving the obstacle
     *
     * @param event
     */
    @Subscribe
    public void moveObstacleOfferEventSubscriber(MoveObstacleOfferEvent event) {
        prepareSubscriber();

        // setup local variables
        LinkedList<Position> path = event.getPath();
        Box obstacle = (Box) event.getObstacle();

        boolean successful = BDIService.getInstance().findMoveBoxFromPathIntention(path, obstacle);

        if (successful) {
            Intention intention = BDIService.getInstance().getIntention(obstacle.getLabel());
            int totalSteps = intention.getApproximateSteps();

            System.err.println(Thread.currentThread().getName()
                    + ": Agent " + BDIService.getInstance().getAgent().getLabel()
                    + ": received a task offer for moving " + event.getObstacle().getLabel()
                    + " and returned approximation: " + Integer.toString(totalSteps) + " steps");

            boolean hasObstacles = intention.getObstaclePositions().size() > 0;

            EventBusService.getEventBus().post(
                    new MoveObstacleEstimationEvent(
                            agent,
                            obstacle,
                            intention.getAgentBoxPseudoPath(),
                            !hasObstacles
                    )
            );
        } else {
            // TODO: Separate worlds?
            throw new RuntimeException("Something is wrong and you should feel wrong.");
        }

        finishSubscriber();
    }

    /**
     * Create a plan for moving the obstacle
     *
     * @param event
     */
    @Subscribe
    public void moveObstacleAssignmentEventSubscriber(MoveObstacleAssignmentEvent event) {
        prepareSubscriber();

        if (event.getAgent().equals(BDIService.getInstance().getAgent())) {
            // We have been assigned the task of moving this obstacle

            LinkedList<Position> path = event.getPath();
            Box obstacle = (Box) event.getObstacle();

            System.err.println(Thread.currentThread().getName() + ": Agent " + agent + ": I won the bid for moving box: " + obstacle);

            boolean successful = BDIService.getInstance().findMoveBoxFromPathIntention(path, obstacle);

            if (successful) {
                // Create plan for moving object
                successful &= BDIService.getInstance().solveMoveBox(obstacle);

                if (successful) {
                    // retrieve the list of primitive actions to execute (blindly)
                    PrimitivePlan plan = BDIService.getInstance().getPrimitivePlan();

                    plan = removeGoBack(plan);

                    event.setResponse(plan);
                } else {
                    // we probably need help helping
                }
            } else {
                throw new RuntimeException("We could not make an intention after estimating, which requires an intention?");
            }
        }

        finishSubscriber();
    }

    /**
     * Removes the last part of the plan, where the agent tries to move back into the box' position
     * @param plan
     * @return
     */
    private PrimitivePlan removeGoBack(PrimitivePlan plan) {

        PrimitivePlan newPlan = new PrimitivePlan(plan);

        Iterator<ConcreteAction> actionIterator = plan.getActions().descendingIterator();
        while (actionIterator.hasNext()) {
            ConcreteAction action = actionIterator.next();
            if (action.getType().equals(ConcreteActionType.MOVE)) {
                newPlan.removeLast();
            }
            else {
                // break as soon as we see an action which is not move
                break;
            }
        }

        return newPlan;
    }

    @Subscribe
    public void conflictResolutionEventSubscriber(ConflictResolutionEvent event) {
        prepareSubscriber();

        if (event.getConflict().getInitiator().equals(BDIService.getInstance().getAgent())) {
            ConflictService conflictService = new ConflictService();

            ResolvedConflict resolvedConflict = conflictService.resolveConflict(event.getConflict());

            event.setResponse(resolvedConflict);
        }

        finishSubscriber();
    }
}