package inf.elte.hu.gameengine_javafx.Entities;

import inf.elte.hu.gameengine_javafx.Components.MaxDistanceFromOriginComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.AccelerationComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.DragComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.DimensionComponent;
import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.VelocityComponent;
import inf.elte.hu.gameengine_javafx.Components.RenderingComponents.ColorComponent;
import inf.elte.hu.gameengine_javafx.Components.RenderingComponents.ImageComponent;
import inf.elte.hu.gameengine_javafx.Components.RenderingComponents.ZIndexComponent;
import inf.elte.hu.gameengine_javafx.Components.ShapeComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class ParticleEntity extends Entity {
    public ParticleEntity(double x, double y, double width, double height, Shape shape, Color color, Color strokeColor, double maxDistance) {
        this.getComponent(PositionComponent.class).setLocalPosition(x, y, this);
        addComponent(new VelocityComponent(3));
        addComponent(new DimensionComponent(width, height));
        addComponent(new ShapeComponent<>(shape));
        addComponent(new ColorComponent(color, strokeColor));
        addComponent(new ZIndexComponent(3));
        addComponent(new MaxDistanceFromOriginComponent(maxDistance));
        addComponent(new AccelerationComponent());
        addComponent(new DragComponent(0.05));
    }

    public ParticleEntity(double x, double y, double width, double height, String imagePath, double maxDistance) {
        this.getComponent(PositionComponent.class).setLocalPosition(x, y, this);
        addComponent(new VelocityComponent(3));
        addComponent(new DimensionComponent(width, height));
        addComponent(new ImageComponent(imagePath, width, height));
        addComponent(new ZIndexComponent(3));
        addComponent(new MaxDistanceFromOriginComponent(maxDistance));
        addComponent(new AccelerationComponent());
        addComponent(new DragComponent(0.05));
    }

    public void render(GraphicsContext gc) {
        if (getComponent(ImageComponent.class) != null) {
            return;
        }
        ShapeComponent<?> shapeComponent = getComponent(ShapeComponent.class);
        if (shapeComponent != null) {
            Shape shape = shapeComponent.getShape();
            if (shape instanceof Line) {
                ((Line) shape).render(gc, getComponent(ColorComponent.class).getColor(), 5);
            } else {
                shape.renderFillWithStroke(gc, getComponent(ColorComponent.class).getColor(), getComponent(ColorComponent.class).getStroke(), 1);
            }
        }
    }

    public void alignShapeWithEntity(Entity entity) {
        if (getComponent(ShapeComponent.class) == null) {
            return;
        }
        Shape shape = entity.getComponent(ShapeComponent.class).getShape();
        PositionComponent positionComponent = getComponent(PositionComponent.class);
        double x = positionComponent.getGlobalX();
        double y = positionComponent.getGlobalY();

        if (shape instanceof Rectangle) {
            shape.moveTo(new Point(x, y));
        } else if (shape instanceof ComplexShape) {
            shape.moveTo(new Point(x, y));
        } else if (shape instanceof Line) {
            shape.moveTo(new Point(x, y));
        } else if (shape instanceof NSidedShape) {
            shape.moveTo(new Point(x, y));
        } else if (shape instanceof Triangle) {
            shape.moveTo(new Point(x, y));
        }
    }

    public static ParticleEntity hardCopySelf(Entity entity) {
        PositionComponent pos = entity.getComponent(PositionComponent.class);
        DimensionComponent dim = entity.getComponent(DimensionComponent.class);
        ShapeComponent<?> shapeComponent = entity.getComponent(ShapeComponent.class);
        ImageComponent imageComponent = entity.getComponent(ImageComponent.class);
        ColorComponent col = entity.getComponent(ColorComponent.class);
        MaxDistanceFromOriginComponent maxDistance = entity.getComponent(MaxDistanceFromOriginComponent.class);
        if (shapeComponent != null) {
            return new ParticleEntity(pos.getGlobalX(), pos.getGlobalY(), dim.getWidth(), dim.getHeight(), shapeComponent.getShape(), col.getColor(), col.getStroke(), maxDistance.getMaxDistance());
        }
        if (imageComponent != null) {
            return new ParticleEntity(pos.getGlobalX(), pos.getGlobalY(), dim.getWidth(), dim.getHeight(), imageComponent.getImagePath(), maxDistance.getMaxDistance());
        }
        System.err.println("Couldn't hard copy particle entity");
        return null;
    }
}
