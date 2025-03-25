package inf.elte.hu.gameengine_javafx.Systems.PhysicsSystems;

import inf.elte.hu.gameengine_javafx.Components.HitBoxComponents.HitBoxComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.AccelerationComponent;
import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.VelocityComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.CentralMassComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Entities.CameraEntity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.ComplexShape;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Shape;
import inf.elte.hu.gameengine_javafx.Misc.Config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The {@code CollisionSystem} class is responsible for detecting and resolving collisions
 * between entities in the game world. It uses hitboxes and velocity data to check if entities
 * collide with one another, and adjusts their positions and velocities accordingly.
 */
public class CollisionSystem extends GameSystem {

    /**
     * Initializes the system, setting it as active.
     */
    @Override
    public void start() {
        this.active = true;
    }

    /**
     * Updates the collision system, checking for collisions and moving entities
     * based on their velocity and acceleration.
     */
    @Override
    public void update() {
        List<Entity> filteredEntities = getEntities();
        List<Entity> hitBoxes = EntityHub.getInstance().getEntitiesWithComponent(HitBoxComponent.class);

        if (filteredEntities == null || filteredEntities.isEmpty()) {
            return;
        }

        processEntities(filteredEntities, hitBoxes);
    }

    /**
     * Processes each entity by checking for collisions and adjusting their movement.
     *
     * @param filteredEntities list of entities to be processed
     * @param hitBoxes list of entities with hitboxes
     */
    private static void processEntities(List<Entity> filteredEntities, List<Entity> hitBoxes) {
        synchronized (filteredEntities) {
            for (Entity entity : filteredEntities) {
                processEntity(hitBoxes, entity);
            }
        }
    }

    /**
     * Processes a single entity to check for collisions and adjust its position and velocity.
     *
     * @param hitBoxes list of entities with hitboxes
     * @param entity the entity to be processed
     */
    private static void processEntity(List<Entity> hitBoxes, Entity entity) {
        HitBoxComponent hitBox = entity.getComponent(HitBoxComponent.class);
        VelocityComponent velocity = entity.getComponent(VelocityComponent.class);
        PositionComponent position = entity.getComponent(PositionComponent.class);

        ComplexShape futureHitBox = null;
        if (hitBox != null) {
            futureHitBox = new ComplexShape(hitBox.getHitBox());
            futureHitBox.moveTo(new Point(position.getGlobalX(), position.getGlobalY()));
        }

        List<Entity> hitBoxesToProcess = new ArrayList<>(hitBoxes);
        hitBoxesToProcess.removeIf(hitbox -> hitbox == null || hitbox.getComponent(CentralMassComponent.class).getCentral().distanceTo(entity.getComponent(CentralMassComponent.class).getCentral()) > Config.tileSize + Config.tileSize * 2 || hitbox == entity);

        moveDiagonally(hitBoxesToProcess, entity, futureHitBox, velocity);
    }

    /**
     * Retrieves a list of entities that are within the camera's viewport and have the necessary components.
     *
     * @return a list of filtered entities that include hitboxes, velocity, and position components
     */
    private static List<Entity> getEntities() {
        Set<Entity> hitboxEntities = new HashSet<>(EntityHub.getInstance().getEntitiesWithComponent(HitBoxComponent.class));

        return EntityHub.getInstance()
                .getEntitiesWithComponent(VelocityComponent.class)
                .stream()
                .filter(hitboxEntities::contains)
                .collect(Collectors.toList());
    }


    /**
     * Moves the entity diagonally and checks for collisions in both horizontal and vertical directions.
     *
     * @param hitBoxes list of hit boxes to check for collisions
     * @param entity the entity to move and check for collisions
     * @param futureHitBox the future position of the entity's hitbox
     * @param velocity the velocity component of the entity
     */
    private static void moveDiagonally(List<Entity> hitBoxes, Entity entity, Shape futureHitBox, VelocityComponent velocity) {
        checkCollisionAndMove(hitBoxes, entity, futureHitBox, velocity);
        if (velocity.getVelocity().getDy() != 0) {
            verticalCollisionCheck(hitBoxes, entity, futureHitBox, velocity);
        }
        if (velocity.getVelocity().getDx() != 0) {
            horizontalCollisionCheck(hitBoxes, entity, futureHitBox, velocity);
        }
    }

