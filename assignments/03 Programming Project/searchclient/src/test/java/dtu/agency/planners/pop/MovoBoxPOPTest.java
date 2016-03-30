package dtu.agency.planners.pop;

public class MovoBoxPOPTest {

    /*
    Level level;
    Agent agent;
    Box box;
    Position agentPosition;
    Position boxPosition;

    @Before
    public void init() throws IOException {
        File resourcesDirectory = new File("src/test/resources");
        String levelPath = resourcesDirectory.getAbsolutePath() + "/SAD1.lvl";

        FileInputStream inputStream = new FileInputStream(levelPath);
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream));

        // Parse the level
        Level level = ProblemMarshaller.marshall(fileReader);

        agent = level.getAgents().get(0);
        box = level.getBoxes().get(0);

        gotoPOP = new GotoPOP(agent);
        moveBoxPOP = new MoveBoxPOP(agent);

        agentPosition = level.getBoardObjectPositions().get(agent.getLabel());
        boxPosition = level.getBoardObjectPositions().get(box.getLabel());
    }

    @Test
    public void testMovePreconditions() {

        MoveAction moveAction = new MoveAction(Direction.EAST, agent, agentPosition);

        List<Precondition> preconditions = moveAction.getPreconditions();

        assertEquals (preconditions.size(), 1);

        AgentAtPrecondition agentAtPrecondition = (AgentAtPrecondition) preconditions.get(0);

        assertEquals(agentAtPrecondition.getAgent(), agent);

        assertEquals(agentAtPrecondition.getAgentPreconditionPosition().getRow(), 1);
        assertEquals(agentAtPrecondition.getAgentPreconditionPosition().getColumn(), 1);
    }

    @Test
    public void testPushPreconditions() {

        PushAction pushAction = new PushAction(box, boxPosition, agent, agentPosition, Direction.EAST, Direction.NORTH);

        List<Precondition> preconditions = pushAction.getPreconditions();

        assertEquals (preconditions.size(), 1);

        BoxAtPrecondition boxAtPrecondition = (BoxAtPrecondition) preconditions.get(0);

        assertEquals(boxAtPrecondition.getBox(), box);
        assertEquals(boxAtPrecondition.getAgent(), agent);

        assertEquals(boxAtPrecondition.getBoxPosition().getRow(), 4);
        assertEquals(boxAtPrecondition.getBoxPosition().getColumn(), 17);
    }

    @Test
    public void testPullPreconditions() {

        PullAction pullAction = new PullAction(box, boxPosition, agent, agentPosition, Direction.EAST, Direction.NORTH);

        List<Precondition> preconditions = pullAction.getPreconditions();

        assertEquals (preconditions.size(), 1);

        BoxAtPrecondition boxAtPrecondition = (BoxAtPrecondition) preconditions.get(0);

        assertEquals(boxAtPrecondition.getBox(), box);
        assertEquals(boxAtPrecondition.getAgent(), agent);

        assertEquals(boxAtPrecondition.getBoxPosition().getRow(), 4);
        assertEquals(boxAtPrecondition.getBoxPosition().getColumn(), 17);
    }

    @Test
    public void testSolveAgentAtPrecondition() {
        AgentAtPrecondition agentAtPrecondition = new AgentAtPrecondition(agent, new Position(5, 5));

        PriorityQueue<Action> actions = gotoPOP.solvePrecondition(agentAtPrecondition);

        assertEquals(3, actions.size());

        for (Action action : actions) {
            System.out.println(action.toString());
            System.out.println(action.getHeuristic());
        }
    }

    @Test
    public void testSolveBoxAtPrecondition() {
        BoxAtPrecondition boxAtPrecondition = new BoxAtPrecondition(box, agent, new Position(5, 5));

        PriorityQueue<Action> actions = moveBoxPOP.solvePrecondition(boxAtPrecondition);

        for (Action action : actions) {
            System.out.println(action.toString());
            System.out.println(action.getHeuristic());
        }
    }
    */
}
