package inf.elte.hu.gameengine_javafx.Misc.EventHandling.EventListeners;

import inf.elte.hu.gameengine_javafx.Components.WorldComponents.WorldDimensionComponent;
import inf.elte.hu.gameengine_javafx.Core.SystemHub;
import inf.elte.hu.gameengine_javafx.Entities.CameraEntity;
import inf.elte.hu.gameengine_javafx.Entities.WorldEntity;
import inf.elte.hu.gameengine_javafx.Misc.Config;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.EventListener;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.Events.ResolutionChangeEvent;
import inf.elte.hu.gameengine_javafx.Misc.Layers.GameCanvas;
import inf.elte.hu.gameengine_javafx.Misc.MapClasses.World;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.SceneManagementSystem;

public class ResolutionChangeEventListener implements EventListener<ResolutionChangeEvent> {
    @Override
    public void onEvent(ResolutionChangeEvent event) {
        GameCanvas canvas = GameCanvas.getInstance();
        canvas.getScene().getWindow().setWidth(event.getWidth());
        canvas.getScene().getWindow().setHeight(event.getHeight());
        canvas.setWidth(event.getWidth());
        canvas.setHeight(event.getHeight());
        Config.setRelativeAspectRatio();
        SystemHub.getInstance().getSystem(SceneManagementSystem.class).requestSceneChange(SystemHub.getInstance().getSystem(SceneManagementSystem.class).getCurrentScene());
    }

    @Override
    public void onExit() {

    }

}
