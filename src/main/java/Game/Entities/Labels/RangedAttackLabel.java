package Game.Entities.Labels;

import inf.elte.hu.gameengine_javafx.Entities.UIEntities.TextEntity;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class RangedAttackLabel extends TextEntity {
    public RangedAttackLabel(String text, double x, double y, double width, double height) {
        super(x, y, width, height, text);
        Platform.runLater(() -> {
            Text textNode = this.getTextNode();
            textNode.setStyle("-fx-font-weight: bold; -fx-font-size: 36px;");
            textNode.setFill(Color.WHITE);
            textNode.setStroke(Color.BLACK);
            textNode.setStrokeWidth(1.5);
        });
    }
}
