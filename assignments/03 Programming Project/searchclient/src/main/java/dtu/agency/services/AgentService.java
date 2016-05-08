package dtu.agency.services;

import dtu.agency.board.Agent;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

public class AgentService {

    private static ArrayBlockingQueue<Agent> availableAgents;
    private static final Map<String, BDIService> bdiServices
            = Collections.synchronizedMap(new HashMap<>());

    private static AgentService instance;

    public static AgentService getInstance() {
        if (instance == null) {
            instance = new AgentService();
        }
        return instance;
    }

    /**
     * Add the Agent back along with its BDIServices
     *
     * @param agent
     * @param bdiService
     */
    public void addAgent(Agent agent, BDIService bdiService) {
        synchronized (bdiServices) {
            // availableAgents.add(agent);
            bdiServices.put(agent.getLabel(), bdiService);
            bdiServices.notifyAll();
        }
    }

    public void addAgents(List<Agent> agentList) {
        agentList.forEach(agent -> {
            // Create BDIServices for each agent if it does not exist
            BDIService bdiService = new BDIService(agent);
            bdiServices.put(agent.getLabel(), bdiService);
        });

        availableAgents = new ArrayBlockingQueue<>(agentList.size());
        availableAgents.addAll(agentList);
    }

    /**
     * Take an agent from the queue
     * block calling thread until an agent becomes available
     *
     * @return
     */
    public Agent take() throws InterruptedException {
        return availableAgents.take();
    }

    /**
     * Returns the {@code BDIService} associated with the given agent
     * Blocks calling thread until it becomes available
     *
     * @param agent
     * @return
     */
    public BDIService getBDIServiceInstance(Agent agent) {
        while ((bdiServices.get(agent.getLabel())) == null) {
            try {
                synchronized (bdiServices) {
                    bdiServices.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
        }
        return bdiServices.remove(agent.getLabel());
    }
}
