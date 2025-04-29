package Game.Systems;

import Game.Components.HealthComponent;
import Game.Entities.BigSnowBallEntity;
import Game.Entities.ChestEntity;
import Game.Entities.Labels.GoldGainedLabel;
import Game.Entities.PolarBearEntity;
import Game.Entities.SnowBallEntity;
import Game.Misc.PlayerStats;
import Game.Misc.Scenes.HomeScene;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.VelocityComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.CentralMassComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Core.SystemHub;
import inf.elte.hu.gameengine_javafx.Entities.ParticleEmitterEntity;
import inf.elte.hu.gameengine_javafx.Entities.ParticleEntity;
import inf.elte.hu.gameengine_javafx.Entities.PlayerEntity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.NSidedShape;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Misc.Configs.DisplayConfig;
import inf.elte.hu.gameengine_javafx.Misc.Direction;
import inf.elte.hu.gameengine_javafx.Misc.Sound.SoundEffect;
import inf.elte.hu.gameengine_javafx.Misc.Sound.SoundEffectStore;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.SceneManagementSystem;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Random;

/**
 * System responsible for removing dead entities from the game world and handling their associated behaviors.
 */
public class RemoveDeadObjectSystem extends GameSystem {

    /**
     * Starts the system by setting it to active.
     */
    @Override
    public void start() {
        this.active = true;
    }

    /**
     * Updates the system by checking for dead entities, and removes them from the game world while triggering associated behaviors.
     */
    @Override
    protected void update() {
        var entities = EntityHub.getInstance().getEntitiesWithComponent(HealthComponent.class);
        var dead = new ArrayList<Entity>();
        for (var entity : entities) {
            addToDeadList(entity, dead);
        }
        for (var entity : dead) {
            // Removes a sound effect that was associated with a now dead entity
            SoundEffectStore.getInstance().getSoundEffects().removeIf(e -> e.getOwner() == entity);

            if (handlePlayerEntity(entity)) continue;
            EntityHub.getInstance().removeEntity(entity);
            handlePolarBearEntity(entity);
            handleChestEntity(entity);
            handleBigSnowBallEntity(entity);
            handleSnowBallEntity(entity);
        }
    }

    /**
     * Adds entities that are dead to the dead list for processing.
     *
     * @param entity the entity to check
     * @param dead   the list of dead entities
     */
    private void addToDeadList(Entity entity, ArrayList<Entity> dead) {
        if (!entity.getComponent(HealthComponent.class).isAlive()) {
            dead.add(entity);
        }
        killUnmovingSnowballs(entity, dead);
    }

    /**
     * Marks snowball entities with low velocity as dead.
     * @param entity the entity to check
     * @param dead the list of dead entities
     */
    private void killUnmovingSnowballs(Entity entity, ArrayList<Entity> dead) {
        if (entity instanceof SnowBallEntity || entity instanceof BigSnowBallEntity) {
            if (Math.abs(entity.getComponent(VelocityComponent.class).getVelocity().getDx()) < 0.5 && Math.abs(entity.getComponent(VelocityComponent.class).getVelocity().getDy()) < 0.5) {
                dead.add(entity);
            }
        }
    }

    /**
     * Handles the removal of a polar bear entity, including adding gold and tracking the cause of death.
     * @param entity the polar bear entity to handle
     */
    private void handlePolarBearEntity(Entity entity) {
        if (entity instanceof PolarBearEntity) {
            Random rand = new Random();
            int gold = rand.nextInt(10, 25);
            handleGoldGained(entity, gold);
            extractCauseOfDeath(entity);
        }
    }

    /**
     * Extracts the cause of death for the polar bear entity and updates player statistics.
     * @param entity the polar bear entity
     */
    private void extractCauseOfDeath(Entity entity) {
        switch (entity.getComponent(HealthComponent.class).getCauseOfDeath()) {
            case RANGED:
                PlayerStats.rangedKills++;
                break;
            case MELEE:
                PlayerStats.meleeKills++;
                break;
        }
    }

    /**
     * Handles the player entity when it is dead, including transitioning to the home scene and updating player stats.
     * @param entity the player entity to handle
     * @return true if the entity was a player and was handled, false otherwise
     */
    private boolean handlePlayerEntity(Entity entity) {
        if (entity instanceof PlayerEntity) {
            Random rand = new Random();
            SystemHub.getInstance().getSystem(SceneManagementSystem.class).requestSceneChange(new HomeScene(new BorderPane(), DisplayConfig.resolution.first(), DisplayConfig.resolution.second(), new Point(10 * 100 + 100 / 2, 3 * 100)));
            PlayerStats.gold = (int) (PlayerStats.gold * rand.nextDouble(0.5, 0.75));
            PlayerStats.health = rand.nextInt(25, 100);
            return true;
        }
        return false;
    }

    /**
     * Handles the removal of a chest entity, including adding gold and creating particle effects.
     * @param entity the chest entity to handle
     */
    private void handleChestEntity(Entity entity) {
        if (entity instanceof ChestEntity) {
            Random rand = new Random();
            int gold = rand.nextInt(25, 50);
            handleGoldGained(entity, gold);
            CentralMassComponent pos = entity.getComponent(CentralMassComponent.class);
            new ParticleEmitterEntity(
                    pos.getCentralX(), pos.getCentralY(),
                    new ParticleEntity(pos.getCentralX(), pos.getCentralY(), 15, 15,
                            new NSidedShape(new Point(pos.getCentralX(), pos.getCentralY()), 5, 32),
                            Color.BROWN, Color.SADDLEBROWN, 75),
                    Direction.ALL, 10
            );
        }
    }

    /**
     * Handles the removal of a big snowball entity, creating particle effects at its location.
     * @param entity the big snowball entity to handle
     */
    private void handleBigSnowBallEntity(Entity entity) {
        if (entity instanceof BigSnowBallEntity) {
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
     * Handles the removal of a snowball entity, creating particle effects at its location.
     * @param entity the snowball entity to handle
     */
    private void handleSnowBallEntity(Entity entity) {
        if (entity instanceof SnowBallEntity) {
            CentralMassComponent pos = entity.getComponent(CentralMassComponent.class);
            new ParticleEmitterEntity(
                    pos.getCentralX(), pos.getCentralY(),
                    new ParticleEntity(pos.getCentralX(), pos.getCentralY(), 10, 10,
                            new NSidedShape(new Point(pos.getCentralX(), pos.getCentralY()), 5, 32),
                            Color.SNOW, Color.GREY, 50),
                    Direction.ALL, 3
            );
        }
    }

    /**
     * Handles the addition of gold to the player's total and displays a gold gained label.
     * @param entity the entity that triggered the gold gain
     * @param gold the amount of gold gained
     */
    private void handleGoldGained(Entity entity, int gold) {
        PlayerStats.gold += gold;
        SoundEffectStore.getInstance().add(new SoundEffect(entity, "/assets/sound/sfx/goldGained.wav", "gold_" + entity.getId(), 0.6f, 0.0f, 1000, false));
        new GoldGainedLabel(String.valueOf(gold), entity.getComponent(CentralMassComponent.class).getCentralX(), entity.getComponent(CentralMassComponent.class).getCentralY() - 50, 100, 100);
    }
}
