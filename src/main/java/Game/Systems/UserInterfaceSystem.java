package Game.Systems;

import Game.Entities.Labels.DamageLabel;
import inf.elte.hu.gameengine_javafx.Components.TimeComponent;
import inf.elte.hu.gameengine_javafx.Components.UIComponents.LabelComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Misc.Layers.uiRoot;
import javafx.application.Platform;
import javafx.scene.Node;

public class UserInterfaceSystem extends GameSystem {
    @Override
    public void start() {
        this.active = true;
    }

    @Override
    protected void update() {
        var DamageLabels = EntityHub.getInstance().getEntitiesWithType(DamageLabel.class);
        for (var entity : DamageLabels) {
            long currentTime = System.currentTimeMillis();
            long lastOccurrence = entity.getComponent(TimeComponent.class).getLastOccurrence();
            long timeBetweenOccurrences = entity.getComponent(TimeComponent.class).getTimeBetweenOccurrences();

            if (currentTime - lastOccurrence > timeBetweenOccurrences) {
                Platform.runLater(() -> {
                    Node uiElement = entity.getComponent(LabelComponent.class).getUIElement();
                    if (uiElement != null) {
                        uiRoot.getInstance().getChildren().remove(uiElement);
                    }
                });
            }

        }
    }
}
