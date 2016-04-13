package dtu.agency.services;

import dtu.agency.board.Agent;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class AgentService {

    private static ArrayBlockingQueue<Agent> availableAgents;
    private static final ConcurrentHashMap<String, BDIService> bdiServices = new ConcurrentHashMap<>();

    private static AgentService instance;

    public static AgentService getInstance() {
        if (instance == null) {
            instance = new AgentService();
        }
        return instance;
    }

    /**
     * Add the Agent back along with its BDIServices
     * @param agent
     * @param bdiService
     */
    public void addAgent(Agent agent, BDIService bdiService) {
        synchronized (bdiServices) {
            availableAgents.add(agent);
            bdiServices.put(agent.getLabel(), bdiService);
            bdiServices.notify();
        }
    }

    public void addAgents(List<Agent> agentList) {
        availableAgents = new ArrayBlockingQueue<>(agentList.size());
        availableAgents.addAll(agentList);
    }

    /**
     * Take an agent from the queue
     * block calling thread until an agent becomes available
     * @return
     */
    public Agent take() throws InterruptedException {
        Agent agent = availableAgents.take();

        if (!bdiServices.containsKey(agent.getLabel())) {
            // Create BDIServices for this agent if it does not exist
            BDIService bdiService = new BDIService(agent);
            BDIService.setInstance(bdiService);
            bdiServices.put(agent.getLabel(), BDIService.getInstance());
        }

        return agent;
    }

    /**
     * Returns the {@code BDIService} associated with the given agent
     * Blocks calling thread until it becomes available
     * @param agent
     * @return
     */
    public BDIService getBDIServiceInstance(Agent agent) {

        BDIService bdiService;

        // TODO: This could be replaced with a real guarded lock datastructure
        while ((bdiService = bdiServices.get(agent.getLabel())) == null) {
            try {
                synchronized (bdiServices) {
                    wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return bdiService;
    }
}
