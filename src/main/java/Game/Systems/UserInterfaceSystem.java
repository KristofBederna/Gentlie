package Game.Systems;

import Game.Entities.Labels.*;
import Game.Entities.PolarBearEntity;
import Game.Misc.PlayerStats;
import Game.Misc.Scenes.DungeonScene;
import inf.elte.hu.gameengine_javafx.Components.InteractiveComponent;
import inf.elte.hu.gameengine_javafx.Components.TimeComponent;
import inf.elte.hu.gameengine_javafx.Components.UIComponents.TextComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Core.SystemHub;
import inf.elte.hu.gameengine_javafx.Entities.PlayerEntity;
import inf.elte.hu.gameengine_javafx.Entities.UIEntities.TextEntity;
import inf.elte.hu.gameengine_javafx.Misc.Layers.uiRoot;
import inf.elte.hu.gameengine_javafx.Misc.Tuple;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.SceneManagementSystem;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;

public class UserInterfaceSystem extends GameSystem {
    private double lastGold = PlayerStats.gold;
    private double lastHealth = PlayerStats.health;
    private int lastEnemies = 0;

    @Override
    public void start() {
        this.active = true;
    }

    @Override
    protected void update() {
        var damageLabels = EntityHub.getInstance().getEntitiesWithType(DamageLabel.class);
        var goldGainedLabels = EntityHub.getInstance().getEntitiesWithType(GoldGainedLabel.class);
        for (var entity : damageLabels) {
            if (entity == null) {
                continue;
            }
            handleLabels(entity);
        }
        for (var entity : goldGainedLabels) {
            if (entity == null) {
                continue;
            }
            handleLabels(entity);
        }
        var goldLabel = EntityHub.getInstance().getEntitiesWithType(GoldLabel.class).getFirst();
        var healthLabel = EntityHub.getInstance().getEntitiesWithType(HealthLabel.class).getFirst();
        handleEnemiesLabel();
        showCooldowns();
        handleStatisticsLabels(goldLabel, healthLabel);
    }

    private void handleStatisticsLabels(Entity goldLabel, Entity healthLabel) {
        if (goldLabel != null && healthLabel != null) {
            if (PlayerStats.gold != lastGold) {
                Platform.runLater(() -> {
                    ((TextEntity) goldLabel).getTextNode().setText(String.valueOf(PlayerStats.gold));
                    lastGold = PlayerStats.gold;
                });
            }
            if (PlayerStats.health != lastHealth) {
                Platform.runLater(() -> {
                    ((TextEntity) healthLabel).getTextNode().setText(String.format("%.0f", PlayerStats.health));
                    lastHealth = PlayerStats.health;
                });
            }
        }
    }

    private void showCooldowns() {
        if (SystemHub.getInstance().getSystem(SceneManagementSystem.class).getCurrentScene() instanceof DungeonScene) {
            PlayerEntity player = (PlayerEntity) EntityHub.getInstance().getEntitiesWithType(PlayerEntity.class).getFirst();
            var meleeAttackLabel = EntityHub.getInstance().getEntitiesWithType(MeleeAttackLabel.class).getFirst();
            var rangedAttackLabel = EntityHub.getInstance().getEntitiesWithType(RangedAttackLabel.class).getFirst();
            Platform.runLater(() -> {
                ((TextEntity) meleeAttackLabel).getTextNode().setText(String.valueOf(Math.max(0, player.getComponent(InteractiveComponent.class).getLastTimeCalled(new Tuple<>(null, MouseButton.PRIMARY)).second() == 20 ? 0 : PlayerStats.meleeCooldown + (player.getComponent(InteractiveComponent.class).getLastTimeCalled(new Tuple<>(null, MouseButton.PRIMARY)).first() - System.currentTimeMillis()))));
                ((TextEntity) rangedAttackLabel).getTextNode().setText(String.valueOf(Math.max(0, player.getComponent(InteractiveComponent.class).getLastTimeCalled(new Tuple<>(null, MouseButton.SECONDARY)).second() == 20 ? 0 : PlayerStats.rangedCooldown + (player.getComponent(InteractiveComponent.class).getLastTimeCalled(new Tuple<>(null, MouseButton.SECONDARY)).first() - System.currentTimeMillis()))));
            });
        }
    }

    private void handleEnemiesLabel() {
        if (!EntityHub.getInstance().getEntitiesWithType(EnemyLabel.class).isEmpty()) {
            var enemiesLabel = EntityHub.getInstance().getEntitiesWithType(EnemyLabel.class).getFirst();
            var enemies = EntityHub.getInstance().getEntitiesWithType(PolarBearEntity.class);
            int size = enemies.size();
            if (size != lastEnemies) {
                Platform.runLater(() -> {
                    ((TextEntity) enemiesLabel).getTextNode().setText(String.valueOf(size));
                    lastEnemies = size;
                });
            }
        }
    }

    private void handleLabels(Entity entity) {
        long currentTime = System.currentTimeMillis();
        long lastOccurrence = entity.getComponent(TimeComponent.class).getLastOccurrence();
        long timeBetweenOccurrences = entity.getComponent(TimeComponent.class).getTimeBetweenOccurrences();

        if (currentTime - lastOccurrence > timeBetweenOccurrences) {
            removeLabel(entity);
        }
    }

    private void removeLabel(Entity entity) {
        Platform.runLater(() -> {
            Node uiElement = entity.getComponent(TextComponent.class).getNode();
            if (uiElement != null) {
                uiRoot.getInstance().getChildren().remove(uiElement);
            }
        });
    }
}
