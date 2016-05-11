package dtu.agency.conflicts;

import dtu.agency.board.Agent;
import dtu.agency.board.Position;
import dtu.agency.planners.plans.ConcretePlan;
import dtu.agency.planners.plans.PrimitivePlan;
import dtu.agency.services.GlobalLevelService;
import jdk.nashorn.internal.objects.Global;

public class Conflict {

    private Agent conceder;
    private ConcretePlan concederPlan;
    private Position concederPosition;

    private Agent initiator;
    private ConcretePlan initiatorPlan;
    private Position initiatorPosition;

    public Conflict(Agent agentOne, ConcretePlan agentOnePlan, Agent agentTwo, ConcretePlan agentTwoPlan) {

        if (agentOnePlan.getActions().size() > agentTwoPlan.getActions().size()) {
            conceder = agentOne;
            concederPlan = agentOnePlan;
            concederPosition = GlobalLevelService.getInstance().getPosition(agentOne);
            initiator = agentTwo;
            initiatorPlan = agentTwoPlan;
            initiatorPosition = GlobalLevelService.getInstance().getPosition(agentTwo);
        } else {
            conceder = agentTwo;
            concederPlan = agentTwoPlan;
            concederPosition = GlobalLevelService.getInstance().getPosition(agentTwo);
            initiator = agentOne;
            initiatorPlan = agentOnePlan;
            initiatorPosition = GlobalLevelService.getInstance().getPosition(agentOne);
        }
    }

    public Conflict(Integer agentOne, ConcretePlan agentOnePlan, Integer agentTwo, ConcretePlan agentTwoPlan) {

        if (agentOnePlan == null || agentOnePlan.getActions().isEmpty()) {
            conceder = GlobalLevelService.getInstance().getAgent(agentTwo);
            concederPlan = agentTwoPlan;
            concederPosition = GlobalLevelService.getInstance().getPosition(conceder);
            initiator = GlobalLevelService.getInstance().getAgent(agentOne);
            initiatorPlan = new PrimitivePlan();
            initiatorPosition = GlobalLevelService.getInstance().getPosition(initiator);

        } else if (agentTwoPlan == null || agentTwoPlan.getActions().isEmpty()) {
            conceder = GlobalLevelService.getInstance().getAgent(agentOne);
            concederPlan = agentOnePlan;
            concederPosition = GlobalLevelService.getInstance().getPosition(conceder);
            initiator = GlobalLevelService.getInstance().getAgent(agentTwo);
            initiatorPlan = new PrimitivePlan();
            initiatorPosition = GlobalLevelService.getInstance().getPosition(initiator);

        } else if (agentOnePlan.getActions().size() > agentTwoPlan.getActions().size()) {
            conceder = GlobalLevelService.getInstance().getAgent(agentOne);
            concederPlan = agentOnePlan;
            concederPosition = GlobalLevelService.getInstance().getPosition(conceder);
            initiator = GlobalLevelService.getInstance().getAgent(agentTwo);
            initiatorPlan = agentTwoPlan;
            initiatorPosition = GlobalLevelService.getInstance().getPosition(initiator);

        } else {
            conceder = GlobalLevelService.getInstance().getAgent(agentTwo);
            concederPlan = agentTwoPlan;
            concederPosition = GlobalLevelService.getInstance().getPosition(conceder);
            initiator = GlobalLevelService.getInstance().getAgent(agentOne);
            initiatorPlan = agentOnePlan;
            initiatorPosition = GlobalLevelService.getInstance().getPosition(initiator);
        }
    }

    public void swap() {
        Agent initiator = getInitiator();
        ConcretePlan initiatorPlan = getInitiatorPlan();
        Position initiatorPosition = getInitiatorPosition();

        setInitiator(getConceder());
        setInitiatorPlan(getConcederPlan());
        setInitiatorPosition(getConcederPosition());

        setConceder(initiator);
        setConcederPlan(initiatorPlan);
        setConcederPosition(initiatorPosition);
    }

    public Agent getConceder() {
        return conceder;
    }

    public ConcretePlan getConcederPlan() {
        return concederPlan;
    }

    public Agent getInitiator() {
        return initiator;
    }

    public ConcretePlan getInitiatorPlan() {
        return initiatorPlan;
    }

    public Position getConcederPosition() {
        return concederPosition;
    }

    public Position getInitiatorPosition() {
        return initiatorPosition;
    }

    public void setConceder(Agent conceder) {
        this.conceder = conceder;
    }

    public void setConcederPlan(ConcretePlan concederPlan) {
        this.concederPlan = concederPlan;
    }

    public void setInitiator(Agent initiator) {
        this.initiator = initiator;
    }

    public void setInitiatorPlan(ConcretePlan initiatorPlan) {
        this.initiatorPlan = initiatorPlan;
    }

    public void setConcederPosition(Position concederPosition) {
        this.concederPosition = concederPosition;
    }

    public void setInitiatorPosition(Position initiatorPosition) {
        this.initiatorPosition = initiatorPosition;
    }
}
