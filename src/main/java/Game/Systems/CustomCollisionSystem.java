package Game.Systems;

import Game.Components.HealthComponent;
import Game.Entities.BigSnowBallEntity;
import Game.Entities.Labels.DamageLabel;
import Game.Entities.PolarBearEntity;
import Game.Entities.SnowBallEntity;
import Game.Misc.CauseOfDeath;
import Game.Misc.EnemyStats;
import Game.Misc.IgnoreCollisions;
import Game.Misc.PlayerStats;
import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.HitBoxComponents.HitBoxComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.AccelerationComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.VelocityComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.CentralMassComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Entities.ParticleEmitterEntity;
import inf.elte.hu.gameengine_javafx.Entities.ParticleEntity;
import inf.elte.hu.gameengine_javafx.Entities.PlayerEntity;
import inf.elte.hu.gameengine_javafx.Entities.TileEntity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.ComplexShape;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.NSidedShape;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Shape;
import inf.elte.hu.gameengine_javafx.Misc.Config;
import inf.elte.hu.gameengine_javafx.Misc.Direction;
import javafx.scene.paint.Color;

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
public class CustomCollisionSystem extends GameSystem {

    /**
     * Initializes the system, setting it as active.
     */
    @Override
    public void start() {
        this.active = true;
        IgnoreCollisions ignore = IgnoreCollisions.getInstance();
        ignore.getCollisionRules().computeIfAbsent(PlayerEntity.class, k -> new ArrayList<>()).add(SnowBallEntity.class);
        ignore.getCollisionRules().computeIfAbsent(PlayerEntity.class, k -> new ArrayList<>()).add(PolarBearEntity.class);

        ignore.getCollisionRules().computeIfAbsent(PolarBearEntity.class, k -> new ArrayList<>()).add(PolarBearEntity.class);
        ignore.getCollisionRules().computeIfAbsent(PolarBearEntity.class, k -> new ArrayList<>()).add(PlayerEntity.class);
        ignore.getCollisionRules().computeIfAbsent(PolarBearEntity.class, k -> new ArrayList<>()).add(TileEntity.class);
        ignore.getCollisionRules().computeIfAbsent(PolarBearEntity.class, k -> new ArrayList<>()).add(BigSnowBallEntity.class);

        ignore.getCollisionRules().computeIfAbsent(SnowBallEntity.class, k -> new ArrayList<>()).add(SnowBallEntity.class);

        ignore.getCollisionRules().computeIfAbsent(TileEntity.class, k -> new ArrayList<>()).add(PolarBearEntity.class);
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
        if (entity == null) {
            return;
        }
        HitBoxComponent hitBox = entity.getComponent(HitBoxComponent.class);
        VelocityComponent velocity = entity.getComponent(VelocityComponent.class);
        PositionComponent position = entity.getComponent(PositionComponent.class);

        ComplexShape futureHitBox = null;
        if (hitBox != null) {
            futureHitBox = new ComplexShape(hitBox.getHitBox());
            futureHitBox.moveTo(new Point(position.getGlobalX(), position.getGlobalY()));
        }

        List<Entity> hitBoxesToProcess = new ArrayList<>(hitBoxes);
        hitBoxesToProcess.removeIf(hitbox -> hitbox == null || hitbox.getComponent(CentralMassComponent.class).getCentral().distanceTo(entity.getComponent(CentralMassComponent.class).getCentral()) > Config.scaledTileSize + Config.scaledTileSize * 2 || hitbox == entity);

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
        Shape horizontalBox = new ComplexShape(futureHitBox.getPoints());
        Shape verticalBox = new ComplexShape(futureHitBox.getPoints());
        horizontalCollisionCheck(hitBoxes, entity, horizontalBox, velocity);
        verticalCollisionCheck(hitBoxes, entity, verticalBox, velocity);

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
                if (otherHitBox != null
                        && Shape.intersect(futureHitBox, otherHitBox)
                        && !IgnoreCollisions.shouldIgnoreCollision(entity, otherEntity)) {
                    velocity.setVelocity(0, velocity.getVelocity().getDy());
                    if (entity.getComponent(AccelerationComponent.class) != null) {
                        entity.getComponent(AccelerationComponent.class).getAcceleration().setDx(0);
                    }
                    handleRangedCombat(entity, otherEntity);
                    break;
                }
            }
        }
    }

    private static void handleRangedCombat(Entity entity, Entity otherEntity) {
        if ((entity instanceof SnowBallEntity && otherEntity instanceof PolarBearEntity) ||
                (otherEntity instanceof SnowBallEntity && entity instanceof PolarBearEntity)) {

            Entity polarBear = (entity instanceof PolarBearEntity) ? entity : otherEntity;
            Entity snowball = (entity instanceof SnowBallEntity) ? entity : otherEntity;

            if (snowball.getComponent(HealthComponent.class).getHealth() > 0) {
                snowball.getComponent(HealthComponent.class).setHealth(0, CauseOfDeath.DECAY);

                double effectiveDamage = PlayerStats.rangedDamage * (1 - EnemyStats.rangedResistance);
                effectiveDamage = Math.max(effectiveDamage, 0);

                polarBear.getComponent(HealthComponent.class).decreaseHealth(effectiveDamage, CauseOfDeath.RANGED);

                CentralMassComponent pos = polarBear.getComponent(CentralMassComponent.class);
                new DamageLabel(String.format("%.1f", effectiveDamage), pos.getCentralX(), pos.getCentralY(), 100, 0);
            }
        }

        if ((entity instanceof BigSnowBallEntity && otherEntity instanceof PlayerEntity) ||
                (otherEntity instanceof BigSnowBallEntity && entity instanceof PlayerEntity)) {

            Entity player = (entity instanceof PlayerEntity) ? entity : otherEntity;
            Entity snowball = (entity instanceof BigSnowBallEntity) ? entity : otherEntity;

            if (snowball.getComponent(HealthComponent.class).getHealth() > 0) {
                snowball.getComponent(HealthComponent.class).setHealth(0, CauseOfDeath.DECAY);

                double effectiveDamage = EnemyStats.rangedDamage * (1 - PlayerStats.rangedResistance);
                effectiveDamage = Math.max(effectiveDamage, 0);

                player.getComponent(HealthComponent.class).decreaseHealth(effectiveDamage, CauseOfDeath.RANGED);
                PlayerStats.health -= effectiveDamage;

                CentralMassComponent pos = player.getComponent(CentralMassComponent.class);
                new DamageLabel(String.format("%.1f", effectiveDamage), pos.getCentralX(), pos.getCentralY(), 100, 0);
            }
        }

        if (entity instanceof SnowBallEntity) {
            entity.getComponent(VelocityComponent.class).stopMovement();
            CentralMassComponent pos = entity.getComponent(CentralMassComponent.class);
            new ParticleEmitterEntity(
                    pos.getCentralX(), pos.getCentralY(),
                    new ParticleEntity(pos.getCentralX(), pos.getCentralY(), 10, 10,
                            new NSidedShape(new Point(pos.getCentralX(), pos.getCentralY()), 5, 32),
                            Color.SNOW, Color.GREY, 50),
                    Direction.ALL, 3
            );
        }
        if (entity instanceof BigSnowBallEntity) {
            entity.getComponent(VelocityComponent.class).stopMovement();
            CentralMassComponent pos = entity.getComponent(CentralMassComponent.class);
            new ParticleEmitterEntity(
                    pos.getCentralX(), pos.getCentralY(),
                    new ParticleEntity(pos.getCentralX(), pos.getCentralY(), 15, 15,
                            new NSidedShape(new Point(pos.getCentralX(), pos.getCentralY()), 5, 32),
                            Color.SNOW, Color.GREY, 75),
                    Direction.ALL, 5
            );
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
                if (otherHitBox != null
                        && Shape.intersect(futureHitBox, otherHitBox)
                        && !IgnoreCollisions.shouldIgnoreCollision(entity, otherEntity)) {
                    velocity.setVelocity(velocity.getVelocity().getDx(), 0);
                    if (entity.getComponent(AccelerationComponent.class) != null) {
                        entity.getComponent(AccelerationComponent.class).getAcceleration().setDy(0);
                    }
                    handleRangedCombat(entity, otherEntity);
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
