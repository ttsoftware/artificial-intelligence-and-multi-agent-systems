package dtu.agency.services;

import dtu.agency.agent.AgentThread;
import dtu.agency.agent.AgentThreadPoolExecutor;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadService {

    private static AgentThreadPoolExecutor agentThreadExecutor;

    public static void execute(AgentThread thread) {
        agentThreadExecutor.execute(thread);
    }

    public static void shutdown() {
        agentThreadExecutor.shutdown();
    }

    public static AgentThreadPoolExecutor getAgentExecutor() {
        return agentThreadExecutor;
    }

    public static void setNumberOfAgents(int numberOfAgents) {
        agentThreadExecutor = new AgentThreadPoolExecutor(
                (ThreadPoolExecutor) Executors.newFixedThreadPool(numberOfAgents, Thread::new)
        );
    }
}
