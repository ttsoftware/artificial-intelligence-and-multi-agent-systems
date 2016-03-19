package dtu.agency.events.agent;

import com.google.common.eventbus.Subscribe;
import dtu.agency.board.Goal;
import dtu.agency.events.EventSubscriber;
import dtu.agency.events.agency.GoalEstimationEvent;
import dtu.agency.planners.HTNPlanner;
import dtu.agency.services.EventBusService;

import java.util.Random;

public class GoalOfferEventSubscriber implements EventSubscriber<GoalOfferEvent> {

    private String agentLabel;

    public GoalOfferEventSubscriber(String agentLabel) {
        this.agentLabel = agentLabel;
    }

    @Subscribe
    public void changeSubscriber(GoalOfferEvent event) {

        Goal goal = event.getGoal();

        // HTN agency
        HTNPlanner htnPlanner = new HTNPlanner(goal);

        Random random = new Random();
        int randomSteps = random.nextInt();

        System.out.println("Agent recieved a goaloffer event and returned: " + Integer.toString(randomSteps));

        EventBusService.getEventBus().post(new GoalEstimationEvent(agentLabel, randomSteps));
    }
}
