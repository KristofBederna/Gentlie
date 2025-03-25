package inf.elte.hu.gameengine_javafx.Misc.EventHandling;

/**
 * Interface for handling events within the game engine.
 * <br>
 * Implementing classes should define how to process specific event types.
 *
 * @param <T> The type of event this listener handles.
 */
public interface EventListener<T extends Event> {

    /**
     * Called when an event of type {@code T} occurs.
     *
     * @param event The event instance to be processed.
     */
    void onEvent(T event);
}
