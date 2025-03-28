package Game.Misc.EventHandling.EventListeners;

import Game.Entities.AdventureLabel;
import Game.Entities.DungeonLabel;
import Game.Misc.EventHandling.Events.EnterAdventureEvent;
import Game.Misc.EventHandling.Events.EnterDungeonEvent;
import Game.Misc.Scenes.DungeonScene;
import Game.Misc.Scenes.EnemyIslandScene;
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

public class EnterDungeonEventListener implements EventListener<EnterDungeonEvent> {
    @Override
    public void onEvent(EnterDungeonEvent event) {
        ((DungeonLabel)EntityHub.getInstance().getEntitiesWithType(DungeonLabel.class).getFirst()).addToUI();
        if (KeyboardInputHandler.getInstance().isKeyPressed(KeyCode.E))
            SystemHub.getInstance().getSystem(SceneManagementSystem.class).requestSceneChange(new DungeonScene(new BorderPane(), 1920, 1080));
    }

    @Override
    public void onExit() {
        ((LabelEntity)EntityHub.getInstance().getEntitiesWithType(DungeonLabel.class).getFirst()).removeFromUI();
    }
}
