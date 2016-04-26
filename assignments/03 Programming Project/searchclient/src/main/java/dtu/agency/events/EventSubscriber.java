package dtu.agency.events;

import java.io.Serializable;

public interface EventSubscriber<T extends Event> extends Serializable {

    /**
     * Listens for event
     * @param event Event
     */
    void changeSubscriber(T event);
}
