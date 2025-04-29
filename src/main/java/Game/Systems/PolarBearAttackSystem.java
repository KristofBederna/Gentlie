package Game.Systems;

import Game.Components.AttackBoxComponent;
import Game.Components.AttackTimeOutComponent;
import Game.Components.HealthComponent;
import Game.Entities.BigSnowBallEntity;
import Game.Entities.Labels.DamageLabel;
import Game.Entities.PolarBearEntity;
import Game.Misc.CauseOfDeath;
import Game.Misc.EnemyStats;
import Game.Misc.PlayerStats;
import inf.elte.hu.gameengine_javafx.Components.HitBoxComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.VelocityComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.CentralMassComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.DimensionComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Entities.PlayerEntity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.ComplexShape;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Rectangle;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Shape;
import inf.elte.hu.gameengine_javafx.Maths.Vector;
import inf.elte.hu.gameengine_javafx.Misc.Configs.MapConfig;
import inf.elte.hu.gameengine_javafx.Misc.Sound.SoundEffect;
import inf.elte.hu.gameengine_javafx.Misc.Sound.SoundEffectStore;
import inf.elte.hu.gameengine_javafx.Misc.Time;

import java.util.Random;

/**
 * System responsible for handling polar bear attacks, including melee and ranged (snowball) attacks.
 */
public class PolarBearAttackSystem extends GameSystem {

    private static final double SNOWBALL_THROW_CHANCE = 0.0002;
    private static final long SNOWBALL_COOLDOWN = 10000;
    private static final double MIN_DISTANCE = 200;
    private static final double MAX_DISTANCE = 500;

    /**
     * Activates the system.
     */
    @Override
    public void start() {
        this.active = true;
    }

    /**
     * Updates the system by processing all polar bears and checking for attack opportunities.
     */
    @Override
    protected void update() {
        var bears = EntityHub.getInstance().getEntitiesWithType(PolarBearEntity.class);
        var player = EntityHub.getInstance().getEntitiesWithType(PlayerEntity.class).getFirst();
        if (player == null) {
            return;
        }

        Random random = new Random();
        for (var bear : bears) {
            if (bear == null) {
                return;
            }
            processBear(bear, player, random);
        }
    }

    /**
     * Processes a single polar bear entity to determine if it should attack the player.
     *
     * @param bear   the polar bear entity
     * @param player the player entity
     * @param random the random generator
     */
    private void processBear(Entity bear, Entity player, Random random) {
        Point bearCentral = bear.getComponent(CentralMassComponent.class).getCentral();
        Point playerCentral = player.getComponent(CentralMassComponent.class).getCentral();

        double distance = bearCentral.distanceTo(playerCentral);
        if (distance <= MAX_DISTANCE) {
            handleAttacking(bear, player, random, distance, playerCentral, bearCentral);
        }
    }

    /**
     * Handles the logic for whether a polar bear attacks with melee or throws a snowball.
     * @param bear the polar bear entity
     * @param player the player entity
     * @param random the random generator
     * @param distance the distance between bear and player
     * @param playerCentral player's central point
     * @param bearCentral bear's central point
     */
    private void handleAttacking(Entity bear, Entity player, Random random, double distance, Point playerCentral, Point bearCentral) {
        if (onCooldown(bear)) return;

        boolean throwSnowball = random.nextDouble() < SNOWBALL_THROW_CHANCE;

        if (throwSnowball && distance >= MIN_DISTANCE) {
            handleSnowballThrowing(bear, playerCentral, bearCentral);
        } else if (distance <= MIN_DISTANCE) {
            handleMeleeAttacking(bear, player, playerCentral, bearCentral);
        }
    }

