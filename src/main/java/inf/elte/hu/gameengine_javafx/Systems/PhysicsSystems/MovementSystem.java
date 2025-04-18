package inf.elte.hu.gameengine_javafx.Systems.PhysicsSystems;

import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.HitBoxComponents.HitBoxComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.*;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.CentralMassComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.DimensionComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.WorldDataComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Entities.TileEntity;
import inf.elte.hu.gameengine_javafx.Entities.WorldEntity;
import inf.elte.hu.gameengine_javafx.Misc.Configs.MapConfig;
import inf.elte.hu.gameengine_javafx.Misc.Configs.PhysicsConfig;
import inf.elte.hu.gameengine_javafx.Misc.IgnoreFriction;

import java.util.List;

public class MovementSystem extends GameSystem {

    @Override
    public void start() {
        this.active = true;
    }

    @Override
    public void update() {
        var entitiesSnapshot = getEntities();
        if (entitiesSnapshot.isEmpty()) return;

        for (Entity entity : entitiesSnapshot) {
            processEntity(entity);
        }
    }

    private void processEntity(Entity entity) {
        if (entity == null) return;

        var velocity = entity.getComponent(VelocityComponent.class);
        var position = entity.getComponent(PositionComponent.class);
        var acceleration = entity.getComponent(AccelerationComponent.class);

        double newDx = velocity.getVelocity().getDx();
        double newDy = velocity.getVelocity().getDy();

        newDx = applyAccelerationX(acceleration, newDx, entity);
        newDy = applyAccelerationY(acceleration, newDy, entity);

        TileEntity tile = getCurrentTile(entity);

        if (!IgnoreFriction.ignore.contains(entity.getClass())) {
            double[] frictionAdjusted = applyFriction(tile, newDx, newDy, getMass(entity));
            newDx = frictionAdjusted[0];
            newDy = frictionAdjusted[1];
        }

        double[] velocityAdjusted = applyVelocityLimitsAndDrag(entity, newDx, newDy, acceleration);
        newDx = velocityAdjusted[0];
        newDy = velocityAdjusted[1];

        velocity.setVelocity(newDx, newDy);
        position.setLocalPosition(position.getLocalX() + newDx, position.getLocalY() + newDy, entity);

        updateCentralMass(entity);
        updateHitBoxes(entity, velocity);
    }

    private double applyAccelerationX(AccelerationComponent acceleration, double currentDx, Entity entity) {
        return (acceleration != null) ? currentDx + acceleration.getAcceleration().getDx() / getMass(entity) : currentDx;
    }

    private double applyAccelerationY(AccelerationComponent acceleration, double currentDy, Entity entity) {
        return (acceleration != null) ? currentDy + acceleration.getAcceleration().getDy() / getMass(entity) : currentDy;
    }


    private double getMass(Entity entity) {
        var massComponent = entity.getComponent(MassComponent.class);
        return (massComponent != null) ? massComponent.getMass() : PhysicsConfig.defaultMass;
    }

    private double[] applyFriction(TileEntity tile, double dx, double dy, double mass) {
        double frictionCoefficient = PhysicsConfig.defaultFriction;
        if (tile != null && tile.getComponent(FrictionComponent.class) != null) {
            frictionCoefficient = tile.getComponent(FrictionComponent.class).getFriction();
        }

        // Calculate normal force (simplified: assume gravity = 1 unit down, so normal = mass)
        double normalForce = mass;

        // Friction force = μ * normalForce
        double frictionForce = frictionCoefficient * normalForce;

        // Friction acceleration = frictionForce / mass = μ (in 2D)
        double frictionAccel = frictionForce / mass * PhysicsConfig.fixedDeltaTime;  // == frictionCoefficient

        if (Math.abs(dx) > 0) dx -= Math.signum(dx) * Math.min(frictionAccel, Math.abs(dx));
        if (Math.abs(dy) > 0) dy -= Math.signum(dy) * Math.min(frictionAccel, Math.abs(dy));

        return new double[]{dx, dy};
    }


    private double[] applyVelocityLimitsAndDrag(Entity entity, double dx, double dy, AccelerationComponent acceleration) {
        var velocity = entity.getComponent(VelocityComponent.class);
        double maxSpeed = velocity.getMaxVelocity() * MapConfig.getTileScale();
        double drag = getDrag(entity);
        double dragFactor = Math.pow(1 - drag, PhysicsConfig.fixedDeltaTime);

        if (acceleration == null || acceleration.getAcceleration().getDx() == 0) dx *= dragFactor;
        if (acceleration == null || acceleration.getAcceleration().getDy() == 0) dy *= dragFactor;

        if (Math.abs(dx) < 0.01) dx = 0;
        if (Math.abs(dy) < 0.01) dy = 0;

        double magnitude = Math.sqrt(dx * dx + dy * dy);
        if (magnitude > maxSpeed) {
            double scale = maxSpeed / magnitude;
            dx *= scale;
            dy *= scale;
        }

        return new double[]{dx, dy};
    }

    private double getDrag(Entity entity) {
        var dragComponent = entity.getComponent(DragComponent.class);
        return (dragComponent != null) ? dragComponent.getDrag() : PhysicsConfig.defaultDrag;
    }

    private void updateCentralMass(Entity entity) {
        var position = entity.getComponent(PositionComponent.class);
        var dimension = entity.getComponent(DimensionComponent.class);
        var centralMass = entity.getComponent(CentralMassComponent.class);

        if (dimension != null && centralMass != null) {
            centralMass.setCentralX(position.getGlobalX() + dimension.getWidth() / 2);
            centralMass.setCentralY(position.getGlobalY() + dimension.getHeight() / 2);
        }
    }

    private void updateHitBoxes(Entity entity, VelocityComponent velocity) {
        if (entity.getComponent(HitBoxComponent.class) != null) {
            double dx = velocity.getVelocity().getDx();
            double dy = velocity.getVelocity().getDy();
            entity.getComponent(HitBoxComponent.class).getHitBox().translate(dx, dy);
        }
    }

    private TileEntity getCurrentTile(Entity entity) {
        if (WorldEntity.getInstance() == null) return null;
        var worldData = WorldEntity.getInstance().getComponent(WorldDataComponent.class);
        var position = entity.getComponent(PositionComponent.class);
        var central = entity.getComponent(CentralMassComponent.class);

        return (central != null)
                ? worldData.getElement(central.getCentral())
                : worldData.getElement(position.getGlobal());
    }

    private List<Entity> getEntities() {
        return EntityHub.getInstance().getEntitiesWithComponent(VelocityComponent.class);
    }
}
