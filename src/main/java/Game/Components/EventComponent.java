package Game.Components;

import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.Event;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.EventListener;

public class EventComponent<T extends Event> extends Component {
    private T event;
    private EventListener<T> eventHandler;

    public EventComponent(T event, EventListener<T> eventHandler) {
        this.event = event;
        this.eventHandler = eventHandler;
    }

    public Event getEvent() {
        return event;
    }

    public EventListener<T> getEventHandler() {
        return eventHandler;
    }

    @Override
    public String getStatus() {
        return "";
    }
}
