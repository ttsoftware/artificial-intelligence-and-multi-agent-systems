package dtu.events;

import com.google.common.eventbus.Subscribe;

public class EventSubscriber {

    @Subscribe
    public void change(Event event) {
        // Do something with the event
    }
}
