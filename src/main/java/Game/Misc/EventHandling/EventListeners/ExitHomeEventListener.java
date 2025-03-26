package Game.Misc.EventHandling.EventListeners;

import Game.Misc.EventHandling.Events.ExitHomeEvent;
import Game.Misc.Scenes.HomeIslandScene;
import Game.Misc.Scenes.MainScene;
import inf.elte.hu.gameengine_javafx.Core.SystemHub;
import inf.elte.hu.gameengine_javafx.Main;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.EventListener;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.SceneManagementSystem;
import javafx.scene.layout.BorderPane;

public class ExitHomeEventListener implements EventListener<ExitHomeEvent> {
    @Override
    public void onEvent(ExitHomeEvent event) {
        SystemHub.getInstance().getSystem(SceneManagementSystem.class).requestSceneChange(new HomeIslandScene(new BorderPane(), 1920, 1080));
    }

    @Override
    public void onExit() {

    }
}
