package dtu.agency.services;

import dtu.agency.actions.abstractaction.SolveGoalAction;
import dtu.agency.actions.abstractaction.rlaction.RGotoAction;
import dtu.agency.actions.abstractaction.rlaction.RLAction;
import dtu.agency.agent.bdi.GoalIntention;
import dtu.agency.agent.bdi.Ideas;
import dtu.agency.agent.bdi.Intention;
import dtu.agency.agent.bdi.MoveBoxFromPathIntention;
import dtu.agency.board.*;
import dtu.agency.planners.hlplanner.HLPlanner;
import dtu.agency.planners.htn.HTNPlanner;
import dtu.agency.planners.htn.RelaxationMode;
import dtu.agency.planners.plans.HLPlan;
import dtu.agency.planners.plans.PrimitivePlan;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

/**
 * Purpose of this BDIService is for the agent to be able to compare
 * own state to global, to enable it to interact by re-planning/communicating
 * in execution phase
 */
public class BDIService {

    private Agent agent;
    private HLPlan currentHLPlan;
    private HashMap<String, Intention> intentions;
    private BDILevelService bdiLevelService;

    private static ThreadLocal<BDIService> threadLocal = new ThreadLocal<>();

    /**
     * We must call setInstance() before it becomes available
     *
     * @param bdiService
     */
    public static void setInstance(BDIService bdiService) {
        threadLocal.set(bdiService);
    }

    public static synchronized BDIService getInstance() {
        return threadLocal.get();
    }

    public BDIService(Agent agent) {
        this.agent = agent;

        Level levelClone = GlobalLevelService.getInstance().getLevelClone();
        bdiLevelService = new BDILevelService(levelClone);

        currentHLPlan = new HLPlan();
        intentions = new HashMap<>();
    }

    /**
     * Interface actions  /  API to an agent's mind
     * These actions return an actual answer
     */

    /**
     * @return Who do I think I am
     */
    public Agent getAgent() {
        return agent;
    }

    /**
     * @return Where do I Think I am
     */
    public Position getAgentCurrentPosition() {
        return bdiLevelService.getPosition(agent);
    }

    /**
     * @return My current perception of the environment/level surrounding me, in it's current state
     */
    public BDILevelService getBDILevelService() {
        return bdiLevelService;
    }

    /**
     * Update the current BDI level to the Global
     */
    public void updateBDILevelService() {
        bdiLevelService.setLevel(GlobalLevelService.getInstance().getLevelClone());
    }

    /**
     * @param label The goal solved by this intention
     * @return My previously chosen intention, concerning this goal, thought of at a prior state
     */
    public Intention getIntention(String label) {
        return intentions.get(label);
    }

    /**
     * Public access to my thought routines,
     * some are just changing my inner state, but not returning anything
     * while other are more sporadic thoughts returning stuff that is not stored
     */

    /**
     * Fills the data structure containing information on ways to solve this
     * particular target goal, by one node per box that could potentially
     * solve this goal
     *
     * @param goal The goal targeted
     * @return The ideas (list of SolveGoalActions) that could potentially solve this goal.
     */
    public Ideas thinkOfIdeas(Goal goal) {
        PlanningLevelService pls = new PlanningLevelService(bdiLevelService.getLevelClone());
        Ideas ideas = new Ideas(goal, pls); // agent destination is used for heuristic purpose

        for (Box box : pls.getLevel().getBoxes()) {
            if (box.getColor().equals(agent.getColor())) {
                if (box.canSolveGoal(goal)) {
                    SolveGoalAction solveGoalAction = new SolveGoalAction(box, goal);
                    ideas.add(solveGoalAction);
                } else {
                    // TODO: What do we do here?
                }
            }
        }
        return ideas;
    }

