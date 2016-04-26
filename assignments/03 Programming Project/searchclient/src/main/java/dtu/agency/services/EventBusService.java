package dtu.agency.services;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;

public class EventBusService {

    private static final EventBus eventBus = new AsyncEventBus(ThreadService.getAgentExecutor());

    public static EventBus getEventBus() {
        return eventBus;
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
}
