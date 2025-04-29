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

public class EventTileSystem extends GameSystem {
    @Override
    public void start() {
        this.active = true;
    }

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

    private void processEvents(Entity event, EventManager eventManager, List<Entity> player) {
        if (event == null) {
            return;
        }
        eventManager.registerListener(event.getComponent(EventComponent.class).getEvent().getClass(), event.getComponent(EventComponent.class).getEventHandler());
        determineCollision(event, eventManager, player);
    }

    private void determineCollision(Entity event, EventManager eventManager, List<Entity> player) {
        if (Shape.intersect(event.getComponent(ShapeComponent.class).getShape(), player.getFirst().getComponent(HitBoxComponent.class).getHitBox())) {
            handleOnCollision(event, eventManager);
        } else {
            handleNoCollision(event);
        }
    }

    private void handleNoCollision(Entity event) {
        checkIfExitedCollision(event);
        event.getComponent(isInsideEventComponent.class).setInside(false, null);
    }

    private void checkIfExitedCollision(Entity event) {
        if (event.getComponent(isInsideEventComponent.class).isInside()) {
            event.getComponent(EventComponent.class).getEventHandler().onExit(event.getComponent(EventComponent.class).getEvent());
        }
    }

    private void handleOnCollision(Entity event, EventManager eventManager) {
        if (event.getComponent(EventComponent.class).getEvent() instanceof OpenShopEvent && event.getComponent(isInsideEventComponent.class).isInside()) {
            return;
        }
        eventManager.fireEvent(event.getComponent(EventComponent.class).getEvent());
        event.getComponent(isInsideEventComponent.class).setInside(true, event);
    }
}