    /**
     * Select the best idea from the top five ideas, and evolve it into a desire
     */
    public boolean findGoalIntention(Ideas ideas, Goal goal) { // Belief is handled internally by pls
        PlanningLevelService pls = new PlanningLevelService(bdiLevelService.getLevelClone());
        GoalIntention bestIntention = null;
        int bestApproximation = Integer.MAX_VALUE;
        int counter = (ideas.getIdeas().size() < 5) ? ideas.getIdeas().size() : 5;

        // plan only 5 best initial heuristics, but if none of those are valid -> keep planning.
        while (counter > 0 || bestIntention == null) {
            counter--;
            if (ideas.getIdeas().isEmpty()) break;

            SolveGoalAction idea = ideas.getBest();
            Box targetBox = idea.getBox();

            Position targetBoxPosition = pls.getPosition(targetBox);
            BoardCell boxCell = pls.getCell(targetBoxPosition);

            if (boxCell == BoardCell.BOX_GOAL) {
                BoxAndGoal boxAndGoal = (BoxAndGoal) pls.getObject(targetBoxPosition);
                if (boxAndGoal.isSolved()) {
                    continue;
                }
            }

            HTNPlanner htn = new HTNPlanner(pls, idea, RelaxationMode.NoAgentsNoBoxes);
            PrimitivePlan pseudoPlan = htn.plan();
            if (pseudoPlan == null) {
                continue;
            }
            // the positions of the cells that the agent is going to step on top of
            LinkedList<Position> agentPseudoPath = pls.getOrderedPath(pseudoPlan);
            LinkedList<Position> agentBoxPseudoPath = pls.getOrderedPathWithBox(pseudoPlan);
            LinkedList<Position> obstaclePositions = pls.getObstaclePositions(agentPseudoPath);

            // see how many obstacles on the path are reachable (cheaper) / unreachable (more expensive)
            int nReachable = 0;
            ListIterator iterator = obstaclePositions.listIterator(0);

            while (iterator.hasNext()) {
                Position next = (Position) iterator.next();
                nReachable++;
                if (targetBoxPosition.equals(next)) {
                    break;
                }
            }

            int nUnReachable = obstaclePositions.size() - nReachable;

            GoalIntention intention = new GoalIntention(
                    goal,
                    targetBox,
                    pseudoPlan,
                    agentPseudoPath,
                    agentBoxPseudoPath,
                    obstaclePositions,
                    nReachable,
                    nUnReachable
            );
            if (intention.getApproximateSteps() < bestApproximation) {
                bestIntention = intention;
                bestApproximation = intention.getApproximateSteps();
            }
        }
        if (bestIntention != null) {
            getIntentions().put(goal.getLabel(), bestIntention);
            return true;
        }
        return false;
    }

    /**
     * Create MoveBoxFromPathIntention
     */
    public boolean findMoveBoxFromPathIntention(LinkedList<Position> path,
                                                Box targetBox) {

        PlanningLevelService pls = new PlanningLevelService(bdiLevelService.getLevelClone());

        Position targetBoxPosition = pls.getPosition(targetBox);

        // Move agent next to the boc
        RLAction moveAgentAction = new RGotoAction(
                targetBox,
                targetBoxPosition
        );

        HTNPlanner htn = new HTNPlanner(pls, moveAgentAction, RelaxationMode.NoAgentsNoBoxes);
        PrimitivePlan pseudoPlan = htn.plan();

        // the positions of the cells that the agent is going to step on top of
        LinkedList<Position> agentPseudoPath = pls.getOrderedPath(pseudoPlan);
        LinkedList<Position> agentBoxPseudoPath = pls.getOrderedPathWithBox(pseudoPlan);
        LinkedList<Position> obstaclePositions = pls.getObstaclePositions(agentPseudoPath);

        MoveBoxFromPathIntention intention = new MoveBoxFromPathIntention(
                targetBox,
                pseudoPlan,
                agentPseudoPath,
                agentBoxPseudoPath,
                obstaclePositions,
                obstaclePositions.size(),
                0,
                path
        );

        if (pseudoPlan == null) {
            return false;
        }

        getIntentions().put(targetBox.getLabel(), intention);
        return true;
    }

    /**
     * This method tries to solve the level as best as possible at current state
     *
     * @param goal The goal to be solved
     * @return The success whether a plan was found, to solving this goal
     */
    public boolean solveGoal(Goal goal) {
        // Continue solving this goal, using the Intention found in the bidding round
        GoalIntention intention = (GoalIntention) intentions.get(goal.getLabel());
        PlanningLevelService pls = new PlanningLevelService(bdiLevelService.getLevelClone());

        HLPlanner planner = new HLPlanner(intention, pls);

        HLPlan hlPlan = planner.plan(
                intention,
                new HLPlan()
        );

        // Check the result of this planning phase, and return success
        if (hlPlan != null) {
            currentHLPlan = hlPlan;
            return true;
        }
        return false;
    }

    /**
     * @param box
     * @return
     */
    public boolean solveMoveBox(Box box) {

        PlanningLevelService pls = new PlanningLevelService(bdiLevelService.getLevelClone());

        Intention intention = intentions.get(box.getLabel());
        HLPlanner planner = new HLPlanner(intention, pls);

        HLPlan hlPlan = planner.plan(
                intention,
                new HLPlan()
        );

        // Check the result of this planning phase, and return success
        if (hlPlan != null) {
            currentHLPlan = hlPlan;
            return true;
        }
        return false;
    }

    /**
     * Convert my entire set of remaining High Level Plans to Concrete Actions,
     * and append them to my current Low Level Plan
     */
    public PrimitivePlan getPrimitivePlan() {

        PrimitivePlan plan = new PrimitivePlan();
        plan.appendActions(currentHLPlan.evolve(new PlanningLevelService(bdiLevelService.getLevelClone())));

        currentHLPlan.getActions().clear();

        return plan;
    }

    public HLPlan getCurrentHLPlan() {
        return currentHLPlan;
    }

    /**
     * Private thought routines - Memory I/O, etc.
     */

    /**
     * @return Retrieve memory of Intentions, given a goal..
     */
    private HashMap<String, Intention> getIntentions() {
        return intentions;
    }

    /**
     * @return All intentions for this agent
     */
    public List<Intention> getAgentIntentions() {
        return intentions.values().stream().collect(Collectors.toList());
    }
}
