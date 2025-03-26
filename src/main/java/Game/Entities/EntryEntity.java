package Game.Entities;

import Game.Components.EventComponent;
import Game.Components.isInsideEventComponent;
import Game.Misc.EventHandling.EventListeners.EnterHomeEventListener;
import Game.Misc.EventHandling.EventListeners.ExitHomeEventListener;
import Game.Misc.EventHandling.Events.EnterHomeEvent;
import Game.Misc.EventHandling.Events.ExitHomeEvent;
import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.DimensionComponent;
import inf.elte.hu.gameengine_javafx.Components.ShapeComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Entities.UIEntities.LabelEntity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Rectangle;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.Event;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.EventListener;

public class EntryEntity extends Entity {
    public EntryEntity(double x, double y, double width, double height, Event event, EventListener<?> listener) {
        getComponent(PositionComponent.class).setGlobal(new Point(x, y));
        addComponent(new DimensionComponent(width, height));
        addComponent(new ShapeComponent<>(new Rectangle(new Point(x, y), width, height)));
        addComponent(new EventComponent(event, listener));
        addComponent(new isInsideEventComponent());

        addToManager();
    }
}
