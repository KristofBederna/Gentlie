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

public class RemoveDeadObjectSystem extends GameSystem {
    @Override
    public void start() {
        this.active = true;
    }

    @Override
    protected void update() {
        var entities = EntityHub.getInstance().getEntitiesWithComponent(HealthComponent.class);
        var dead = new ArrayList<Entity>();
        for (var entity : entities) {
            if (!entity.getComponent(HealthComponent.class).isAlive()) {
                dead.add(entity);
            }
            if (entity instanceof SnowBallEntity || entity instanceof BigSnowBallEntity) {
                if (Math.abs(entity.getComponent(VelocityComponent.class).getVelocity().getDx()) < 0.5 && Math.abs(entity.getComponent(VelocityComponent.class).getVelocity().getDy()) < 0.5) {
                    dead.add(entity);
                }
            }
        }
        for (var entity : dead) {
            SoundEffectStore.getInstance().getSoundEffects().removeIf(e -> e.getOwner() == entity);

            if (entity instanceof PlayerEntity) {
                Random rand = new Random();
                SystemHub.getInstance().getSystem(SceneManagementSystem.class).requestSceneChange(new HomeScene(new BorderPane(), DisplayConfig.resolution.first(), DisplayConfig.resolution.second(), new Point(10 * 100 + 100 / 2, 3 * 100)));
                PlayerStats.gold = (int) (PlayerStats.gold * rand.nextDouble(0.5, 0.75));
                PlayerStats.health = rand.nextInt(25, 100);
                continue;
            }
            EntityHub.getInstance().removeEntity(entity);
            if (entity instanceof PolarBearEntity) {
                Random rand = new Random();
                int gold = rand.nextInt(10, 25);
                handleGoldGained(entity, gold);

                switch (entity.getComponent(HealthComponent.class).getCauseOfDeath()) {
                    case RANGED:
                        PlayerStats.rangedKills++;
                        break;
                    case MELEE:
                        PlayerStats.meleeKills++;
                        break;
                }
            }
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
    }

    private void handleGoldGained(Entity entity, int gold) {
        PlayerStats.gold += gold;
        SoundEffectStore.getInstance().add(new SoundEffect(entity, "/assets/sound/sfx/goldGained.wav", "gold_" + entity.getId(), 0.6f, 0.0f, 1000, false));
        new GoldGainedLabel(String.valueOf(gold), entity.getComponent(CentralMassComponent.class).getCentralX(), entity.getComponent(CentralMassComponent.class).getCentralY() - 50, 100, 100);
    }
}
