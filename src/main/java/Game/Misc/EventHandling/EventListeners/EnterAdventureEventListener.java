package Game.Misc.EventHandling.EventListeners;

import Game.Entities.AdventureLabel;
import Game.Entities.EnterHomeLabel;
import Game.Misc.EventHandling.Events.EnterAdventureEvent;
import Game.Misc.EventHandling.Events.EnterHomeEvent;
import Game.Misc.Scenes.EnemyIslandScene;
import Game.Misc.Scenes.HomeScene;
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

public class EnterAdventureEventListener implements EventListener<EnterAdventureEvent> {
    @Override
    public void onEvent(EnterAdventureEvent event) {
        ((AdventureLabel)EntityHub.getInstance().getEntitiesWithType(AdventureLabel.class).getFirst()).addToUI();
        if (KeyboardInputHandler.getInstance().isKeyPressed(KeyCode.E))
            SystemHub.getInstance().getSystem(SceneManagementSystem.class).requestSceneChange(new EnemyIslandScene(new BorderPane(), 1920, 1080, new Point(2*Config.tileSize, 2*Config.tileSize+Config.tileSize*0.25-1)));
    }

    @Override
    public void onExit() {
        ((LabelEntity)EntityHub.getInstance().getEntitiesWithType(AdventureLabel.class).getFirst()).removeFromUI();
    }
}
