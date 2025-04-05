package Game.Entities;

import Game.Components.HealthComponent;
import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.HitBoxComponents.HitBoxComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.AccelerationComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.DragComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.MassComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.VelocityComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.CentralMassComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.DimensionComponent;
import inf.elte.hu.gameengine_javafx.Components.RenderingComponents.ImageComponent;
import inf.elte.hu.gameengine_javafx.Components.RenderingComponents.ZIndexComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.NSidedShape;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Maths.Vector;

public class SnowBallEntity extends Entity {
    public SnowBallEntity(double x, double y, double width, double height, Vector initialVelocity) {
        getComponent(PositionComponent.class).setLocal(new Point(x, y), this);
        addComponent(new HealthComponent(1));
        addComponent(new CentralMassComponent(x+width/2, y+height/2));
        addComponent(new DimensionComponent(width, height));
        addComponent(new HitBoxComponent(new NSidedShape(new Point(x+width/2, y+height/2), width/2, 32).getPoints()));
        addComponent(new ImageComponent("/assets/images/Snowball.png", width, height));
        addComponent(new ZIndexComponent(3));
        addComponent(new VelocityComponent(initialVelocity, 15));
        addComponent(new AccelerationComponent());
        addComponent(new DragComponent(0.95));
        addComponent(new MassComponent(0.5));

        addToManager();
    }
}
