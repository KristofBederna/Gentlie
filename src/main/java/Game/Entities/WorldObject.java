package Game.Entities;

import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.HitBoxComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.CentralMassComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.DimensionComponent;
import inf.elte.hu.gameengine_javafx.Components.RenderingComponents.ImageComponent;
import inf.elte.hu.gameengine_javafx.Components.RenderingComponents.ZIndexComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Rectangle;

public class WorldObject extends Entity {
    public WorldObject(double x, double y, double width, double height, String path, boolean hasHitBox, int ZIndex) {
        this.getComponent(PositionComponent.class).setLocal(new Point(x, y), this);
        this.addComponent(new DimensionComponent(width, height));
        this.addComponent(new ImageComponent(path, width, height));
        this.addComponent(new ZIndexComponent(ZIndex));
        this.addComponent(new CentralMassComponent(x + width / 2, y + height / 2));

        if (hasHitBox) {
            this.addComponent(new HitBoxComponent(new Rectangle(new Point(x, y), width, height).getPoints()));
        }

        addToManager();
    }
}
