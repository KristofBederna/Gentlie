package inf.elte.hu.gameengine_javafx.Entities;

import inf.elte.hu.gameengine_javafx.Components.ShapeComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Rectangle;

public class DebugEntity extends Entity {
    public DebugEntity(Rectangle rectangle) {
        this.addComponent(new ShapeComponent<>(rectangle));
        addToManager();
    }
}
