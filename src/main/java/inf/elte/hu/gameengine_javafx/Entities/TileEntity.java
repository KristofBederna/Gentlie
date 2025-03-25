package inf.elte.hu.gameengine_javafx.Entities;

import inf.elte.hu.gameengine_javafx.Components.HitBoxComponents.HitBoxComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.FrictionComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.CentralMassComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.DimensionComponent;
import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.RenderingComponents.ImageComponent;
import inf.elte.hu.gameengine_javafx.Components.RenderingComponents.ZIndexComponent;
import inf.elte.hu.gameengine_javafx.Components.TileValueComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Rectangle;

public class TileEntity extends Entity {
    public TileEntity(int value, double x, double y, String path, double width, double height) {
        this.addComponent(new TileValueComponent(value));
        this.addComponent(new ImageComponent(path, width, height));
        this.getComponent(PositionComponent.class).setLocalPosition(x, y, this);
        this.addComponent(new ZIndexComponent(0));
        this.addComponent(new DimensionComponent(width, height));
        this.addComponent(new CentralMassComponent(x + width / 2, y + height / 2));
        this.addComponent(new FrictionComponent(4));

        addToManager();
    }

    public TileEntity(int value, double x, double y, String path, double width, double height, boolean hasHitBox) {
        this.addComponent(new TileValueComponent(value));
        this.addComponent(new ImageComponent(path, width, height));
        this.getComponent(PositionComponent.class).setLocalPosition(x, y, this);
        this.addComponent(new ZIndexComponent(0));
        this.addComponent(new DimensionComponent(width, height));
        this.addComponent(new CentralMassComponent(x + width / 2, y + height / 2));
        if (hasHitBox) {
            this.addComponent(new HitBoxComponent(new Rectangle(new Point(x, y), width, height).getPoints()));
        }
        this.addComponent(new FrictionComponent(4));

        addToManager();
    }

    public TileEntity(int value, double x, double y, String path) {
        this.addComponent(new TileValueComponent(value));
        this.addComponent(new ImageComponent(path));
        this.getComponent(PositionComponent.class).setLocalPosition(x, y, this);
        this.addComponent(new ZIndexComponent(0));

        addToManager();
    }

    public void addHitBox(int x, int y, double width, double height) {
        this.addComponent(new HitBoxComponent(new Rectangle(new Point(x, y), width, height).getPoints()));
    }
}
