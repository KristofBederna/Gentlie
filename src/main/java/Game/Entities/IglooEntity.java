package Game.Entities;

import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.DimensionComponent;
import inf.elte.hu.gameengine_javafx.Components.RenderingComponents.ImageComponent;
import inf.elte.hu.gameengine_javafx.Components.RenderingComponents.ZIndexComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;

public class IglooEntity extends Entity {
    public IglooEntity(double x, double y, double width, double height) {
        this.getComponent(PositionComponent.class).setGlobal(new Point(x, y));
        this.addComponent(new ImageComponent("/assets/images/igloo.png", width, height));
        this.addComponent(new DimensionComponent(width, height));
        this.addComponent(new ZIndexComponent(2));

        addToManager();
    }
}
