package Game.Entities.Labels;

import inf.elte.hu.gameengine_javafx.Components.TimeComponent;
import inf.elte.hu.gameengine_javafx.Entities.UIEntities.TextEntity;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class GoldGainedLabel extends TextEntity {
    public GoldGainedLabel(String text, double x, double y, double width, double height) {
        super(x, y, width, height, text);
        this.addComponent(new TimeComponent(500L));
        this.getComponent(TimeComponent.class).setLastOccurrence();

        Platform.runLater(() -> {
            Text textNode = this.getTextNode();
            textNode.setStyle("-fx-font-weight: bold; -fx-font-size: 24px;");
            textNode.setFill(Color.GOLD);
        });

        addToManager();
    }
}
