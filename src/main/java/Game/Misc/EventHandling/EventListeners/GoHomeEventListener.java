package Game.Misc.EventHandling.EventListeners;

import Game.Entities.Labels.GoHomeLabel;
import Game.Misc.EventHandling.Events.GoHomeEvent;
import Game.Misc.Scenes.HomeIslandScene;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Core.SystemHub;
import inf.elte.hu.gameengine_javafx.Entities.UIEntities.LabelEntity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Misc.Config;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.EventListener;
import inf.elte.hu.gameengine_javafx.Misc.InputHandlers.KeyboardInputHandler;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.SceneManagementSystem;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;

public class GoHomeEventListener implements EventListener<GoHomeEvent> {
    @Override
    public void onEvent(GoHomeEvent event) {
        ((GoHomeLabel) EntityHub.getInstance().getEntitiesWithType(GoHomeLabel.class).getFirst()).addToUI();
        if (KeyboardInputHandler.getInstance().isKeyPressed(KeyCode.E))
            SystemHub.getInstance().getSystem(SceneManagementSystem.class).requestSceneChange(new HomeIslandScene(new BorderPane(), 1920, 1080, new Point(13* Config.scaledTileSize -Config.scaledTileSize *0.25-1, 2*Config.scaledTileSize +Config.scaledTileSize *0.25-1)));
    }

    @Override
    public void onExit() {
        ((LabelEntity)EntityHub.getInstance().getEntitiesWithType(GoHomeLabel.class).getFirst()).removeFromUI();
    }
}
