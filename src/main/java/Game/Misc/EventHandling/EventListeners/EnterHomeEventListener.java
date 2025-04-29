package Game.Misc.EventHandling.EventListeners;

import Game.Entities.Labels.EnterHomeLabel;
import Game.Misc.EventHandling.Events.EnterHomeEvent;
import Game.Misc.Scenes.HomeScene;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Core.SystemHub;
import inf.elte.hu.gameengine_javafx.Entities.UIEntities.LabelEntity;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.EventListener;
import inf.elte.hu.gameengine_javafx.Misc.InputHandlers.KeyboardInputHandler;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.SceneManagementSystem;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;

public class EnterHomeEventListener implements EventListener<EnterHomeEvent> {
    /**
     * Listens for a pressed E key, if E is pressed, switches the scene.
     *
     * @param event The event instance to be processed.
     */
    @Override
    public void onEvent(EnterHomeEvent event) {
        ((EnterHomeLabel)EntityHub.getInstance().getEntitiesWithType(EnterHomeLabel.class).getFirst()).addToUI();
        if (KeyboardInputHandler.getInstance().isKeyPressed(KeyCode.E))
            SystemHub.getInstance().getSystem(SceneManagementSystem.class).requestSceneChange(new HomeScene(new BorderPane(), 1920, 1080, event.getSpawn()));
    }

    /**
     * If the entity exits the event's tile, the label is removed.
     * @param event The event instance to be processed.
     */
    @Override
    public void onExit(EnterHomeEvent event) {
        ((LabelEntity)EntityHub.getInstance().getEntitiesWithType(EnterHomeLabel.class).getFirst()).removeFromUI();
    }
}
