package dtu.agency.planners.htn;

import dtu.agency.actions.AbstractAction;
import dtu.agency.actions.ConcreteAction;
import dtu.agency.actions.abstractaction.AbstractActionType;
import dtu.agency.actions.abstractaction.SolveGoalAction;
import dtu.agency.actions.abstractaction.hlaction.*;
import dtu.agency.actions.abstractaction.rlaction.RGotoAction;
import dtu.agency.actions.abstractaction.rlaction.RMoveBoxAction;
import dtu.agency.actions.concreteaction.Direction;
import dtu.agency.actions.concreteaction.MoveConcreteAction;
import dtu.agency.actions.concreteaction.PullConcreteAction;
import dtu.agency.actions.concreteaction.PushConcreteAction;
import dtu.agency.board.BoardCell;
import dtu.agency.board.Position;
import dtu.agency.planners.plans.MixedPlan;
import dtu.agency.services.DebugService;
import dtu.agency.services.PlanningLevelService;

import java.util.ArrayList;

/**
 * Data structure to keep track of state of the two chosen BoardObjects targeted
 * in a specific HTNPlanner, an Agent and a Box
 *  - Along with all the methods needed to manipulate the state
 *    when encountering Concrete- as well as HL-Actions
 */
public class HTNState {
    private static void debug(String msg, int indentationChange) { DebugService.print(msg, indentationChange); }
    private static void debug(String msg){ debug(msg, 0); }

    private final Position agentPosition;
    private final Position boxPosition;
    private final PlanningLevelService pls;
    private RelaxationMode relaxationMode;

    /**
     * copy constructor
     * @param other The HTNState to be copied
     */
    public HTNState(HTNState other) {
        this.agentPosition = new Position(other.getAgentPosition());
        this.boxPosition = new Position(other.getBoxPosition());
        this.pls = other.getPlanningLevelService();
        this.relaxationMode = other.getRelaxationMode();
    }

    public HTNState(Position agentPosition, Position boxPosition, PlanningLevelService pls, RelaxationMode mode) throws AssertionError {
        this.agentPosition = agentPosition;
        this.boxPosition = boxPosition;
        this.pls = pls;
        this.relaxationMode = mode;
        if (agentPosition == null) throw new AssertionError("MUST have an agent location");
        if (pls == null) throw new AssertionError("MUST have a PlanningLevelService available");
    }

    public PlanningLevelService getPlanningLevelService() {
        return pls;
    }

    public Position getAgentPosition() {
        return agentPosition;
    }

    public Position getBoxPosition() {
        return boxPosition;
    }

    public RelaxationMode getRelaxationMode() {
        return relaxationMode;
    }

    public void setRelaxationMode(RelaxationMode relaxationMode) {
        this.relaxationMode = relaxationMode;
    }

    private boolean boxIsMovable() {
        return agentPosition.isAdjacentTo(boxPosition);
    }

    public Direction getDirectionToBox() { // returns the direction from agent to box
        return pls.getRelativeDirection(agentPosition, boxPosition, false);
    }

    /**
     * A check performed to see if the current state is legal
     * or if it e.g. has an agent and a wall in same position (illegal)
     * @return
     */
    private boolean isLegal() {
        debug("isLegal(): ", 2);
        debug("RelaxMode: " + relaxationMode.toString());
        boolean legal = true;

        switch (relaxationMode) { // walls are considered already

            case None:            // consider agents + boxes + walls
                legal &= (!wallConflict());
                legal &= (!boxConflict());
                legal &= (!agentConflict());
                break;

            case NoAgents:        // Boxes and Walls are considered
                legal &= (!wallConflict());
                legal &= (!boxConflict());
                break;

            case NoAgentsNoBoxes: // Only Walls are considered
                legal &= (!wallConflict());
                break;

            default:
                break;
        }

        debug("", -2);
        return legal;
    }


    /**
     * Detects if this state will conflict with another agent
     * @return
     */
    private boolean agentConflict() {
        boolean conflict = false;
        debug("agentConflict",2);
        debug("AgentPosition: "+agentPosition);
        conflict |= agentConflict(agentPosition);
        debug("BoxPosition: "+boxPosition);
        conflict |= agentConflict(boxPosition);
        debug("",-2);
        return conflict;
    }

