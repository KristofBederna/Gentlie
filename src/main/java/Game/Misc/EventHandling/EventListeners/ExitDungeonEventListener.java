package Game.Misc.EventHandling.EventListeners;

import Game.Entities.Labels.EnterEnemyIslandLabel;
import Game.Entities.PolarBearEntity;
import Game.Misc.DungeonGenerationConfig;
import Game.Misc.EventHandling.Events.ExitDungeonEvent;
import Game.Misc.GameSaver;
import Game.Misc.PlayerStats;
import Game.Misc.Scenes.EnemyIslandScene;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Core.SystemHub;
import inf.elte.hu.gameengine_javafx.Entities.UIEntities.LabelEntity;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.EventListener;
import inf.elte.hu.gameengine_javafx.Misc.InputHandlers.KeyboardInputHandler;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.SceneManagementSystem;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;

import java.io.File;

public class ExitDungeonEventListener implements EventListener<ExitDungeonEvent> {
    /**
     * Listens for a pressed E key, if E is pressed, switches the scene, but before switching saves the dungeon state, if there are no enemies remaining the setup for a new map is made.
     *
     * @param event The event instance to be processed.
     */
    @Override
    public void onEvent(ExitDungeonEvent event) {
        ((EnterEnemyIslandLabel) EntityHub.getInstance().getEntitiesWithType(EnterEnemyIslandLabel.class).getFirst()).addToUI();
        if (KeyboardInputHandler.getInstance().isKeyPressed(KeyCode.E)) {
            GameSaver.saveDungeonState();
            var PolarBears = EntityHub.getInstance().getEntitiesWithType(PolarBearEntity.class);
            if (PolarBears.isEmpty()) {
                newMapSetup();
            }
            SystemHub.getInstance().getSystem(SceneManagementSystem.class).requestSceneChange(new EnemyIslandScene(new BorderPane(), 1920, 1080, event.getSpawn()));
        }
    }

    /**
     * Deletes the old map's file, updates the difficulty sliders.
     */
    private void newMapSetup() {
        DungeonGenerationConfig.enemySpawnFactorReset = Math.min(-0.45, DungeonGenerationConfig.enemySpawnFactorReset += 0.02);
        DungeonGenerationConfig.chestSpawnFactorReset = Math.min(-0.45, DungeonGenerationConfig.chestSpawnFactorReset += 0.02);
        File map = new File(PlayerStats.currentSave + "/lastMapGenerated.txt");
        if (map.exists())
            map.delete();
        File entities = new File(PlayerStats.currentSave + "/dungeonEntities.txt");
        if (entities.exists())
            entities.delete();
    }

    /**
     * If the entity exits the event's tile, the label is removed.
     * @param event The event instance to be processed.
     */
    @Override
    public void onExit(ExitDungeonEvent event) {
        ((LabelEntity) EntityHub.getInstance().getEntitiesWithType(EnterEnemyIslandLabel.class).getFirst()).removeFromUI();
    }
}
