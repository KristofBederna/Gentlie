package Game.Misc.EventHandling.EventListeners;

import Game.Entities.Labels.EnterInnLabel;
import Game.Misc.EventHandling.Events.EnterInnEvent;
import Game.Misc.Scenes.InnScene;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Core.SystemHub;
import inf.elte.hu.gameengine_javafx.Entities.UIEntities.LabelEntity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.EventListener;
import inf.elte.hu.gameengine_javafx.Misc.InputHandlers.KeyboardInputHandler;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.SceneManagementSystem;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;

public class EnterInnListener implements EventListener<EnterInnEvent> {
    /**
     * Listens for a pressed E key, if E is pressed, switches the scene.
     *
     * @param event The event instance to be processed.
     */
    @Override
    public void onEvent(EnterInnEvent event) {
        ((EnterInnLabel) EntityHub.getInstance().getEntitiesWithType(EnterInnLabel.class).getFirst()).addToUI();
        if (KeyboardInputHandler.getInstance().isKeyPressed(KeyCode.E))
            SystemHub.getInstance().getSystem(SceneManagementSystem.class).requestSceneChange(new InnScene(new BorderPane(), 1920, 1080, new Point(5 * 150 + 150 / 2, 8 * 150)));
    }

    /**
     * If the entity exits the event's tile, the label is removed.
     * @param event The event instance to be processed.
     */
    @Override
    public void onExit(EnterInnEvent event) {
        ((LabelEntity)EntityHub.getInstance().getEntitiesWithType(EnterInnLabel.class).getFirst()).removeFromUI();
    }
}
