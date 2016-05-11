package dtu.agency.planners.htn;

import dtu.agency.actions.AbstractAction;
import dtu.agency.actions.ConcreteAction;
import dtu.agency.actions.abstractaction.AbstractActionType;
import dtu.agency.actions.abstractaction.SolveGoalAction;
import dtu.agency.actions.abstractaction.hlaction.HLAction;
import dtu.agency.actions.abstractaction.hlaction.HMoveBoxAction;
import dtu.agency.actions.abstractaction.rlaction.RGotoAction;
import dtu.agency.actions.abstractaction.rlaction.RMoveBoxAction;
import dtu.agency.actions.concreteaction.Direction;
import dtu.agency.actions.concreteaction.MoveConcreteAction;
import dtu.agency.actions.concreteaction.PullConcreteAction;
import dtu.agency.actions.concreteaction.PushConcreteAction;
import dtu.agency.board.BoardCell;
import dtu.agency.board.BoxAndGoal;
import dtu.agency.board.Position;
import dtu.agency.planners.plans.MixedPlan;
import dtu.agency.services.PlanningLevelService;

import java.util.ArrayList;

/**
 * Data structure to keep track of state of the two chosen BoardObjects targeted
 * in a specific HTNPlanner, an Agent and a Box
 * - Along with all the methods needed to manipulate the state
 * when encountering Concrete- as well as HL-Actions
 */
public class HTNState {

    private final Position agentPosition;
    private final Position boxPosition;
    private final PlanningLevelService pls;
    private RelaxationMode relaxationMode;

    /**
     * copy constructor
     *
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
     *
     * @return
     */
    private boolean isLegal() {
        boolean legal = true;

        switch (relaxationMode) { // walls are considered already

            case None:            // consider agents + boxes + walls
                legal &= (!wallConflict());
                legal &= (!boxConflict());
                legal &= (!agentConflict());
                legal &= (!solvedGoalConflict());
                break;

            case NoAgents:        // Boxes and Walls are considered
                legal &= (!wallConflict());
                legal &= (!boxConflict());
                legal &= (!solvedGoalConflict());
                break;

            case NoAgentsNoBoxes: // Only Walls are considered
                legal &= (!wallConflict());
                legal &= (!solvedGoalConflict());
                break;

            default:
                break;
        }

        return legal;
    }

    /**
     * Detects if this state will conflict with a solved goal
     *
     * @return
     */
    private boolean solvedGoalConflict() {
        BoardCell boxCell = pls.getCell(boxPosition);
        BoardCell agentCell = pls.getCell(agentPosition);

        if (boxCell == BoardCell.BOX_GOAL
                && ((BoxAndGoal) pls.getObject(boxPosition)).isSolved()) {
            return true;
        }
        if (agentCell == BoardCell.BOX_GOAL
                && ((BoxAndGoal) pls.getObject(agentPosition)).isSolved()) {
            return true;
        }
        return false;
    }

    /**
     * Detects if this state will conflict with another agent
     *
     * @return
     */
    private boolean agentConflict() {
        boolean conflict = false;
        conflict |= agentConflict(agentPosition);
        conflict |= agentConflict(boxPosition);
        return conflict;
    }

    private boolean agentConflict(Position pos) {
        boolean conflict = false;
        if (!pls.isFree(pos)) {
            BoardCell cell = pls.getLevel().getBoardState()[pos.getRow()][pos.getColumn()];
            if (cell == BoardCell.AGENT || cell == BoardCell.AGENT_GOAL) {
                conflict = true;
            }
        }
        return conflict;
    }

    /**
     * Detects if this state will conflict with another box
     *
     * @return
     */
    private boolean boxConflict() {
        boolean conflict = false;
        conflict |= boxConflict(agentPosition);
        conflict |= boxConflict(boxPosition);
        return conflict;
    }

    private boolean boxConflict(Position pos) {
        boolean conflict = false;
        if (!pls.isFree(pos)) {
            BoardCell cell = pls.getLevel().getBoardState()[pos.getRow()][pos.getColumn()];
            if (cell == BoardCell.BOX || cell == BoardCell.BOX_GOAL) {
                conflict = true;
            }
        }
        return conflict;
    }

    /**
     * detects if this state will conflict with a wall
     *
     * @return
     */
    private boolean wallConflict() {
        boolean conflict = pls.isWall(agentPosition);
        if (conflict) return conflict;

        if (boxPosition != null) {
            conflict |= pls.isWall(boxPosition);
        }
        return conflict;
    }

    /**
     * Any High Level ConcreteAction can be refined, as per the Hierarchical Task Network (HTN) approach
     */
    public ArrayList<MixedPlan> getRefinements(HLAction action) {
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

                        gotoRefinement.addAction(move);
                        gotoRefinement.addAction(gta); // append this action again recursively
                        refinements.add(gotoRefinement);
                    }
                    break;

