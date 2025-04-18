package inf.elte.hu.gameengine_javafx.Misc.EventHandling.EventListeners;

import inf.elte.hu.gameengine_javafx.Misc.Configs.DisplayConfig;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.EventListener;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.Events.FullScreenToggleEvent;
import javafx.application.Platform;
import javafx.stage.Stage;

public class FullScreenToggleEventListener implements EventListener<FullScreenToggleEvent> {
    @Override
    public void onEvent(FullScreenToggleEvent event) {
        Stage stage = event.getStage();
        Platform.runLater(() -> {
            stage.setFullScreen(!stage.isFullScreen());
            if (!stage.isFullScreen()) {
                stage.setWidth(DisplayConfig.resolution.first());
                stage.setHeight(DisplayConfig.resolution.second());
            }
        });
    }

    @Override
    public void onExit(FullScreenToggleEvent event) {

    }
}
