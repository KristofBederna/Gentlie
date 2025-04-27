package Game.Components;

import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.Event;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.EventListener;

public class EventComponent<T extends Event> extends Component {
    private final T event;
    private final EventListener<T> eventHandler;

    public EventComponent(T event, EventListener<T> eventHandler) {
        this.event = event;
        this.eventHandler = eventHandler;
    }

    public EventComponent(T event, EventListener<T> eventHandler, T eventOnExit) {
        this.event = event;
        this.eventHandler = eventHandler;
    }

    public Event getEvent() {
        return event;
    }

    public EventListener<T> getEventHandler() {
        return eventHandler;
    }
}
