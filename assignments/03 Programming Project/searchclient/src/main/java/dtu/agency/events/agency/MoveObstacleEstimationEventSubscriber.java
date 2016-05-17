package dtu.agency.events.agency;

import dtu.agency.board.Agent;
import dtu.agency.board.BoardObject;
import dtu.agency.events.EstimationEventSubscriber;
import dtu.agency.events.agent.MoveObstacleEstimationEvent;

import java.util.List;

public class MoveObstacleEstimationEventSubscriber extends EstimationEventSubscriber<MoveObstacleEstimationEvent> {

    public MoveObstacleEstimationEventSubscriber(BoardObject obstacle, List<Agent> agents) {
        super(obstacle, agents);
    }
}
