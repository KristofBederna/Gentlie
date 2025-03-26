package inf.elte.hu.gameengine_javafx.Misc.EventHandling.EventListeners;

import inf.elte.hu.gameengine_javafx.Misc.EventHandling.EventListener;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.Events.FullScreenToggleEvent;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.Events.TestEvent;
import javafx.application.Platform;
import javafx.stage.Stage;

public class FullScreenToggleEventListener implements EventListener<FullScreenToggleEvent> {
    @Override
    public void onEvent(FullScreenToggleEvent event) {
        Stage stage = event.getStage();
        Platform.runLater(() -> stage.setFullScreen(!stage.isFullScreen()));
    }

    @Override
    public void onExit() {

    }
}
