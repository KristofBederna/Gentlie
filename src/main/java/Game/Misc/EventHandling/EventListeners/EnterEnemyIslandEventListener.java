package Game.Misc.EventHandling.EventListeners;

import Game.Entities.Labels.EnterEnemyIslandLabel;
import Game.Misc.EventHandling.Events.EnterEnemyIslandEvent;
import Game.Misc.Scenes.EnemyIslandScene;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Core.SystemHub;
import inf.elte.hu.gameengine_javafx.Entities.UIEntities.LabelEntity;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.EventListener;
import inf.elte.hu.gameengine_javafx.Misc.InputHandlers.KeyboardInputHandler;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.SceneManagementSystem;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;

public class EnterEnemyIslandEventListener implements EventListener<EnterEnemyIslandEvent> {
    /**
     * Listens for a pressed E key, if E is pressed, switches the scene.
     *
     * @param event The event instance to be processed.
     */
    @Override
    public void onEvent(EnterEnemyIslandEvent event) {
        ((EnterEnemyIslandLabel)EntityHub.getInstance().getEntitiesWithType(EnterEnemyIslandLabel.class).getFirst()).addToUI();
        if (KeyboardInputHandler.getInstance().isKeyPressed(KeyCode.E))
            SystemHub.getInstance().getSystem(SceneManagementSystem.class).requestSceneChange(new EnemyIslandScene(new BorderPane(), 1920, 1080, event.getSpawn()));
    }

    /**
     * If the entity exits the event's tile, the label is removed.
     * @param event The event instance to be processed.
     */
    @Override
    public void onExit(EnterEnemyIslandEvent event) {
        ((LabelEntity)EntityHub.getInstance().getEntitiesWithType(EnterEnemyIslandLabel.class).getFirst()).removeFromUI();
    }
}
