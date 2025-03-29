package inf.elte.hu.gameengine_javafx.Entities;

import inf.elte.hu.gameengine_javafx.Components.HitBoxComponents.HitBoxComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.FrictionComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.CentralMassComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.DimensionComponent;
import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.RenderingComponents.ImageComponent;
import inf.elte.hu.gameengine_javafx.Components.RenderingComponents.ZIndexComponent;
import inf.elte.hu.gameengine_javafx.Components.TileValueComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.TileSetComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Core.ResourceHub;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Rectangle;
import inf.elte.hu.gameengine_javafx.Misc.Config;
import inf.elte.hu.gameengine_javafx.Misc.MapClasses.TileLoader;
import javafx.scene.image.Image;

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

    public void addHitBox(double x, double y, double width, double height) {
        this.addComponent(new HitBoxComponent(new Rectangle(new Point(x, y), width, height).getPoints()));
        EntityHub.getInstance().getComponentCache().get(HitBoxComponent.class).add(this.getId());
    }

    public void changeValues(int value) {
        this.getComponent(TileValueComponent.class).setTileValue(value);
        this.getComponent(ImageComponent.class).setImagePath("/assets/tiles/"+WorldEntity.getInstance().getComponent(TileSetComponent.class).getTileLoader().getTilePath(value)+".png");
        if (Config.wallTiles.contains(value)) {
            this.addHitBox(getComponent(PositionComponent.class).getGlobalX(), getComponent(PositionComponent.class).getGlobalY(), getComponent(DimensionComponent.class).getWidth(), getComponent(DimensionComponent.class).getHeight());
        } else {
            this.removeComponentsByType(HitBoxComponent.class);
        }
    }
}
