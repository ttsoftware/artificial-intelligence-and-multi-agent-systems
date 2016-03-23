package dtu.agency.services;

import com.google.common.eventbus.EventBus;

import java.io.Serializable;

public class EventBusService implements Serializable {

    private static final EventBus eventBus = new EventBus();

    public static EventBus getEventBus() {
        return eventBus;
    }
}
