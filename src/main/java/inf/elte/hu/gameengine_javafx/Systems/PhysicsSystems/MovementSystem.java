package inf.elte.hu.gameengine_javafx.Systems.PhysicsSystems;

import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.HitBoxComponent;
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

/**
 * The MovementSystem is responsible for updating the movement of entities within the game world.
 * It handles the application of forces like acceleration, friction, drag, and velocity limits,
 * and updates entities' positions and other relevant components such as hitboxes and central mass.
 */
public class MovementSystem extends GameSystem {

    /**
     * Initializes the system by setting the active status to true.
     */
    @Override
    public void start() {
        this.active = true;
    }

    /**
     * Updates the movement of all entities that have a VelocityComponent attached.
     * Applies acceleration, friction, drag, and velocity limits, then updates the entity's position.
     */
    @Override
    public void update() {
        var entitiesSnapshot = getEntities();
        if (entitiesSnapshot.isEmpty()) return;

        for (Entity entity : entitiesSnapshot) {
            if (entity == null) {
                continue;
            }
            processEntity(entity);
        }
    }

    /**
     * Processes the movement of an individual entity by applying acceleration,
     * friction, drag, and velocity limits. Updates the entity's position, central mass,
     * and hitboxes accordingly.
     *
     * @param entity The entity to be processed.
     */
    private void processEntity(Entity entity) {
        if (entity == null) return;

        var velocity = entity.getComponent(VelocityComponent.class);
        var position = entity.getComponent(PositionComponent.class);
        var acceleration = entity.getComponent(AccelerationComponent.class);

        double newDx = velocity.getVelocity().getDx();
        double newDy = velocity.getVelocity().getDy();

        // Apply acceleration
        newDx = applyAccelerationX(acceleration, newDx, entity);
        newDy = applyAccelerationY(acceleration, newDy, entity);

        // Get the current tile to apply friction
        TileEntity tile = getCurrentTile(entity);

        if (!IgnoreFriction.ignore.contains(entity.getClass())) {
            double[] frictionAdjusted = applyFriction(tile, newDx, newDy, getMass(entity));
            newDx = frictionAdjusted[0];
            newDy = frictionAdjusted[1];
        }

        // Apply drag and velocity limits
        double[] velocityAdjusted = applyVelocityLimitsAndDrag(entity, newDx, newDy, acceleration);
        newDx = velocityAdjusted[0];
        newDy = velocityAdjusted[1];

        // Update velocity and position
        velocity.setVelocity(newDx, newDy);
        position.setLocalPosition(position.getLocalX() + newDx, position.getLocalY() + newDy, entity);

        // Update central mass and hitboxes
        updateCentralMass(entity);
        updateHitBoxes(entity, velocity);
    }

    /**
     * Applies acceleration to the entity's velocity in the X direction.
     *
     * @param acceleration The acceleration component of the entity.
     * @param currentDx    The current velocity in the X direction.
     * @param entity       The entity being processed.
     * @return The updated velocity in the X direction.
     */
    private double applyAccelerationX(AccelerationComponent acceleration, double currentDx, Entity entity) {
        return (acceleration != null) ? currentDx + acceleration.getAcceleration().getDx() / getMass(entity) : currentDx;
    }

    /**
     * Applies acceleration to the entity's velocity in the Y direction.
     *
     * @param acceleration The acceleration component of the entity.
     * @param currentDy The current velocity in the Y direction.
     * @param entity The entity being processed.
     * @return The updated velocity in the Y direction.
     */
    private double applyAccelerationY(AccelerationComponent acceleration, double currentDy, Entity entity) {
        return (acceleration != null) ? currentDy + acceleration.getAcceleration().getDy() / getMass(entity) : currentDy;
    }

    /**
     * Returns the mass of the entity. If no mass component is found, it returns the default mass.
     *
     * @param entity The entity to get the mass of.
     * @return The mass of the entity.
     */
    private double getMass(Entity entity) {
        var massComponent = entity.getComponent(MassComponent.class);
        return (massComponent != null) ? massComponent.getMass() : PhysicsConfig.defaultMass;
    }