    /**
     * Checks for collisions and moves the entity based on the result.
     *
     * @param hitBoxes list of hit boxes to check for collisions
     * @param entity the entity to move and check for collisions
     * @param futureHitBox the future position of the entity's hitbox
     * @param velocity the velocity component of the entity
     */
    private static void checkCollisionAndMove(List<Entity> hitBoxes, Entity entity, Shape futureHitBox, VelocityComponent velocity) {
        horizontalCollisionCheck(hitBoxes, entity, futureHitBox, velocity);
        verticalCollisionCheck(hitBoxes, entity, futureHitBox, velocity);
    }

    /**
     * Checks for horizontal collisions and updates the entity's velocity if necessary.
     *
     * @param hitBoxes list of hit boxes to check for collisions
     * @param entity the entity to check for collisions
     * @param futureHitBox the future position of the entity's hitbox
     * @param velocity the velocity component of the entity
     */
    private static void horizontalCollisionCheck(List<Entity> hitBoxes, Entity entity, Shape futureHitBox, VelocityComponent velocity) {
        translateHitBoxHorizontally(entity, futureHitBox, velocity);
        synchronized (hitBoxes) {
            for (Entity otherEntity : hitBoxes) {
                if (otherEntity == entity) continue;

                Shape otherHitBox = otherEntity.getComponent(HitBoxComponent.class).getHitBox();
                if (otherHitBox != null && Shape.intersect(futureHitBox, otherHitBox)) {
                    velocity.setVelocity(0, velocity.getVelocity().getDy());
                    if (entity.getComponent(AccelerationComponent.class) != null) {
                        entity.getComponent(AccelerationComponent.class).getAcceleration().setDx(0);
                    }
                    break;
                }
            }
        }
    }

    /**
     * Checks for vertical collisions and updates the entity's velocity if necessary.
     *
     * @param hitBoxes list of hit boxes to check for collisions
     * @param entity the entity to check for collisions
     * @param futureHitBox the future position of the entity's hitbox
     * @param velocity the velocity component of the entity
     */
    private static void verticalCollisionCheck(List<Entity> hitBoxes, Entity entity, Shape futureHitBox, VelocityComponent velocity) {
        translateHitBoxVertically(entity, futureHitBox, velocity);

        synchronized (hitBoxes) {
            for (Entity otherEntity : hitBoxes) {
                if (otherEntity == entity) continue;

                Shape otherHitBox = otherEntity.getComponent(HitBoxComponent.class).getHitBox();
                if (otherHitBox != null && Shape.intersect(futureHitBox, otherHitBox)) {
                    velocity.setVelocity(velocity.getVelocity().getDx(), 0);
                    if (entity.getComponent(AccelerationComponent.class) != null) {
                        entity.getComponent(AccelerationComponent.class).getAcceleration().setDy(0);
                    }
                    break;
                }
            }
        }
    }

    /**
     * Translates the entity's hitbox horizontally based on its velocity and acceleration.
     *
     * @param entity the entity whose hitbox is to be translated
     * @param futureHitBox the future position of the entity's hitbox
     * @param velocity the velocity component of the entity
     */
    private static void translateHitBoxHorizontally(Entity entity, Shape futureHitBox, VelocityComponent velocity) {
        double dx = velocity.getVelocity().getDx();

        AccelerationComponent accelerationComponent = entity.getComponent(AccelerationComponent.class);
        if (accelerationComponent != null) {
            dx += accelerationComponent.getAcceleration().getDx();
        }
        futureHitBox.translate(dx, 0);
    }

    /**
     * Translates the entity's hitbox vertically based on its velocity and acceleration.
     *
     * @param entity the entity whose hitbox is to be translated
     * @param futureHitBox the future position of the entity's hitbox
     * @param velocity the velocity component of the entity
     */
    private static void translateHitBoxVertically(Entity entity, Shape futureHitBox, VelocityComponent velocity) {
        double dy = velocity.getVelocity().getDy();

        AccelerationComponent accelerationComponent = entity.getComponent(AccelerationComponent.class);
        if (accelerationComponent != null) {
            dy += accelerationComponent.getAcceleration().getDy();
        }
        futureHitBox.translate(0, dy);
    }
}