                case RMoveBoxAction:
                    RMoveBoxAction mba = (RMoveBoxAction) action;
                    if (!this.boxIsMovable()) {
                        // "Box not movable"
                    } else {
                        Direction dirToBox = priorState.getDirectionToBox();

                        // PUSH REFINEMENTS
                        for (Direction dir : Direction.values()) {
                            MixedPlan pushRefinement = new MixedPlan();
                            ConcreteAction push = new PushConcreteAction(mba.getBox(), dirToBox, dir);
                            HTNState result = priorState.applyConcreteAction(push);
                            if (result == null) continue; // then the action was illegal !
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
                    MixedPlan mbarRefinement = new MixedPlan();

                    mbarRefinement.addAction(new RGotoAction(pls.getTrackingBox(), pls.getPosition(pls.getTrackingBox())));
                    mbarRefinement.addAction(new RMoveBoxAction(mbar.getBox(), mbar.getBoxDestination()));
                    mbarRefinement.addAction(new RGotoAction(mbar.getAgentDestination()));
                    refinements.add(mbarRefinement);
                    break;
            }
        }
        return refinements;
    }

    /**
     * Refactored this, such that the action does not apply a state, but a state applies an action.
     * The given instance is the "oldState", and the returned state is the "newState"
     *
     * @param concreteAction The concrete action to be applied (move / push / pull)
     * @return a new HTNState instance with @concreteAction applied to it
     */
    public HTNState applyConcreteAction(ConcreteAction concreteAction) {
        Position oldAgentPos = getAgentPosition();
        Position oldBoxPos = getBoxPosition();
        RelaxationMode mode = getRelaxationMode();
        Position newAgentPos, newBoxPos;
        HTNState result;

        boolean valid = true;

        switch (concreteAction.getType()) {
            case MOVE: {
                MoveConcreteAction moveAction = (MoveConcreteAction) concreteAction;
                newAgentPos = pls.getAdjacentPositionInDirection(oldAgentPos, moveAction.getDirection());

                result = new HTNState(newAgentPos, oldBoxPos, pls, mode);

                if (result.isLegal()) {
                    return result;
                }
                return null;
            }
            case PUSH: {

                PushConcreteAction pushAction = (PushConcreteAction) concreteAction;

                newAgentPos = pls.getAdjacentPositionInDirection(oldAgentPos, pushAction.getAgentDirection());
                newBoxPos = pls.getAdjacentPositionInDirection(oldBoxPos, pushAction.getBoxMovingDirection());
                result = new HTNState(newAgentPos, newBoxPos, pls, mode);

                // check preconditions !!! THIS IS PUSH
                // NOT opposite directions (would be pull!)
                valid &= !pushAction.getAgentDirection().getInverse().equals(pushAction.getBoxMovingDirection());
                // post conditions
                // Push: agent follows box
                valid &= newAgentPos.equals(oldBoxPos);
                // Push: agent is not at wrong location
                valid &= !newBoxPos.equals(oldAgentPos);

                break;
            }
            case PULL: {

                PullConcreteAction pullAction = (PullConcreteAction) concreteAction;

                newAgentPos = pls.getAdjacentPositionInDirection(oldAgentPos, pullAction.getAgentDirection());
                newBoxPos = pls.getAdjacentPositionInDirection(oldBoxPos, pullAction.getBoxMovingDirection());
                result = new HTNState(newAgentPos, newBoxPos, pls, mode);

                // check preconditions !!! THIS IS PULL
                // NOT same directions (would be push)
                // old - valid &= !pullAction.getAgentDirection().getInverse().equals(pullAction.getBoxMovingDirection());
                valid &= !pullAction.getAgentDirection().equals(pullAction.getBoxDirection());
                // post conditions
                // Pull: box follows agent
                valid &= newBoxPos.equals(oldAgentPos);
                // Pull: agent is not at wrong location
                valid &= (!newAgentPos.equals(oldBoxPos));
                break;
            }
            case NONE: {
                return this;
            }
            default:
                throw new UnsupportedOperationException("Invalid action type");
        }
        // box and agent is neighbor in prior state // is this unnecessary?
        valid &= oldAgentPos.isAdjacentTo(oldBoxPos);
        // box and agent are still neighbours in posterior state
        valid &= newAgentPos.isAdjacentTo(newBoxPos);
        valid &= result.isLegal();

        return (valid) ? result : null;
    }

    /**
     * Checks if the purpose of the current high level action is fulfilled
     *
     * @param abstractAction To be checked for fulfilled purpose
     * @return boolean True if purpose is fulfilled
     */
    public boolean isPurposeFulfilled(AbstractAction abstractAction) {
        boolean fulfilled = false;

        if (abstractAction instanceof HLAction) {
            HLAction action = (HLAction) abstractAction;
            switch (abstractAction.getType()) {

                case RGotoAction:
                    if (action.getBox() == null) {
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
        result = prime * result + ((boxPosition != null) ? boxPosition.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        String s;
        s = "State:[Ag:" + agentPosition.toString();
        s += ",Bx:" + ((boxPosition != null) ? boxPosition.toString() : "null") + "]";
        return s;
    }

}
