package dtu.agency.threads;

import dtu.agency.board.Agent;
import dtu.agency.services.AgentService;
import dtu.agency.services.BDIService;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AgentThreadPoolExecutor extends ThreadPoolExecutor {

    /**
     * Create a new {code: @AgentThreadPoolExecutor} from its parent
     * @param threadPoolExecutor
     */
    public AgentThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor) {
        super(
                threadPoolExecutor.getCorePoolSize(),
                threadPoolExecutor.getMaximumPoolSize(),
                60,
                TimeUnit.SECONDS,
                threadPoolExecutor.getQueue(),
                threadPoolExecutor.getThreadFactory(),
                threadPoolExecutor.getRejectedExecutionHandler()
        );
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {

        // get an available agent
        Agent agent = null;
        try {
            agent = AgentService.getInstance().take();
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }

        System.err.println(t.getName() + ": Starting thread for agent " + agent.getLabel());

        BDIService bdiService = AgentService.getInstance().getBDIServiceInstance(agent);

        // Instantiate ThreadLocal inside BDIService
        BDIService.setInstance(bdiService);

        // invoke parent implementation
        super.beforeExecute(t, r);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {

        System.err.println("Finished executing task for agent " + BDIService.getInstance().getAgent().getLabel());

        // Add agent back into queue
        AgentService.getInstance().addAgent(
                BDIService.getInstance().getAgent(),
                BDIService.getInstance()
        );

        // invoke parent implementation
        super.afterExecute(r, t);
    }
}
