package dtu.agency.events;

import com.google.common.eventbus.EventBus;

public class EventBusService {

    private static final EventBus eventBus = new EventBus();

    public static EventBus getEventBus() {
        return eventBus;
    }
}