    /**
     * Handles melee attack logic.
     * @param bear the polar bear entity
     * @param player the player entity
     * @param playerCentral player's central point
     * @param bearCentral bear's central point
     */
    private void handleMeleeAttacking(Entity bear, Entity player, Point playerCentral, Point bearCentral) {
        double dx = playerCentral.getX() - bearCentral.getX();
        double dy = playerCentral.getY() - bearCentral.getY();
        double angle = Math.atan2(-dy, dx);
        double angleDeg = Math.toDegrees(angle);
        if (angleDeg < 0) angleDeg += 360;

        int directionX, directionY;

        if (angleDeg >= 337.5 || angleDeg < 22.5) {
            directionX = 1;
            directionY = 0;
        } else if (angleDeg < 67.5) {
            directionX = 1;
            directionY = -1;
        } else if (angleDeg < 112.5) {
            directionX = 0;
            directionY = -1;
        } else if (angleDeg < 157.5) {
            directionX = -1;
            directionY = -1;
        } else if (angleDeg < 202.5) {
            directionX = -1;
            directionY = 0;
        } else if (angleDeg < 247.5) {
            directionX = -1;
            directionY = 1;
        } else if (angleDeg < 292.5) {
            directionX = 0;
            directionY = 1;
        } else {
            directionX = 1;
            directionY = 1;
        }

        double width = bear.getComponent(DimensionComponent.class).getWidth();
        double height = bear.getComponent(DimensionComponent.class).getHeight();

        ComplexShape attackBox = new ComplexShape(new Rectangle(new Point(bearCentral.getX() - width / 2, bearCentral.getY() - height / 2), width, height).getPoints());

        double offsetX = directionX * (width / 2 + width / 2);
        double offsetY = directionY * (height / 2 + height / 2);
        attackBox.translate(offsetX, offsetY);

        if (bear.getComponent(AttackTimeOutComponent.class) == null) {
            bear.addComponent(new AttackTimeOutComponent(3000));
        }

        if (bear.getComponent(AttackTimeOutComponent.class) != null &&
                (System.currentTimeMillis() - bear.getComponent(AttackTimeOutComponent.class).getStartTime()) > bear.getComponent(AttackTimeOutComponent.class).getDuration()) {

            handleBearAttackingPlayer(bear, player, attackBox);
        }
    }

    /**
     * Checks collision between polar bear's attack box and player, and applies damage if hit.
     * @param bear the polar bear entity
     * @param player the player entity
     * @param attackBox the shape of the attack box
     */
    private void handleBearAttackingPlayer(Entity bear, Entity player, ComplexShape attackBox) {
        if (bear.getComponent(AttackBoxComponent.class) == null) {
            generateAttackBox(bear, attackBox);
        }

        if (Shape.intersect(bear.getComponent(AttackBoxComponent.class).getAttackBox(), player.getComponent(HitBoxComponent.class).getHitBox())) {
            handleDamage(bear, player);
        }
    }

    /**
     * Generates an attack box for the polar bear and stops its movement.
     * @param bear the polar bear entity
     * @param attackBox the shape of the attack box
     */
    private void generateAttackBox(Entity bear, ComplexShape attackBox) {
        bear.addComponent(new AttackBoxComponent(attackBox.getPoints(), 100));
        SoundEffectStore.getInstance().add(new SoundEffect(bear, "/assets/sound/sfx/roar.wav", "roar_" + bear.getId(), 0.4f, 0.0f, 1000, false));
        bear.getComponent(VelocityComponent.class).stopMovement();
    }

    /**
     * Applies damage to the player and shows a floating damage label.
     * @param bear the polar bear entity
     * @param player the player entity
     */
    private void handleDamage(Entity bear, Entity player) {
        player.getComponent(HealthComponent.class).decreaseHealth(EnemyStats.meleeDamage * (1 - PlayerStats.meleeResistance), CauseOfDeath.MELEE);
        PlayerStats.health = player.getComponent(HealthComponent.class).getHealth();

        CentralMassComponent pos = player.getComponent(CentralMassComponent.class);
        new DamageLabel(String.format("%.1f", EnemyStats.meleeDamage * (1 - PlayerStats.meleeResistance)), pos.getCentralX(), pos.getCentralY(), 100, 0);

        bear.removeComponentsByType(AttackBoxComponent.class);
        bear.addComponent(new AttackTimeOutComponent(3000));
    }

    /**
     * Handles the snowball-throwing action from the polar bear.
     * @param bear the polar bear entity
     * @param playerCentral the player's central position
     * @param bearCentral the bear's central position
     */
    private void handleSnowballThrowing(Entity bear, Point playerCentral, Point bearCentral) {
        double dx = playerCentral.getX() - bearCentral.getX();
        double dy = playerCentral.getY() - bearCentral.getY();
        double length = Math.sqrt(dx * dx + dy * dy);
        double speed = 1500 * Time.getInstance().getDeltaTime();
        Vector throwDirection = new Vector((dx / length) * speed, (dy / length) * speed);

        new BigSnowBallEntity(bearCentral.getX(), bearCentral.getY(), MapConfig.scaledTileSize / 2, MapConfig.scaledTileSize / 2, throwDirection);

        bear.addComponent(new AttackTimeOutComponent(SNOWBALL_COOLDOWN));
        bear.getComponent(VelocityComponent.class).stopMovement();
    }

    /**
     * Checks whether the bear is currently on attack cooldown.
     * @param bear the polar bear entity
     * @return true if on cooldown, false otherwise
     */
    private boolean onCooldown(Entity bear) {
        AttackTimeOutComponent attackCooldown = bear.getComponent(AttackTimeOutComponent.class);
        if (attackCooldown != null && (System.currentTimeMillis() - attackCooldown.getStartTime()) < attackCooldown.getDuration()) {
            return true;
        }
        return false;
    }
}
