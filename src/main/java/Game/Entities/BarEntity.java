package Game.Entities;

import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.HitBoxComponents.HitBoxComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.CentralMassComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.DimensionComponent;
import inf.elte.hu.gameengine_javafx.Components.RenderingComponents.ImageComponent;
import inf.elte.hu.gameengine_javafx.Components.RenderingComponents.ZIndexComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Rectangle;

public class BarEntity extends Entity {
    public BarEntity(double x, double y, double width, double height) {
        this.getComponent(PositionComponent.class).setGlobal(new Point(x, y));
        this.addComponent(new DimensionComponent(width, height));
        this.addComponent(new HitBoxComponent(new Rectangle(new Point(x, y), width, height).getPoints()));
        this.addComponent(new ImageComponent("/assets/images/Inn_Bar.png", width, height));
        this.addComponent(new ZIndexComponent(4));
        this.addComponent(new CentralMassComponent(x + width / 2, y + height / 2));

        addToManager();
    }
}