    private boolean agentConflict(Position pos) {
        boolean conflict = false;
        if (!pls.isFree(pos)) {
            BoardCell cell = pls.getLevel().getBoardState()[pos.getRow()][pos.getColumn()];
            if (DebugService.inDebugMode()) {
                debug("status: " + cell);
                String objectAtPosition = pls.getObjectLabels(pos);
                debug("agentConflict: @" + pos + " something else exist labelled: " + objectAtPosition);
            }
            if (cell == BoardCell.AGENT || cell == BoardCell.AGENT_GOAL) { conflict = true; }
        }
        return conflict;
    }

    /**
     * Detects if this state will conflict with another box
     * @return
     */
    private boolean boxConflict() {
        boolean conflict = false;
        debug("boxConflict",2);
        debug("AgentPosition: "+agentPosition);
        conflict |= boxConflict(agentPosition);
        debug("BoxPosition: "+boxPosition);
        conflict |= boxConflict(boxPosition);
        debug("",-2);
        return conflict;
    }

    private boolean boxConflict(Position pos){
        boolean conflict = false;
        if (!pls.isFree(pos) ) {
            BoardCell cell = pls.getLevel().getBoardState()[pos.getRow()][pos.getColumn()];
            if (DebugService.inDebugMode()) {
                debug("status: " + cell);
                String objectAtPosition = pls.getObjectLabels(pos);
                debug("boxConflict: @" + pos + " something else exist labelled: " + objectAtPosition);
            }
            if (cell == BoardCell.BOX || cell == BoardCell.BOX_GOAL ) { conflict = true; }
        }
        return conflict;
    }

    /**
     * detects if this state will conflict with a wall
     * @return
     */
    private boolean wallConflict() {
        boolean conflict = pls.isWall(agentPosition);
        debug("Agent position "+agentPosition.toString()+" is same as a wall:" + Boolean.toString(conflict) );
        if (conflict) return conflict;

        if (boxPosition != null) {
            conflict |= pls.isWall(boxPosition);
            debug("Box position "+boxPosition.toString()+" is same as a wall:" + Boolean.toString(conflict) );
        }
        return conflict;
    }

    /**
     * Any High Level ConcreteAction can be refined, as per the Hierarchical Task Network (HTN) approach
     */
    public ArrayList<MixedPlan> getRefinements(HLAction action){
        debug("getRefinements(): " + action.toString(), 2);
        HTNState priorState = this;
        // check if the prior state fulfills this HLActions agentDestination, and if so return empty plan of refinements

        ArrayList<MixedPlan> refinements = new ArrayList<>();

        if (this.isPurposeFulfilled(action)) {
            refinements = action.doneRefinement();

        } else {

            switch (action.getType()) {

                case RGotoAction:
                    RGotoAction gta = (RGotoAction) action;

                    for (Direction dir : Direction.values()) {
                        MixedPlan gotoRefinement = new MixedPlan();
                        ConcreteAction move = new MoveConcreteAction(dir);
                        HTNState result = priorState.applyConcreteAction(move);
                        if (result == null) continue; // illegal move, discard it

                        debug(move.toString() + " -> " + result.toString());
                        gotoRefinement.addAction(move);
                        gotoRefinement.addAction(gta); // append this action again recursively
                        refinements.add(gotoRefinement);
                    }
                    break;

                case RMoveBoxAction:
                    RMoveBoxAction mba = (RMoveBoxAction) action;
                    if (!this.boxIsMovable()) {
                        debug("Box not movable");
                    } else {

                        Direction dirToBox = priorState.getDirectionToBox();
                        debug("direction to box: " + dirToBox.toString());

                        // PUSH REFINEMENTS
                        for (Direction dir : Direction.values()) {
                            MixedPlan pushRefinement = new MixedPlan();
                            ConcreteAction push = new PushConcreteAction(mba.getBox(), dirToBox, dir);
                            HTNState result = priorState.applyConcreteAction(push);
                            if (result == null) continue; // then the action was illegal !
                            debug(push.toString() + " -> " + result.toString());
                            pushRefinement.addAction(push);
                            pushRefinement.addAction(mba);
                            refinements.add(pushRefinement);
                        }

                        // PULL REFINEMENTS
                        for (Direction dir : Direction.values()) {
                            MixedPlan pullRefinement = new MixedPlan();
                            ConcreteAction pull = new PullConcreteAction(mba.getBox(), dir, dirToBox);
                            HTNState result = priorState.applyConcreteAction(pull);
                            if (result == null) continue; // then the action was illegal !
                            debug(pull.toString() + " -> " + result.toString());
                            pullRefinement.addAction(pull);
                            pullRefinement.addAction(mba);
                            refinements.add(pullRefinement);
                        }
                    }
                    break;

                case No:
                    break;

                case HMoveBoxAndReturn:
                    HMoveBoxAction mbar = (HMoveBoxAction) action;
                    debug("mbar refinement mbar.getBox():"+mbar.getBox());
                    debug("mbar refinement mbar.getAgentDestination():"+mbar.getAgentDestination());
                    debug("mbar refinement pls.getCurrentBox():"+pls.getTrackingBox());
                    debug("mbar refinement pls.getCurrentBoxPosition():"+pls.getPosition(pls.getTrackingBox()));
                    MixedPlan mbarRefinement = new MixedPlan();

                    mbarRefinement.addAction(new RGotoAction( pls.getTrackingBox(), pls.getPosition(pls.getTrackingBox()) ) );
                    mbarRefinement.addAction(new RMoveBoxAction( mbar.getBox(), mbar.getBoxDestination() ) );
                    mbarRefinement.addAction(new RGotoAction( mbar.getAgentDestination() ) );
                    refinements.add(mbarRefinement);
                    break;
                }
            }
        debug("refinements: " + refinements.toString(), -2);
        return refinements;
    }

