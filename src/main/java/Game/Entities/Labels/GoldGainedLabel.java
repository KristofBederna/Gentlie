package Game.Entities.Labels;

import inf.elte.hu.gameengine_javafx.Components.TimeComponent;
import inf.elte.hu.gameengine_javafx.Components.UIComponents.LabelComponent;
import inf.elte.hu.gameengine_javafx.Entities.UIEntities.LabelEntity;
import javafx.application.Platform;

public class GoldGainedLabel extends LabelEntity {
    public GoldGainedLabel(String text, double x, double y, double width, double height) {
        super(text, x, y, width, height);
        this.addComponent(new TimeComponent(500L));
        this.getComponent(TimeComponent.class).setLastOccurrence();

        Platform.runLater(() -> {
            this.getComponent(LabelComponent.class).getUIElement().setStyle("-fx-font-weight: bold; -fx-font-size: 36px; -fx-text-fill: gold; -fx-text-alignment: center;");
        });

        addToManager();
    }
}
