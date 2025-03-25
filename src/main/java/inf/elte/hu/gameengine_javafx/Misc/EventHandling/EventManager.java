package inf.elte.hu.gameengine_javafx.Misc.EventHandling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages event listeners and dispatches events to registered listeners.
 * <br>
 * This class allows different parts of the game engine to communicate through events.
 */
public class EventManager {
    private final Map<Class<? extends Event>, List<EventListener<?>>> listeners = new HashMap<>();

    /**
     * Registers an event listener for a specific type of event.
     *
     * @param eventType The class type of the event to listen for.
     * @param listener  The listener that will handle the event.
     * @param <T>       The type of event.
     */
    public <T extends Event> void registerListener(Class<T> eventType, EventListener<T> listener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }

    /**
     * Fires an event, notifying all registered listeners of the event type.
     *
     * @param event The event instance to be dispatched.
     * @param <T>   The type of event being fired.
     */
    @SuppressWarnings("unchecked")
    public <T extends Event> void fireEvent(T event) {
        List<EventListener<?>> eventListeners = listeners.get(event.getClass());
        if (eventListeners != null) {
            for (EventListener<?> listener : eventListeners) {
                ((EventListener<T>) listener).onEvent(event);
            }
        }
    }
}
