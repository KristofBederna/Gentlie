package inf.elte.hu.gameengine_javafx.Misc.EventHandling.EventListeners;

import inf.elte.hu.gameengine_javafx.Core.SystemHub;
import inf.elte.hu.gameengine_javafx.Misc.Configs.DisplayConfig;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.EventListener;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.Events.ResolutionChangeEvent;
import inf.elte.hu.gameengine_javafx.Misc.Layers.GameCanvas;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.SceneManagementSystem;

public class ResolutionChangeEventListener implements EventListener<ResolutionChangeEvent> {
    @Override
    public void onEvent(ResolutionChangeEvent event) {
        GameCanvas canvas = GameCanvas.getInstance();
        if (!DisplayConfig.fullScreenMode) {
            canvas.getScene().getWindow().setWidth(event.getWidth());
            canvas.getScene().getWindow().setHeight(event.getHeight());
        }
        canvas.setWidth(event.getWidth());
        canvas.setHeight(event.getHeight());
        DisplayConfig.setRelativeAspectRatio();
        SystemHub.getInstance().getSystem(SceneManagementSystem.class).requestSceneChange(SystemHub.getInstance().getSystem(SceneManagementSystem.class).getCurrentScene());
    }

    @Override
    public void onExit(ResolutionChangeEvent event) {

    }

}
