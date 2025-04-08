package Game.Entities;

import Game.Components.HealthComponent;
import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.HitBoxComponents.HitBoxComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.DragComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.MassComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.VelocityComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.CentralMassComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.DimensionComponent;
import inf.elte.hu.gameengine_javafx.Components.RenderingComponents.ImageComponent;
import inf.elte.hu.gameengine_javafx.Components.RenderingComponents.ZIndexComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Rectangle;
import inf.elte.hu.gameengine_javafx.Maths.Vector;

public class SnowBallEntity extends Entity {
    public SnowBallEntity(double x, double y, double width, double height, Vector initialVelocity) {
        getComponent(PositionComponent.class).setLocal(new Point(x, y), this);
        addComponent(new HealthComponent(1));
        addComponent(new CentralMassComponent(x+width/2, y+height/2));
        addComponent(new DimensionComponent(width, height));
        addComponent(new HitBoxComponent(new Rectangle(new Point(x+width*0.1, y+height*0.1), width*0.9, height*0.9).getPoints()));
        addComponent(new ImageComponent("/assets/images/Snowball.png", width, height));
        addComponent(new ZIndexComponent(4));
        addComponent(new VelocityComponent(initialVelocity, 10));
        addComponent(new DragComponent(0.95));
        addComponent(new MassComponent(0.5));

        addToManager();
    }
}
