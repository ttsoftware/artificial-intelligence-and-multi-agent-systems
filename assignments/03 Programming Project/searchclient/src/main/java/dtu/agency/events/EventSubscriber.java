package dtu.agency.events;

public interface EventSubscriber<T extends Event> {

    /**
     * Listens for event
     * @param event Event
     */
    void changeSubscriber(T event);
}
