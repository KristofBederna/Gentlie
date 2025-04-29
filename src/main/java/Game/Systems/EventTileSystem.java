package Game.Systems;

import Game.Components.EventComponent;
import Game.Components.isInsideEventComponent;
import Game.Misc.EventHandling.Events.OpenShopEvent;
import inf.elte.hu.gameengine_javafx.Components.HitBoxComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.PlayerComponent;
import inf.elte.hu.gameengine_javafx.Components.ShapeComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Shape;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.EventManager;

import java.util.List;

/**
 * System that handles in-game event tile collisions with the player.
 * Fires events when the player steps on or off a tile associated with an event.
 */
public class EventTileSystem extends GameSystem {

    /**
     * Initializes the system and marks it as active.
     */
    @Override
    public void start() {
        this.active = true;
    }

    /**
     * Called on each game tick. Detects interactions between the player and event entities,
     * and handles firing or exiting events accordingly.
     */
    @Override
    protected void update() {
        EventManager eventManager = new EventManager();
        var events = EntityHub.getInstance().getEntitiesWithComponent(EventComponent.class);
        var player = EntityHub.getInstance().getEntitiesWithComponent(PlayerComponent.class);

        if (player == null) {
            return;
        }

        for (var event : events) {
            processEvents(event, eventManager, player);
        }
    }

    /**
     * Registers an event handler for the given event entity and checks if the player interacts with it.
     *
     * @param event        the entity containing an event component
     * @param eventManager the event manager used to register and fire events
     * @param player       the list of player entities (expected to contain one player)
     */
    private void processEvents(Entity event, EventManager eventManager, List<Entity> player) {
        if (event == null) {
            return;
        }
        eventManager.registerListener(
                event.getComponent(EventComponent.class).getEvent().getClass(),
                event.getComponent(EventComponent.class).getEventHandler()
        );
        determineCollision(event, eventManager, player);
    }

    /**
     * Checks if the player's hitbox intersects with the event's shape,
     * and fires or exits the event accordingly.
     *
     * @param event the event entity to check collision with
     * @param eventManager the manager responsible for firing the event
     * @param player the list of player entities
     */
    private void determineCollision(Entity event, EventManager eventManager, List<Entity> player) {
        if (Shape.intersect(
                event.getComponent(ShapeComponent.class).getShape(),
                player.getFirst().getComponent(HitBoxComponent.class).getHitBox())
        ) {
            handleOnCollision(event, eventManager);
        } else {
            handleNoCollision(event);
        }
    }

    /**
     * Called when the player is no longer in contact with the event entity.
     * Fires the exit event if the player had previously entered.
     *
     * @param event the event entity being exited
     */
    private void handleNoCollision(Entity event) {
        checkIfExitedCollision(event);
        event.getComponent(isInsideEventComponent.class).setInside(false, null);
    }

    /**
     * Checks whether the player had previously entered the event's trigger zone.
     * If so, invokes the onExit behavior of the event handler.
     *
     * @param event the entity containing the event and state
     */
    private void checkIfExitedCollision(Entity event) {
        if (event.getComponent(isInsideEventComponent.class).isInside()) {
            event.getComponent(EventComponent.class)
                    .getEventHandler()
                    .onExit(event.getComponent(EventComponent.class).getEvent());
        }
    }

    /**
     * Called when the player enters or stays in contact with the event entity.
     * Prevents re-triggering non-repeatable events like shop opening.
     *
     * @param event the entity that triggered the collision
     * @param eventManager the manager used to dispatch the event
     */
    private void handleOnCollision(Entity event, EventManager eventManager) {
        boolean isShopAndAlreadyInside =
                event.getComponent(EventComponent.class).getEvent() instanceof OpenShopEvent &&
                        event.getComponent(isInsideEventComponent.class).isInside();

        if (isShopAndAlreadyInside) {
            return;
        }

        eventManager.fireEvent(event.getComponent(EventComponent.class).getEvent());
        event.getComponent(isInsideEventComponent.class).setInside(true, event);
    }
}
