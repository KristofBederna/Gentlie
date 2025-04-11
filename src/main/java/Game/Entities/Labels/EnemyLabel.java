package Game.Entities.Labels;

import inf.elte.hu.gameengine_javafx.Components.UIComponents.LabelComponent;
import inf.elte.hu.gameengine_javafx.Entities.UIEntities.LabelEntity;
import javafx.application.Platform;

public class EnemyLabel extends LabelEntity {
    public EnemyLabel(String text, double x, double y, double width, double height) {
        super(text, x, y, width, height);
        Platform.runLater(() -> {
            this.getComponent(LabelComponent.class).getUIElement().setStyle("-fx-font-size: 36px; -fx-text-fill: white; -fx-highlight-text-fill: black;");
        });
    }
}
