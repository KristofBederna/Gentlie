package Game.Systems;

import Game.Components.HealthComponent;
import Game.Entities.SnowBallEntity;
import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.VelocityComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.CentralMassComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Entities.ParticleEmitterEntity;
import inf.elte.hu.gameengine_javafx.Entities.ParticleEntity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.NSidedShape;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Misc.Direction;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class RemoveDeadObjectSystem extends GameSystem {
    @Override
    public void start() {
        this.active = true;
    }

    @Override
    protected void update() {
        var entities = EntityHub.getInstance().getEntitiesWithComponent(HealthComponent.class);
        var dead = new ArrayList<Entity>();
        for (var entity : entities) {
            if (!entity.getComponent(HealthComponent.class).isAlive()) {
                dead.add(entity);
            }
            if (entity instanceof SnowBallEntity) {
                if (Math.abs(entity.getComponent(VelocityComponent.class).getVelocity().getDx()) < 0.5 && Math.abs(entity.getComponent(VelocityComponent.class).getVelocity().getDy()) < 0.5) {
                    dead.add(entity);
                }
            }
        }
        for (var entity : dead) {
            EntityHub.getInstance().removeEntity(entity);
        }
    }
}
