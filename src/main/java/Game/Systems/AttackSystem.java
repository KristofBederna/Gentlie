package Game.Systems;

import Game.Components.AttackBoxComponent;
import Game.Components.HealthComponent;
import Game.Entities.SnowBallEntity;
import Game.Misc.PlayerStats;
import inf.elte.hu.gameengine_javafx.Components.HitBoxComponents.HitBoxComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Entities.PlayerEntity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Shape;

public class AttackSystem extends GameSystem {
    @Override
    public void start() {
        this.active = true;
    }

    @Override
    protected void update() {
        var attackers = EntityHub.getInstance().getEntitiesWithComponent(AttackBoxComponent.class);
        var entities = EntityHub.getInstance().getEntitiesWithComponent(HealthComponent.class);
        entities.removeIf(e -> e instanceof SnowBallEntity || e.getComponent(HitBoxComponent.class) == null);
        for (var entity : attackers) {
            for (var otherEntity : entities) {
                if (entity.equals(otherEntity)) {
                    continue;
                }
                AttackBoxComponent attackBox = entity.getComponent(AttackBoxComponent.class);
                HitBoxComponent hitBox = otherEntity.getComponent(HitBoxComponent.class);

                if (Shape.intersect(attackBox.getAttackBox(), hitBox.getHitBox())) {
                    if (entity instanceof PlayerEntity) {
                        otherEntity.getComponent(HealthComponent.class).decreaseHealth(PlayerStats.meleeDamage);
                        entity.getComponent(AttackBoxComponent.class).setHasDamaged(true);
                        System.out.println("Damaged for: " + PlayerStats.meleeDamage + " damage, remaining health: " + otherEntity.getComponent(HealthComponent.class).getHealth());
                    }
                }
            }
            if (System.currentTimeMillis() > entity.getComponent(AttackBoxComponent.class).getStartTime() + entity.getComponent(AttackBoxComponent.class).getDuration() || entity.getComponent(AttackBoxComponent.class).hasDamaged()) {
                entity.removeComponentsByType(AttackBoxComponent.class);
            }
        }
    }
}
