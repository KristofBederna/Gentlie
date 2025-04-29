package Game.Systems;

import Game.Components.HealthComponent;
import Game.Entities.BigSnowBallEntity;
import Game.Entities.ChestEntity;
import Game.Entities.Labels.DamageLabel;
import Game.Entities.PolarBearEntity;
import Game.Entities.SnowBallEntity;
import Game.Misc.CauseOfDeath;
import Game.Misc.EnemyStats;
import Game.Misc.IgnoreCollisions;
import Game.Misc.PlayerStats;
import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.HitBoxComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.AccelerationComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.VelocityComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.CentralMassComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Entities.PlayerEntity;
import inf.elte.hu.gameengine_javafx.Entities.TileEntity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.ComplexShape;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Shape;
import inf.elte.hu.gameengine_javafx.Misc.Configs.MapConfig;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class CustomCollisionSystem extends GameSystem {
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
        ignore.getCollisionRules().computeIfAbsent(PolarBearEntity.class, k -> new ArrayList<>()).add(ChestEntity.class);

        ignore.getCollisionRules().computeIfAbsent(ChestEntity.class, k -> new ArrayList<>()).add(PolarBearEntity.class);
        ignore.getCollisionRules().computeIfAbsent(ChestEntity.class, k -> new ArrayList<>()).add(SnowBallEntity.class);
        ignore.getCollisionRules().computeIfAbsent(ChestEntity.class, k -> new ArrayList<>()).add(BigSnowBallEntity.class);

        ignore.getCollisionRules().computeIfAbsent(BigSnowBallEntity.class, k -> new ArrayList<>()).add(ChestEntity.class);
        ignore.getCollisionRules().computeIfAbsent(SnowBallEntity.class, k -> new ArrayList<>()).add(ChestEntity.class);

        ignore.getCollisionRules().computeIfAbsent(SnowBallEntity.class, k -> new ArrayList<>()).add(SnowBallEntity.class);

        ignore.getCollisionRules().computeIfAbsent(TileEntity.class, k -> new ArrayList<>()).add(PolarBearEntity.class);
    }

    @Override
    public void update() {
        List<Entity> filteredEntities = getEntities();
        List<Entity> hitBoxes = EntityHub.getInstance().getEntitiesWithComponent(HitBoxComponent.class);

        if (filteredEntities == null || filteredEntities.isEmpty()) {
            return;
        }

        processEntities(filteredEntities, hitBoxes);
    }

    private static void processEntities(List<Entity> filteredEntities, List<Entity> hitBoxes) {
        synchronized (filteredEntities) {
            for (Entity entity : filteredEntities) {
                processEntity(hitBoxes, entity);
            }
        }
    }

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
        hitBoxesToProcess.removeIf(hitbox -> hitbox == null || hitbox.getComponent(CentralMassComponent.class).getCentral().distanceTo(entity.getComponent(CentralMassComponent.class).getCentral()) > MapConfig.scaledTileSize + MapConfig.scaledTileSize * 2 || hitbox == entity);

        moveDiagonally(hitBoxesToProcess, entity, futureHitBox, velocity);
    }

    private static List<Entity> getEntities() {
        Set<Entity> hitboxEntities = new HashSet<>(EntityHub.getInstance().getEntitiesWithComponent(HitBoxComponent.class));

        return EntityHub.getInstance()
                .getEntitiesWithComponent(VelocityComponent.class)
                .stream()
                .filter(hitboxEntities::contains)
                .collect(Collectors.toList());
    }

    private static void moveDiagonally(List<Entity> hitBoxes, Entity entity, Shape futureHitBox, VelocityComponent velocity) {
        checkCollisionAndMove(hitBoxes, entity, futureHitBox, velocity);
        if (velocity.getVelocity().getDy() != 0) {
            verticalCollisionCheck(hitBoxes, entity, futureHitBox, velocity);
        }
        if (velocity.getVelocity().getDx() != 0) {
            horizontalCollisionCheck(hitBoxes, entity, futureHitBox, velocity);
        }
    }

    private static void checkCollisionAndMove(List<Entity> hitBoxes, Entity entity, Shape futureHitBox, VelocityComponent velocity) {
        Shape horizontalBox = new ComplexShape(futureHitBox.getPoints());
        Shape verticalBox = new ComplexShape(futureHitBox.getPoints());
        horizontalCollisionCheck(hitBoxes, entity, horizontalBox, velocity);
        verticalCollisionCheck(hitBoxes, entity, verticalBox, velocity);

    }

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

        if (entity instanceof SnowBallEntity || entity instanceof BigSnowBallEntity) {
            entity.getComponent(VelocityComponent.class).stopMovement();
        }
    }

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

    private static void translateHitBoxHorizontally(Entity entity, Shape futureHitBox, VelocityComponent velocity) {
        double dx = velocity.getVelocity().getDx();

        AccelerationComponent accelerationComponent = entity.getComponent(AccelerationComponent.class);
        if (accelerationComponent != null) {
            dx += accelerationComponent.getAcceleration().getDx();
        }
        futureHitBox.translate(dx, 0);
    }

    private static void translateHitBoxVertically(Entity entity, Shape futureHitBox, VelocityComponent velocity) {
        double dy = velocity.getVelocity().getDy();

        AccelerationComponent accelerationComponent = entity.getComponent(AccelerationComponent.class);
        if (accelerationComponent != null) {
            dy += accelerationComponent.getAcceleration().getDy();
        }
        futureHitBox.translate(0, dy);
    }
}
