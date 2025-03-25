package inf.elte.hu.gameengine_javafx.Misc.EventHandling.Events;

import inf.elte.hu.gameengine_javafx.Misc.EventHandling.Event;
import javafx.application.Platform;
import javafx.stage.Stage;

public class FullScreenToggleEvent implements Event {
    Stage stage;

    public FullScreenToggleEvent(Stage stage) {
        this.stage = stage;
    }

    public Stage getStage() {
        return stage;
    }
}
