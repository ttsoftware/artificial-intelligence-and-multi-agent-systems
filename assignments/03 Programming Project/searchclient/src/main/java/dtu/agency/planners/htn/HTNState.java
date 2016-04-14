package dtu.agency.planners.htn;

import dtu.agency.actions.ConcreteAction;
import dtu.agency.actions.abstractaction.hlaction.HLAction;
import dtu.agency.actions.abstractaction.hlaction.*;
import dtu.agency.actions.abstractaction.rlaction.RGotoAction;
import dtu.agency.actions.abstractaction.rlaction.RMoveBoxAction;
import dtu.agency.actions.concreteaction.Direction;
import dtu.agency.actions.concreteaction.MoveConcreteAction;
import dtu.agency.actions.concreteaction.PullConcreteAction;
import dtu.agency.actions.concreteaction.PushConcreteAction;
import dtu.agency.board.BoardCell;
import dtu.agency.board.Box;
import dtu.agency.board.Position;
import dtu.agency.planners.plans.MixedPlan;
import dtu.agency.services.BDIService;
import dtu.agency.services.DebugService;
import dtu.agency.services.GlobalLevelService;
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

    public boolean boxIsMovable() {
        return agentPosition.isAdjacentTo(boxPosition);
    }

    public Direction getDirectionToBox() { // returns the direction from agent to box
        return pls.getRelativeDirection(agentPosition, boxPosition, false);
    }

    public boolean isLegal() { // we could introduce different levels of relaxations to be enforced here
        debug("isLegal(): ", 2);
        debug("RelaxMode: " + relaxationMode.toString());
        boolean legal = !agentPosition.equals(boxPosition);
        debug("box and agent position are not identical:" + Boolean.toString(legal) );

        legal &= (!wallConflict());
        if (!legal) { // save the computations
            debug("",-2);
            return legal;
        }
        switch (relaxationMode) { // walls are considered already

            case None:            // consider agents + boxes + walls
                legal &= (!agentOrBoxConflict());
                break;

            case NoAgents:        // Boxes and Walls are considered
                legal &= (!boxConflict());
                break;

            case NoAgentsNoBoxes: // Only Walls are considered
                break;

            default:
                debug("relaxationMode defaulted!");
        }


        debug("", -2);
        return legal;
    }

    /**
     * Detects if this state will conflict with another box OR agent
     * @return
     */
    private boolean agentOrBoxConflict() {
        boolean conflict = false;
        String myBox = pls.getCurrentBox().getLabel();
        String myAgent = BDIService.getInstance().getAgent().getLabel();
        Position a = agentPosition;
        Position b = boxPosition;
        int ar = a.getRow(); int ac = a.getColumn();
        int br = b.getRow(); int bc = b.getColumn();

        if (!pls.isFree(agentPosition)) {
            BoardCell acell = pls.getLevel().getBoardState()[ar][ac];
            BoardCell bcell = pls.getLevel().getBoardState()[br][bc];

            String objectAtAgentPosition = pls.getObjectLabels(agentPosition);
            debug("Agent position " + agentPosition.toString() + " is same as something else labelled: " + objectAtAgentPosition);

            if (acell == BoardCell.AGENT || acell == BoardCell.BOX) { // potential conflict1
                if (!(objectAtAgentPosition.equals(myBox)) || (objectAtAgentPosition.equals(myAgent))) { // not myself !
                    conflict = true; // conflict !
                }
            }

            if (bcell == BoardCell.AGENT || acell == BoardCell.BOX) { // potential conflict1
                if (!(objectAtAgentPosition.equals(myBox)) || (objectAtAgentPosition.equals(myAgent))) { // not myself !
                    conflict = true; // conflict !
                }
            }

            if (acell == BoardCell.AGENT_GOAL) { // potential conflict - compare only to agent
                if (!objectAtAgentPosition.substring(0,1).equals(myAgent)) {
                    conflict = true;
                }
            }

            if (bcell == BoardCell.AGENT_GOAL) { // potential conflict - compare only to agent
                if (!objectAtAgentPosition.substring(0,1).equals(myAgent)) {
                    conflict = true;
                }
            }

            if (acell == BoardCell.BOX_GOAL) { // potential conflict - compare only to box
                // TODO : something better FAILS IF MORE THAN 10 BOXES WITH SAME LETTER EXIST IN LEVEL
                if (!objectAtAgentPosition.substring(0,2).equals(myBox)){
                    conflict = true;
                }
            }

            if (bcell == BoardCell.BOX_GOAL) { // potential conflict - compare only to box
                // TODO : something better FAILS IF MORE THAN 10 BOXES WITH SAME LETTER EXIST IN LEVEL
                if (!objectAtAgentPosition.substring(0,2).equals(myBox)){
                    conflict = true;
                }
            }

            debug("Which is a conflict: " + Boolean.toString(conflict));
        }
        return conflict;
    }

    /**
     * Detects if this state will conflict with another box
     * @return
     */
    private boolean boxConflict() {
        boolean conflict = false;
        String myBox = pls.getCurrentBox().getLabel();

        if (!pls.isFree(agentPosition)){
            String objectAtAgentPosition = pls.getObjectLabels(agentPosition);
            if (!objectAtAgentPosition.equals(myBox)) { // not myself !
                conflict = true; // conflict !
                debug("Agent position "+agentPosition.toString()+" is same as a other box:" + Boolean.toString(conflict) );
                debug("My box: "+myBox+" | pls box: " + objectAtAgentPosition );
            }
        }

        if (boxPosition!=null) {
            if (!pls.isFree(boxPosition)) {
                String objectAtBoxPosition = pls.getObjectLabels(boxPosition);
                if (!objectAtBoxPosition.equals(myBox)) { // not myself !
                    conflict = true; // conflict !
                    debug("Box position "+boxPosition.toString()+" is same as a other box:" + Boolean.toString(conflict) );
                }
            }
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

                case SolveGoal:
                    SolveGoalAction sga = (SolveGoalAction) action;
                    MixedPlan sgRefinement = new MixedPlan();
                    sgRefinement.addAction(new RGotoAction(
                            pls.getPosition(sga.getBox())
                    ));
                    sgRefinement.addAction(new RMoveBoxAction(
                            sga.getBox(),
                            pls.getPosition(sga.getGoal())
                    ));
                    refinements.add(sgRefinement);

                    break;

                case Circumvent:
                    CircumventBoxAction circAction = (CircumventBoxAction) action;
                    MixedPlan circRefinement = new MixedPlan();

                    circRefinement.addAction(new RGotoAction(circAction.getAgentDestination())); // TODO: no relaxation!?
                    refinements.add(circRefinement);
                    break;

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

                    case MoveBoxAction:
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

                    case SolveGoalSuper:
                        SolveGoalSuperAction sgsa = (SolveGoalSuperAction) action;

                        for (Box box : BDIService.getInstance().getBDILevelService().getLevel().getBoxes()) {
                            if (box.getLabel().toLowerCase().equals(sgsa.getGoal().getLabel().toLowerCase())) {
                                MixedPlan sgsaRefinement = new MixedPlan();
                                SolveGoalAction sgAction = new SolveGoalAction(box, sgsa.getGoal());
                                debug(sgAction.toString());
                                sgsaRefinement.addAction( sgAction );
                                refinements.add(sgsaRefinement);
                            }
                        }
                        break;

                case No:
                    break;

                case MoveBoxAndReturn:
                        HMoveBoxAction mbar = (HMoveBoxAction) action;
                        MixedPlan mbarRefinement = new MixedPlan();

                        mbarRefinement.addAction(new RGotoAction( // TODO: PlannerLevelService??
                                pls.getPosition(mbar.getBox())
                        ) );
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
     *
     * @param concreteAction
     * @return a new HTNState instance with @concreteAction applied to it
     */
    public HTNState applyConcreteAction(ConcreteAction concreteAction) {
        debug("applyConcreteActions():", 2);
        Position oldAgentPos = getAgentPosition();
        Position oldBoxPos   = getBoxPosition();
        RelaxationMode mode  = getRelaxationMode();
        Position newAgentPos, newBoxPos;
        HTNState result;

        // keep track of validity of the action
        boolean valid =  true;
        debug(this.toString() );

        switch (concreteAction.getType()) {
            case MOVE: {
                MoveConcreteAction moveAction = (MoveConcreteAction) concreteAction;
                newAgentPos = GlobalLevelService.getInstance().getAdjacentPositionInDirection(oldAgentPos, moveAction.getDirection());

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

                newAgentPos = GlobalLevelService.getInstance().getAdjacentPositionInDirection(oldAgentPos, pushAction.getAgentDirection());
                newBoxPos = GlobalLevelService.getInstance().getAdjacentPositionInDirection(oldBoxPos, pushAction.getBoxDirection());
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

                newAgentPos = GlobalLevelService.getInstance().getAdjacentPositionInDirection(oldAgentPos, pullAction.getAgentDirection());
                newBoxPos = GlobalLevelService.getInstance().getAdjacentPositionInDirection(oldBoxPos, pullAction.getBoxDirection().getInverse());
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
     * TODO: Checks if the purpose of the current high level action is fulfilled
     * @param action
     * @return boolean
     */
    public boolean isPurposeFulfilled(HLAction action) {
        debug("isPurposeFulfilled(" + action.toString() + "):", 2);
        boolean fulfilled = false;

        switch (action.getType()) {
            case SolveGoal:
                SolveGoalAction sga = new SolveGoalAction((SolveGoalAction) action);
                fulfilled = this.getBoxPosition().equals(sga.getGoal().getPosition());
                debug(action.toString() + " -> box is"+ ((fulfilled)?" ":" not ") +"at goal location");
                break;

            case Circumvent:
                CircumventBoxAction cba = new CircumventBoxAction((CircumventBoxAction) action);
                fulfilled  = this.getAgentPosition().equals( action.getAgentDestination() );
                fulfilled &= this.getBoxPosition().equals( GlobalLevelService.getInstance().getPosition(action.getBox()) ); // TODO: PlannerLevelService
                debug(action.toString() + " -> agent&box is"+ ((fulfilled)?" ":" not ") +"at destinations");
                break;

            case RGotoAction:
                fulfilled = this.getAgentPosition().isAdjacentTo(action.getAgentDestination()); // TODO: adjacent is enough?? only if box is null
                debug(action.toString() + " -> agent is"+ ((fulfilled)?" ":" not ") +"adjacent to destination");
                break;

            case MoveBoxAction:
                fulfilled = (this.getBoxPosition().equals(action.getAgentDestination()));
                debug(action.toString() + " -> box is"+ ((fulfilled)?" ":" not ") +"at destination");
                break;

            case SolveGoalSuper:
                SolveGoalSuperAction sgsa = new SolveGoalSuperAction((SolveGoalSuperAction) action);
                fulfilled = (this.getBoxPosition()!=null) ? this.getBoxPosition().equals(sgsa.getGoal().getPosition()): false;
                debug(action.toString() + " -> box is"+ ((fulfilled)?" ":" not ") +"at destinations");
                break;

            case No:
                fulfilled = true;
                break;

            case MoveBoxAndReturn:
                HMoveBoxAction mbarAction = new HMoveBoxAction((HMoveBoxAction) action);
                fulfilled  = this.getAgentPosition().equals( mbarAction.getAgentDestination() );
                fulfilled &= this.getBoxPosition().equals( mbarAction.getBoxDestination() );
                debug(action.toString() + " -> agent&box is"+ ((fulfilled)?" ":" not ") +"at destinations");
                break;

            default:
                debug("", -2);
                throw new UnsupportedOperationException("Invalid HLAction type");
        }

        if (fulfilled) {
            debug("Purpose of HLAction is fulfilled: " + action.toString(), -2);
        } else {
            debug("Purpose of HLAction is not fulfilled: " + action.toString(), -2);
        }
        return fulfilled;
    }


    /**
     * What does this do? As far as i can see it does not return anything meaningful
     * Mads: Well it is meaningfull to check whether states have been visited before in the planning loop
     * and to check if 2 states are equal... this method comes in handy :-)
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        HTNState other = (HTNState) obj;
        if (!agentPosition.equals(other.getAgentPosition()))
            return false;
        if ((boxPosition == null) || (other.getBoxPosition() == null)) {
            return boxPosition == other.getBoxPosition();
        }
        if (!boxPosition.equals(other.getBoxPosition()))
            return false;
        return true;
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
