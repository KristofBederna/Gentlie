package Game.Systems;

import Game.Components.EventComponent;
import inf.elte.hu.gameengine_javafx.Components.HitBoxComponents.HitBoxComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.PlayerComponent;
import inf.elte.hu.gameengine_javafx.Components.ShapeComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Shape;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.Event;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.EventListener;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.EventManager;

public class EventTileSystem extends GameSystem {
    @Override
    public void start() {
        this.active = true;
    }

    @Override
    protected void update() {
        EventManager eventManager = new EventManager();
        var entities = EntityHub.getInstance().getEntitiesWithComponent(EventComponent.class);
        var player = EntityHub.getInstance().getEntitiesWithComponent(PlayerComponent.class);

        for (var entity : entities) {
            eventManager.registerListener(entity.getComponent(EventComponent.class).getEvent().getClass(), entity.getComponent(EventComponent.class).getEventHandler());
            if (Shape.intersect(entity.getComponent(ShapeComponent.class).getShape(), player.getFirst().getComponent(HitBoxComponent.class).getHitBox())) {
                eventManager.fireEvent(entity.getComponent(EventComponent.class).getEvent());
            };
        }
    }
}
