package dtu.agency.agent;

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
                1,
                TimeUnit.SECONDS,
                threadPoolExecutor.getQueue(),
                threadPoolExecutor.getThreadFactory(),
                threadPoolExecutor.getRejectedExecutionHandler()
        );
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        // invoke parent implementation
        super.beforeExecute(t, r);

        // get an available agent
        /*
        Agent agent = null;
        try {
            agent = AgentService.getInstance().take();
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }

        BDIService bdiService = AgentService.getInstance().getBDIServiceInstance(agent);

        // Instantiate ThreadLocal inside BDIService
        BDIService.setInstance(bdiService);
        */
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        // invoke parent implementation
        super.afterExecute(r, t);

        // Add agent back into queue
        /*
        AgentService.getInstance().addAgent(
                BDIService.getInstance().getAgent(),
                BDIService.getInstance()
        );
        */
    }
}
