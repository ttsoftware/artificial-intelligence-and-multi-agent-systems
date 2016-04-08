package dtu.agency.agent;

import com.google.common.eventbus.Subscribe;
import dtu.agency.actions.abstractaction.HLAction;
import dtu.agency.actions.abstractaction.hlaction.*;
import dtu.agency.agent.bdi.Belief;
import dtu.agency.board.Agent;
import dtu.agency.board.Goal;
import dtu.agency.events.agency.GoalAssignmentEvent;
import dtu.agency.events.agency.GoalOfferEvent;
import dtu.agency.events.agent.GoalEstimationEvent;
import dtu.agency.events.agent.PlanOfferEvent;
import dtu.agency.planners.htn.PrimitivePlan;
import dtu.agency.planners.htn.HTNPlanner;
import dtu.agency.services.EventBusService;

import java.util.HashMap;
import java.util.Objects;

public class AgentThread implements Runnable {

    // the agency object which this agency corresponds to
    private final Agent agent;
    private Belief belief;                        // could store information about where the agent thinks the different boxes will be located at different timesteps.
    private HashMap<String, HTNPlanner> desires;  // everything the agent want to achieve (aka desires :-) )
    private HLAction intention;                   // keep track of what the agent intends to do, choosing from the desires

    public AgentThread(Agent agent) {
        this.agent = agent;
        desires = new HashMap<>();
    }

    public Belief getBelief() {
        return belief;
    }

    public void setBelief(Belief belief) {
        this.belief = belief;
    }

    public HLAction getIntention() {
        return intention;
    }

    public void setIntention(HLAction intention) {
        this.intention = intention;
    }

    @Override
    public void run() {
        // register all events handled by this class
        EventBusService.register(this);
        System.err.println("Started agent: " + agent.getLabel());
    }

    /**
     * The Agency offered a goal - we bid on it
     *
     * @param event
     */
    @Subscribe
    public void goalOfferEventSubscriber(GoalOfferEvent event) {
        Goal goal = event.getGoal();

        HTNPlanner htnPlanner = new HTNPlanner(this.agent, new SolveGoalSuperAction(goal));
        int steps = htnPlanner.getBestPlanApproximation();

        desires.put(goal.getLabel(), htnPlanner);

        System.err.println("Agent received a goaloffer " + goal.getLabel() + " event and returned: " + Integer.toString(steps));

        EventBusService.getEventBus().post(new GoalEstimationEvent(agent.getLabel(), steps));
    }

    /**
     * The Agency assigned someone a goal
     *
     * @param event
     */
    @Subscribe
    public void goalAssignmentEventSubscriber(GoalAssignmentEvent event) {
        if (Objects.equals(event.getAgentLabel(), agent.getLabel())) {
            // We won the bid for this goal!

            System.err.println("I won the bid for: " + event.getGoal().getLabel());

            // Find the HTNPlanner for this goal, and do the actual planning
            HTNPlanner htnPlanner = desires.get(event.getGoal().getLabel());
            setIntention(htnPlanner.getBestIntention());
            setBelief(htnPlanner.getBestBelief());

            PrimitivePlan plan = htnPlanner.plan();

            // check that the plan in its entirety is 'sound', that is that all cells in the path is free
            if (!planIsSound(plan, intention)) {
                ; // replan! or extend plan until it is
                  // or send distress communication and ask for help/forgiveness/permission to commit suicide/whatever
            }

            EventBusService.getEventBus().post(new PlanOfferEvent(event.getGoal(), agent, plan)); // execute plan
        }
    }

    public boolean planIsSound(PrimitivePlan plan, HLAction intention) {
        switch (intention.getType()) {
            case SolveGoal:
                SolveGoalAction sga = (SolveGoalAction) intention;
                break;
            case Circumvent:
                CircumventBoxAction cba = (CircumventBoxAction) intention;
                break;
            case GotoAction:
                GotoAction gta = (GotoAction) intention;
                break;
            case MoveBoxAction:
                MoveBoxAction mba = (MoveBoxAction) intention;
                break;
            case MoveBoxAndReturn:
                MoveBoxAndReturnAction mbar = (MoveBoxAndReturnAction) intention;
                break;
        }
        return true;
    }


    public Agent getAgent() {
        return agent;
    }
}