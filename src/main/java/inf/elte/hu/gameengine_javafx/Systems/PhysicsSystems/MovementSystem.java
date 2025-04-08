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
import inf.elte.hu.gameengine_javafx.Entities.ParticleEntity;
import inf.elte.hu.gameengine_javafx.Entities.TileEntity;
import inf.elte.hu.gameengine_javafx.Entities.WorldEntity;
import inf.elte.hu.gameengine_javafx.Misc.Config;
import inf.elte.hu.gameengine_javafx.Misc.Time;

import java.util.List;

/**
 * This class is responsible for updating the movement of entities in the game world. It calculates the entity's
 * new position based on velocity, acceleration, mass, drag, and friction components.
 * The system also updates the entity's hitbox according to its movement.
 *
 * The movement system works with various physics components such as:
 * - Velocity
 * - Acceleration
 * - Mass
 * - Drag
 * - Friction
 *
 * The system ensures entities respect a maximum velocity, apply friction forces, and simulate drag over time.
 * Additionally, it updates the central mass for entities that have a {@link CentralMassComponent}.
 */
public class MovementSystem extends GameSystem {

    /**
     * Starts the movement system by activating it.
     */
    @Override
    public void start() {
        this.active = true;
    }

    /**
     * Updates the movement for all entities with the required components (Position, Velocity, Acceleration).
     * This method applies physics updates to the entities based on their components and updates their position
     * accordingly.
     */
    @Override
    public void update() {
        var entitiesSnapshot = getEntities();

        if (entitiesSnapshot.isEmpty()) {
            return;
        }

        for (Entity entity : entitiesSnapshot) {
            processEntity(entity);
        }
    }

    /**
     * Processes each entity to update its position and velocity based on its components.
     * This method applies acceleration, mass, friction, drag, and velocity limits to compute the new velocity
     * and position of the entity.
     *
     * @param entity The entity to process.
     */
    private void processEntity(Entity entity) {
        if (entity == null) return;

        var velocity = entity.getComponent(VelocityComponent.class);
        var position = entity.getComponent(PositionComponent.class);
        var acceleration = entity.getComponent(AccelerationComponent.class);
        var massComponent = entity.getComponent(MassComponent.class);
        var dragComponent = entity.getComponent(DragComponent.class);

        double mass = (massComponent != null) ? massComponent.getMass() : 1.0;
        double drag = (dragComponent != null) ? dragComponent.getDrag() : Config.drag;
        double dragFactor = Math.pow(1 - drag, Time.getInstance().getDeltaTime());
        double maxSpeed = velocity.getMaxVelocity()*Config.getTileScale();

        double newDx = velocity.getVelocity().getDx();
        double newDy = velocity.getVelocity().getDy();

        if (acceleration != null) {
            newDx += acceleration.getAcceleration().getDx() / mass;
            newDy += acceleration.getAcceleration().getDy() / mass;
        }

        TileEntity tile = getCurrentTile(entity);

        if (!(entity instanceof ParticleEntity)) {
            if (tile == null) {
                double friction = Config.friction;
                double frictionForce = friction * Time.getInstance().getDeltaTime();

                if (Math.abs(newDx) > frictionForce) {
                    newDx -= Math.signum(newDx) * frictionForce;
                } else {
                    newDx = 0;
                }

                if (Math.abs(newDy) > frictionForce) {
                    newDy -= Math.signum(newDy) * frictionForce;
                } else {
                    newDy = 0;
                }
            }
             else if (tile.getComponent(FrictionComponent.class) != null) {
                FrictionComponent frictionComponent = tile.getComponent(FrictionComponent.class);
                double friction = (frictionComponent != null) ? frictionComponent.getFriction() : Config.friction;
                double frictionForce = friction * Time.getInstance().getDeltaTime();

                if (Math.abs(newDx) > frictionForce) {
                    newDx -= Math.signum(newDx) * frictionForce;
                } else {
                    newDx = 0;
                }

                if (Math.abs(newDy) > frictionForce) {
                    newDy -= Math.signum(newDy) * frictionForce;
                } else {
                    newDy = 0;
                }
            }
        }

        newDx = Math.max(-maxSpeed, Math.min(maxSpeed, newDx));
        newDy = Math.max(-maxSpeed, Math.min(maxSpeed, newDy));

        if (acceleration == null || (acceleration.getAcceleration().getDx() == 0)) {
            newDx *= dragFactor;
        }
        if (acceleration == null || (acceleration.getAcceleration().getDy() == 0)) {
            newDy *= dragFactor;
        }

        double deadZone = 0.01;
        if (Math.abs(newDx) < deadZone) newDx = 0;
        if (Math.abs(newDy) < deadZone) newDy = 0;

        double magnitude = Math.sqrt(newDx * newDx + newDy * newDy);
        if (magnitude > maxSpeed) {
            double scale = maxSpeed / magnitude;
            newDx *= scale;
            newDy *= scale;
        }

        velocity.setVelocity(newDx, newDy);
        position.setLocalPosition(
                position.getLocalX() + newDx,
                position.getLocalY() + newDy,
                entity
        );

        var dimension = entity.getComponent(DimensionComponent.class);
        var centralMass = entity.getComponent(CentralMassComponent.class);
        if (dimension != null && centralMass != null) {
            centralMass.setCentralX(position.getGlobalX() + dimension.getWidth() / 2);
            centralMass.setCentralY(position.getGlobalY() + dimension.getHeight() / 2);
        }
        position.updateGlobalPosition(entity);
        updateHitBoxes(entity, velocity);
    }

    /**
     * Gets the tile the entity is currently on.
     * This method checks both the position and central mass components to return the correct tile.
     *
     * @param entity The entity for which the tile is to be retrieved.
     * @return The {@link TileEntity} that the entity is currently on.
     */
    private static TileEntity getCurrentTile(Entity entity) {
        TileEntity tile = WorldEntity.getInstance().getComponent(WorldDataComponent.class).getElement(entity.getComponent(PositionComponent.class).getGlobal());

        if (entity.getComponent(CentralMassComponent.class) != null) {
            tile = WorldEntity.getInstance().getComponent(WorldDataComponent.class).getElement(entity.getComponent(CentralMassComponent.class).getCentral());
        }
        return tile;
    }

    /**
     * Retrieves a snapshot of all entities that are inside the camera's viewport and have the required components.
     *
     * @return A list of entities that have a PositionComponent, VelocityComponent, and are inside the viewport.
     */
    private static List<Entity> getEntities() {
        return EntityHub.getInstance().getEntitiesWithComponent(VelocityComponent.class);
    }

    /**
     * Updates the hitboxes of the entities by translating them based on their current velocity.
     *
     * @param entity The entity whose hitbox needs to be updated.
     * @param velocity The velocity component of the entity.
     */
    private void updateHitBoxes(Entity entity, VelocityComponent velocity) {
        double dx = velocity.getVelocity().getDx();
        double dy = velocity.getVelocity().getDy();

        if (entity.getComponent(HitBoxComponent.class) != null) {
            entity.getComponent(HitBoxComponent.class).getHitBox().translate(dx, dy);
        }
    }
}
