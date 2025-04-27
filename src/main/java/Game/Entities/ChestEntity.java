package Game.Entities;

import Game.Components.HealthComponent;
import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.HitBoxComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.CentralMassComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.DimensionComponent;
import inf.elte.hu.gameengine_javafx.Components.RenderingComponents.ImageComponent;
import inf.elte.hu.gameengine_javafx.Components.RenderingComponents.ZIndexComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Rectangle;

public class ChestEntity extends Entity {
    public ChestEntity(double x, double y, double width, double height) {
        this.getComponent(PositionComponent.class).setLocal(new Point(x - width / 2, y - height / 2), this);
        this.addComponent(new DimensionComponent(width, height));
        this.addComponent(new CentralMassComponent(x, y));
        this.addComponent(new ImageComponent("/assets/images/box.png", width, height));
        this.addComponent(new ZIndexComponent(2));
        this.addComponent(new HealthComponent(1));
        this.addComponent(new HitBoxComponent(new Rectangle(new Point(x - width / 2, y - height / 2), width, height).getPoints()));

        addToManager();
    }
}