    /**
     * Applies friction to the entity's velocity based on the current tile's friction coefficient.
     *
     * @param tile The tile the entity is currently on.
     * @param dx The current velocity in the X direction.
     * @param dy The current velocity in the Y direction.
     * @param mass The mass of the entity.
     * @return The velocity adjusted for friction.
     */
    private double[] applyFriction(TileEntity tile, double dx, double dy, double mass) {
        double frictionCoefficient = PhysicsConfig.defaultFriction;
        if (tile != null && tile.getComponent(FrictionComponent.class) != null) {
            frictionCoefficient = tile.getComponent(FrictionComponent.class).getFriction();
        }
        double normalForce = mass;
        double frictionForce = frictionCoefficient * normalForce;
        double frictionAccel = frictionForce / mass * PhysicsConfig.fixedDeltaTime;

        if (Math.abs(dx) > 0) dx -= Math.signum(dx) * Math.min(frictionAccel, Math.abs(dx));
        if (Math.abs(dy) > 0) dy -= Math.signum(dy) * Math.min(frictionAccel, Math.abs(dy));

        return new double[]{dx, dy};
    }

    /**
     * Applies velocity limits and drag to the entity's velocity.
     *
     * @param entity The entity being processed.
     * @param dx The current velocity in the X direction.
     * @param dy The current velocity in the Y direction.
     * @param acceleration The acceleration component of the entity.
     * @return The velocity adjusted for drag and velocity limits.
     */
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

    /**
     * Returns the drag coefficient for the entity. If no drag component is found, it returns the default drag value.
     *
     * @param entity The entity to get the drag for.
     * @return The drag coefficient for the entity.
     */
    private double getDrag(Entity entity) {
        var dragComponent = entity.getComponent(DragComponent.class);
        return (dragComponent != null) ? dragComponent.getDrag() : PhysicsConfig.defaultDrag;
    }

    /**
     * Updates the central mass component of the entity based on its position and dimensions.
     *
     * @param entity The entity whose central mass component is being updated.
     */
    private void updateCentralMass(Entity entity) {
        var position = entity.getComponent(PositionComponent.class);
        var dimension = entity.getComponent(DimensionComponent.class);
        var centralMass = entity.getComponent(CentralMassComponent.class);

        if (dimension != null && centralMass != null) {
            centralMass.setCentralX(position.getGlobalX() + dimension.getWidth() / 2);
            centralMass.setCentralY(position.getGlobalY() + dimension.getHeight() / 2);
        }
    }

    /**
     * Updates the hitboxes of the entity based on its velocity.
     *
     * @param entity The entity whose hitboxes are being updated.
     * @param velocity The velocity component of the entity.
     */
    private void updateHitBoxes(Entity entity, VelocityComponent velocity) {
        if (entity.getComponent(HitBoxComponent.class) != null) {
            double dx = velocity.getVelocity().getDx();
            double dy = velocity.getVelocity().getDy();
            entity.getComponent(HitBoxComponent.class).getHitBox().translate(dx, dy);
        }
    }

    /**
     * Returns the tile the entity is currently standing on based on its position and central mass.
     *
     * @param entity The entity to check the tile for.
     * @return The tile the entity is currently standing on.
     */
    private TileEntity getCurrentTile(Entity entity) {
        if (WorldEntity.getInstance() == null) return null;
        var worldData = WorldEntity.getInstance().getComponent(WorldDataComponent.class);
        var position = entity.getComponent(PositionComponent.class);
        var central = entity.getComponent(CentralMassComponent.class);

        return (central != null)
                ? worldData.getElement(central.getCentral())
                : worldData.getElement(position.getGlobal());
    }

    /**
     * Returns a list of entities that have a VelocityComponent attached.
     *
     * @return A list of entities with a VelocityComponent.
     */
    private List<Entity> getEntities() {
        return EntityHub.getInstance().getEntitiesWithComponent(VelocityComponent.class);
    }
}
