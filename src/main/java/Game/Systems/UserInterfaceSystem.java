package Game.Systems;

import Game.Entities.Labels.DamageLabel;
import Game.Entities.Labels.GoldGainedLabel;
import Game.Entities.Labels.GoldLabel;
import Game.Entities.Labels.HealthLabel;
import Game.Misc.PlayerStats;
import inf.elte.hu.gameengine_javafx.Components.TimeComponent;
import inf.elte.hu.gameengine_javafx.Components.UIComponents.LabelComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Misc.Layers.uiRoot;
import javafx.application.Platform;
import javafx.scene.Node;

public class UserInterfaceSystem extends GameSystem {
    private double lastGold = PlayerStats.gold;
    private double lastHealth = PlayerStats.health;

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
        if (goldLabel != null && healthLabel != null) {
            if (PlayerStats.gold > lastGold) {
                Platform.runLater(() -> {
                    // Update the gold label without removing it
                    goldLabel.getComponent(LabelComponent.class).getUIElement().setText(String.valueOf(PlayerStats.gold));
                    lastGold = PlayerStats.gold;
                });
            }
            if (PlayerStats.health > lastHealth) {
                Platform.runLater(() -> {
                    // Update the health label without removing it
                    healthLabel.getComponent(LabelComponent.class).getUIElement().setText(String.valueOf(PlayerStats.health));
                    lastHealth = PlayerStats.health;
                });
            }
        }

        // If goldLabel is missing, create a new one
        if (goldLabel == null) {
            Platform.runLater(() -> {
                new GoldLabel(String.valueOf(PlayerStats.gold), 100, 100, 100, 100);
            });
        }

        // If healthLabel is missing, create a new one
        if (healthLabel == null) {
            Platform.runLater(() -> {
                new HealthLabel(String.valueOf(PlayerStats.health), 100, 200, 100, 100);
            });
        }
    }
}
