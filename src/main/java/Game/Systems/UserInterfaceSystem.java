package Game.Systems;

import Game.Entities.Labels.*;
import Game.Entities.PolarBearEntity;
import Game.Misc.PlayerStats;
import inf.elte.hu.gameengine_javafx.Components.TimeComponent;
import inf.elte.hu.gameengine_javafx.Components.UIComponents.LabelComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Entities.UIEntities.TextEntity;
import inf.elte.hu.gameengine_javafx.Misc.Layers.uiRoot;
import javafx.application.Platform;
import javafx.scene.Node;

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
            long currentTime = System.currentTimeMillis();
            long lastOccurrence = entity.getComponent(TimeComponent.class).getLastOccurrence();
            long timeBetweenOccurrences = entity.getComponent(TimeComponent.class).getTimeBetweenOccurrences();

            if (currentTime - lastOccurrence > timeBetweenOccurrences) {
                Platform.runLater(() -> {
                    Node uiElement = entity.getComponent(LabelComponent.class).getNode();
                    if (uiElement != null) {
                        uiRoot.getInstance().getChildren().remove(uiElement);
                    }
                });
            }
        }
        for (var entity : goldGainedLabels) {
            long currentTime = System.currentTimeMillis();
            long lastOccurrence = entity.getComponent(TimeComponent.class).getLastOccurrence();
            long timeBetweenOccurrences = entity.getComponent(TimeComponent.class).getTimeBetweenOccurrences();

            if (currentTime - lastOccurrence > timeBetweenOccurrences) {
                Platform.runLater(() -> {
                    Node uiElement = entity.getComponent(LabelComponent.class).getNode();
                    if (uiElement != null) {
                        uiRoot.getInstance().getChildren().remove(uiElement);
                    }
                });
            }
        }

        var goldLabel = EntityHub.getInstance().getEntitiesWithType(GoldLabel.class).getFirst();
        var healthLabel = EntityHub.getInstance().getEntitiesWithType(HealthLabel.class).getFirst();
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
}
