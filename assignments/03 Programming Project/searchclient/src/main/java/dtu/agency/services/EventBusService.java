package dtu.agency.services;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import dtu.agency.agent.AgentThread;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class EventBusService implements Serializable {

    private static List<Thread> threads = new ArrayList<>();
    private static final Executor eventBusExecutor = java.util.concurrent.Executors.newCachedThreadPool(
            r -> {
                Thread t = new Thread(r);
                threads.add(t);
                return t;
            }
    );
    private static final EventBus eventBus = new AsyncEventBus(eventBusExecutor);

    public static EventBus getEventBus() {
        return eventBus;
    }
    public static Executor getExecutor() {
        return eventBusExecutor;
    }

    public static void register(Object subscriber) {
        eventBus.register(subscriber);
    }

    public static void unregister(Object subscriber) {
        eventBus.unregister(subscriber);
    }

    public static void post(Object event) {
        eventBus.post(event);
    }

    public static void execute(AgentThread thread) {
        eventBusExecutor.execute(thread);
    }

    public static List<Thread> getThreads() {
        return threads;
    }
}
