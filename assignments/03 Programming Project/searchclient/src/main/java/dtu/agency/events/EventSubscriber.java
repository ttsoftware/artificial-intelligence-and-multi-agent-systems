package dtu.agency.events;

import com.google.common.eventbus.Subscribe;

public interface EventSubscriber<T extends Event> {

    /**
     * Listens for event
     * @param event Event
     */
    @Subscribe
    void changeSubscriber(T event);
}
