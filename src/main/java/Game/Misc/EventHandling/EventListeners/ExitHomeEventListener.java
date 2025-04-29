package Game.Misc.EventHandling.EventListeners;

import Game.Misc.EventHandling.Events.ExitHomeEvent;
import Game.Misc.Scenes.HomeIslandScene;
import inf.elte.hu.gameengine_javafx.Core.SystemHub;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Misc.Configs.MapConfig;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.EventListener;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.SceneManagementSystem;
import javafx.scene.layout.BorderPane;

public class ExitHomeEventListener implements EventListener<ExitHomeEvent> {
    /**
     * Listens for a player position, if the position is inside the area, switches the scene.
     *
     * @param event The event instance to be processed.
     */
    @Override
    public void onEvent(ExitHomeEvent event) {
        SystemHub.getInstance().getSystem(SceneManagementSystem.class).requestSceneChange(new HomeIslandScene(new BorderPane(), 1920, 1080, new Point(7 * MapConfig.scaledTileSize - MapConfig.scaledTileSize * 0.25 - 1, 2 * MapConfig.scaledTileSize + MapConfig.scaledTileSize * 0.25 - 1)));
    }

    @Override
    public void onExit(ExitHomeEvent event) {

    }
}