    /**
     * Refactored this, such that the action does not apply a state, but a state applies an action.
     * The given instance is the "oldState", and the returned state is the "newState"
     * @param concreteAction The concrete action to be applied (move / push / pull)
     * @return a new HTNState instance with @concreteAction applied to it
     */
    public HTNState applyConcreteAction(ConcreteAction concreteAction) {
        debug("applyConcreteActions():", 2);
        Position oldAgentPos = getAgentPosition();
        Position oldBoxPos   = getBoxPosition();
        RelaxationMode mode  = getRelaxationMode();
        Position newAgentPos, newBoxPos;
        HTNState result;

        boolean valid =  true;
        debug(this.toString() );

        switch (concreteAction.getType()) {
            case MOVE: {
                MoveConcreteAction moveAction = (MoveConcreteAction) concreteAction;
                newAgentPos = pls.getAdjacentPositionInDirection(oldAgentPos, moveAction.getDirection());

                result = new HTNState(newAgentPos, oldBoxPos, pls, mode);
                debug(" + " + moveAction.toString() + " -> " + result.toString() );

                if (result.isLegal()) {
                    debug("", -2);
                    return result;
                } else {
                    debug("HTNState.applyMove: Invalid result " + result.toString(), -2);
                    return null;
                }
            }
            case PUSH: {

                PushConcreteAction pushAction = (PushConcreteAction) concreteAction;

                newAgentPos = pls.getAdjacentPositionInDirection(oldAgentPos, pushAction.getAgentDirection());
                newBoxPos = pls.getAdjacentPositionInDirection(oldBoxPos, pushAction.getBoxDirection());
                result = new HTNState(newAgentPos, newBoxPos, pls, mode);

                debug(" + " + pushAction.toString() + " -> " + result.toString());
                // check preconditions !!! THIS IS PUSH

                valid &= !pushAction.getAgentDirection().getInverse().equals(pushAction.getBoxDirection()); // NOT opposite directions (would be pull!)
                debug(" validation push not opposite directions:" + Boolean.toString(valid));

                // post conditions
                valid &= newAgentPos.equals(oldBoxPos);        // Push: agent follows box
                debug(" validation push agent follows box:" + Boolean.toString(valid));

                valid &= !newBoxPos.equals(oldAgentPos);       // Push: agent is not at wrong location
                debug(" validation push box is not at old agent location:" + Boolean.toString(valid));

                break;
            }
            case PULL: {

                PullConcreteAction pullAction = (PullConcreteAction) concreteAction;

                newAgentPos = pls.getAdjacentPositionInDirection(oldAgentPos, pullAction.getAgentDirection());
                newBoxPos = pls.getAdjacentPositionInDirection(oldBoxPos, pullAction.getBoxDirection().getInverse());
                result = new HTNState(newAgentPos, newBoxPos, pls, mode);

                debug(" + " + pullAction.toString() + " -> " + result.toString());

                // check preconditions !!! THIS IS PULL
                valid &= !pullAction.getAgentDirection().equals(pullAction.getBoxDirection()); // NOT same directions (would be push)
                debug(" validation pull not same directions:" + Boolean.toString(valid));

                // post conditions
                valid &= newBoxPos.equals(oldAgentPos);        // Pull: box follows agent
                debug(" validation pull box follows agent :" + Boolean.toString(valid));

                valid &= (!newAgentPos.equals(oldBoxPos));       // Pull: agent is not at wrong location
                debug(" validation pull agent is not at old box location:" + Boolean.toString(valid));

                break;
            }
            case NONE: {
                debug("", -2);
                return this;
            }
            default:
                debug("", -2);
                throw new UnsupportedOperationException("Invalid action type");
        }
        valid &= oldAgentPos.isAdjacentTo(oldBoxPos);   // box and agent is neighbor in prior state // is this unnecessary?
        debug(" validation box and agent is neighbor in prior state:" + Boolean.toString(valid));

        valid &= newAgentPos.isAdjacentTo(newBoxPos);   // box and agent are still neighbours in posterior state
        debug(" validation box and agent are still neighbours in posterior state:" + Boolean.toString(valid));

        valid &= result.isLegal();
        debug(" validation the new state is legal:" + Boolean.toString(valid));

        debug("", -2);
        return (valid) ? result : null;
    }

