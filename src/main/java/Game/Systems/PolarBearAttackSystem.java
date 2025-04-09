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
import inf.elte.hu.gameengine_javafx.Components.HitBoxComponents.HitBoxComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.VelocityComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.CentralMassComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.DimensionComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Entities.PlayerEntity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.ComplexShape;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Rectangle;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Shape;
import inf.elte.hu.gameengine_javafx.Maths.Vector;
import inf.elte.hu.gameengine_javafx.Misc.Config;
import inf.elte.hu.gameengine_javafx.Misc.Time;

import java.util.Random;

public class PolarBearAttackSystem extends GameSystem {

    private static final double SNOWBALL_THROW_CHANCE = 0.0002;  // 0.02% chance to throw a snowball
    private static final long SNOWBALL_COOLDOWN = 10000;  // 10 seconds cooldown after throwing a snowball
    private static final double MIN_DISTANCE = 200;  // Minimum distance to throw a snowball
    private static final double MAX_DISTANCE = 500;  // Maximum distance to throw a snowball

    @Override
    public void start() {
        this.active = true;
    }

    @Override
    protected void update() {
        var bears = EntityHub.getInstance().getEntitiesWithType(PolarBearEntity.class);
        var player = EntityHub.getInstance().getEntitiesWithType(PlayerEntity.class).getFirst();

        Random random = new Random();
        for (var bear : bears) {
            Point bearCentral = bear.getComponent(CentralMassComponent.class).getCentral();
            Point playerCentral = player.getComponent(CentralMassComponent.class).getCentral();

            double distance = bearCentral.distanceTo(playerCentral);
            if (distance <= MAX_DISTANCE) {
                AttackTimeOutComponent attackCooldown = bear.getComponent(AttackTimeOutComponent.class);
                if (attackCooldown != null && (System.currentTimeMillis() - attackCooldown.getStartTime()) < attackCooldown.getDuration()) {
                    continue;
                }

                boolean throwSnowball = random.nextDouble() < SNOWBALL_THROW_CHANCE;

                if (throwSnowball && distance >= MIN_DISTANCE) {
                    double dx = playerCentral.getX() - bearCentral.getX();
                    double dy = playerCentral.getY() - bearCentral.getY();
                    double length = Math.sqrt(dx * dx + dy * dy);
                    double speed = 1500 * Time.getInstance().getDeltaTime();
                    Vector throwDirection = new Vector((dx / length) * speed, (dy / length) * speed);

                    new BigSnowBallEntity(bearCentral.getX(), bearCentral.getY(), Config.scaledTileSize / 2, Config.scaledTileSize / 2, throwDirection);

                    bear.addComponent(new AttackTimeOutComponent(SNOWBALL_COOLDOWN));
                    bear.getComponent(VelocityComponent.class).stopMovement();
                } else if (distance <= MIN_DISTANCE) {
                    double dx = playerCentral.getX() - bearCentral.getX();
                    double dy = playerCentral.getY() - bearCentral.getY();
                    double angle = Math.atan2(-dy, dx);
                    double angleDeg = Math.toDegrees(angle);
                    if (angleDeg < 0) angleDeg += 360;

                    int directionX, directionY;

                    if (angleDeg >= 337.5 || angleDeg < 22.5) {
                        directionX = 1;
                        directionY = 0;   // E
                    } else if (angleDeg < 67.5) {
                        directionX = 1;
                        directionY = -1;  // NE
                    } else if (angleDeg < 112.5) {
                        directionX = 0;
                        directionY = -1;  // N
                    } else if (angleDeg < 157.5) {
                        directionX = -1;
                        directionY = -1; // NW
                    } else if (angleDeg < 202.5) {
                        directionX = -1;
                        directionY = 0;  // W
                    } else if (angleDeg < 247.5) {
                        directionX = -1;
                        directionY = 1;  // SW
                    } else if (angleDeg < 292.5) {
                        directionX = 0;
                        directionY = 1;   // S
                    } else {
                        directionX = 1;
                        directionY = 1;   // SE
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

                        if (bear.getComponent(AttackBoxComponent.class) == null) {
                            bear.addComponent(new AttackBoxComponent(attackBox.getPoints(), 100));
                            bear.getComponent(VelocityComponent.class).stopMovement();
                        }

                        if (Shape.intersect(bear.getComponent(AttackBoxComponent.class).getAttackBox(), player.getComponent(HitBoxComponent.class).getHitBox())) {
                            player.getComponent(HealthComponent.class).decreaseHealth(EnemyStats.meleeDamage * (1 - EnemyStats.meleeResistance), CauseOfDeath.MELEE);
                            PlayerStats.health = player.getComponent(HealthComponent.class).getHealth();

                            CentralMassComponent pos = player.getComponent(CentralMassComponent.class);
                            new DamageLabel(String.format("%.1f", EnemyStats.meleeDamage * (1 - EnemyStats.meleeResistance)), pos.getCentralX(), pos.getCentralY(), 100, 0);

                            bear.removeComponentsByType(AttackBoxComponent.class);
                            bear.addComponent(new AttackTimeOutComponent(4000));
                        }
                    }
                }
            }
        }
    }
}