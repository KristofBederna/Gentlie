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
        canvas.getScene().getWindow().setWidth(Config.resolution.first());
        canvas.getScene().getWindow().setHeight(Config.resolution.second());
        canvas.setWidth(event.getWidth());
        canvas.setHeight(event.getHeight());
        if (CameraEntity.getInstance() != null) {
            CameraEntity.getInstance().setHeight(canvas.getHeight());
            CameraEntity.getInstance().setWidth(canvas.getWidth());
        }
        Config.gameCanvasHeight = canvas.getScene().getWindow().getHeight();
        Config.gameCanvasWidth = canvas.getScene().getWindow().getWidth();
        Config.setRelativeAspectRatio();
        System.out.println(Config.relativeHeightRatio);
        System.out.println(Config.relativeWidthRatio);
        SystemHub.getInstance().getSystem(SceneManagementSystem.class).requestSceneChange(SystemHub.getInstance().getSystem(SceneManagementSystem.class).getCurrentScene());
    }

    @Override
    public void onExit() {

    }

}