    /**
     * Checks if the purpose of the current high level action is fulfilled
     * @param abstractAction To be checked for fulfilled purpose
     * @return boolean True if purpose is fulfilled
     */
    public boolean isPurposeFulfilled(AbstractAction abstractAction) {
        debug("isPurposeFulfilled(" + abstractAction.toString() + "):", 2);
        boolean fulfilled = false;

        if (abstractAction instanceof HLAction) {
            HLAction action = (HLAction) abstractAction;
            switch (abstractAction.getType()) {

                case RGotoAction:
                    if (action.getBox()==null) {
                        fulfilled = this.getAgentPosition().equals(action.getAgentDestination());
                    } else {
                        fulfilled = this.getAgentPosition().isAdjacentTo(action.getAgentDestination());
                    }
                    break;

                case RMoveBoxAction:
                    fulfilled = (this.getBoxPosition().equals(action.getBoxDestination()));
                    break;

                case No:
                    fulfilled = true;
                    break;

                case HMoveBoxAndReturn:
                    fulfilled = this.getBoxPosition().equals(action.getBoxDestination());
                    fulfilled &= this.getAgentPosition().equals(action.getAgentDestination());
                    break;

                default:
                    throw new UnsupportedOperationException("Invalid HLAction type");
            }
        } else { // action is not instanceof HLAction
            if (abstractAction.getType() == AbstractActionType.SolveGoal) {
                SolveGoalAction sga = new SolveGoalAction((SolveGoalAction) abstractAction);
                fulfilled = this.getBoxPosition().equals(sga.getGoal().getPosition());
            } else {
                throw new UnsupportedOperationException("Invalid AbstractAction type");
            }
        }
        debug("Purpose of " + abstractAction +" is " + ((fulfilled) ? "" : "not ") + "fulfilled: ", -2);
        return fulfilled;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null)
            return false;
        if (getClass() != other.getClass())
            return false;
        HTNState state = (HTNState) other;
        if (!agentPosition.equals(state.getAgentPosition()))
            return false;
        if ((boxPosition == null) || (state.getBoxPosition() == null)) {
            return boxPosition == state.getBoxPosition();
        }
        return boxPosition.equals(state.getBoxPosition());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + agentPosition.hashCode();
        result = prime * result + ((boxPosition!=null) ? boxPosition.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        String s;
        s  = "State:[Ag:" + agentPosition.toString();
        s += ",Bx:" + ((boxPosition!=null) ? boxPosition.toString() : "null") + "]";
        return s;
    }

}
