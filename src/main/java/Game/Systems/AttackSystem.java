package Game.Systems;

import Game.Components.AttackBoxComponent;
import Game.Components.HealthComponent;
import Game.Entities.Labels.DamageLabel;
import Game.Entities.SnowBallEntity;
import Game.Misc.CauseOfDeath;
import Game.Misc.EnemyStats;
import Game.Misc.PlayerStats;
import inf.elte.hu.gameengine_javafx.Components.HitBoxComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.CentralMassComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Entities.PlayerEntity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Shape;

import java.util.List;

/**
 * AttackSystem is responsible for handling melee attack interactions in the game.
 * It checks for collisions between entities with AttackBoxComponent and others with HealthComponent,
 * applies damage, displays damage labels, and removes attack boxes after use or expiration.
 */
public class AttackSystem extends GameSystem {

    /**
     * Activates the system when the game starts.
     */
    @Override
    public void start() {
        this.active = true;
    }

    /**
     * Called every frame to update the attack logic.
     * It checks for valid attackers, processes possible collisions, and handles damage application.
     */
    @Override
    protected void update() {
        var attackers = EntityHub.getInstance().getEntitiesWithComponent(AttackBoxComponent.class);
        var entities = EntityHub.getInstance().getEntitiesWithComponent(HealthComponent.class);
        entities.removeIf(e -> {
            if (e == null) return false;
            return e instanceof SnowBallEntity || e.getComponent(HitBoxComponent.class) == null;
        });
        for (var entity : attackers) {
            processEntity(entity, entities);
        }
    }

    /**
     * Processes a single attacker entity by checking collisions with all potential targets.
     *
     * @param entity   the attacking entity
     * @param entities list of all entities with health that could be attacked
     */
    private void processEntity(Entity entity, List<Entity> entities) {
        if (entity == null) return;
        for (var otherEntity : entities) {
            if (otherEntity == null || entity.equals(otherEntity)) continue;
            lookForCollision(entity, otherEntity);
        }
        removeAttackBoxIfExpiredOrUsed(entity);
    }

    /**
     * Removes the attack box if it has either expired or already caused damage.
     *
     * @param entity the entity holding the attack box
     */
    private void removeAttackBoxIfExpiredOrUsed(Entity entity) {
        AttackBoxComponent attackBox = entity.getComponent(AttackBoxComponent.class);
        if (System.currentTimeMillis() > attackBox.getStartTime() + attackBox.getDuration() || attackBox.hasDamaged()) {
            entity.removeComponentsByType(AttackBoxComponent.class);
        }
    }

    /**
     * Checks if the attack box of an entity collides with another entity's hit box.
     * If so, handles the collision.
     *
     * @param entity      the attacking entity
     * @param otherEntity the target entity
     */
    private void lookForCollision(Entity entity, Entity otherEntity) {
        AttackBoxComponent attackBox = entity.getComponent(AttackBoxComponent.class);
        HitBoxComponent hitBox = otherEntity.getComponent(HitBoxComponent.class);

        if (Shape.intersect(attackBox.getAttackBox(), hitBox.getHitBox())) {
            handleCollision(entity, otherEntity);
        }
    }

    /**
     * Applies damage to the target entity and creates a floating damage label.
     * Currently, only PlayerEntity attackers are supported.
     *
     * @param entity      the attacking entity (must be PlayerEntity)
     * @param otherEntity the target entity that receives damage
     */
    private void handleCollision(Entity entity, Entity otherEntity) {
        if (entity instanceof PlayerEntity) {
            double effectiveDamage = PlayerStats.meleeDamage * (1 - EnemyStats.meleeResistance);
            effectiveDamage = Math.max(effectiveDamage, 0);

            HealthComponent health = otherEntity.getComponent(HealthComponent.class);
            health.decreaseHealth(effectiveDamage, CauseOfDeath.MELEE);

            entity.getComponent(AttackBoxComponent.class).setHasDamaged(true);

            CentralMassComponent pos = otherEntity.getComponent(CentralMassComponent.class);
            new DamageLabel(String.format("%.1f", effectiveDamage), pos.getCentralX(), pos.getCentralY(), 100, 0);
        }
    }
}
