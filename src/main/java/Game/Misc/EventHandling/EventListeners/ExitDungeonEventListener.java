package Game.Misc.EventHandling.EventListeners;

import Game.Entities.ChestEntity;
import Game.Entities.Labels.EnterEnemyIslandLabel;
import Game.Entities.PolarBearEntity;
import Game.Misc.EventHandling.Events.ExitDungeonEvent;
import Game.Misc.PlayerStats;
import Game.Misc.Scenes.EnemyIslandScene;
import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.CentralMassComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Core.SystemHub;
import inf.elte.hu.gameengine_javafx.Entities.UIEntities.LabelEntity;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.EventListener;
import inf.elte.hu.gameengine_javafx.Misc.InputHandlers.KeyboardInputHandler;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.SceneManagementSystem;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ExitDungeonEventListener implements EventListener<ExitDungeonEvent> {
    @Override
    public void onEvent(ExitDungeonEvent event) {
        ((EnterEnemyIslandLabel) EntityHub.getInstance().getEntitiesWithType(EnterEnemyIslandLabel.class).getFirst()).addToUI();
        if (KeyboardInputHandler.getInstance().isKeyPressed(KeyCode.E)) {
            var PolarBears = EntityHub.getInstance().getEntitiesWithType(PolarBearEntity.class);
            var Chests = EntityHub.getInstance().getEntitiesWithType(ChestEntity.class);
            if (PolarBears.isEmpty()) {
                File map = new File(PlayerStats.currentSave + "/lastMapGenerated.txt");
                if (map.exists())
                    map.delete();
                File entities = new File(PlayerStats.currentSave + "/dungeonEntities.txt");
                if (entities.exists())
                    entities.delete();
            } else {
                File file = new File(PlayerStats.currentSave + "/dungeonEntities.txt");
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(PlayerStats.currentSave + "/dungeonEntities.txt"))) {
                    writer.write("PolarBears\n");
                    for (int i = 0; i < PolarBears.size(); i++) {
                        Entity polarBear = PolarBears.get(i);
                        PositionComponent pos = polarBear.getComponent(PositionComponent.class);
                        double x = pos.getGlobalX();
                        double y = pos.getGlobalY();

                        writer.write(x + " " + y);

                        writer.newLine();
                    }
                    writer.write("Chests\n");
                    for (int i = 0; i < Chests.size(); i++) {
                        Entity chest = Chests.get(i);
                        CentralMassComponent pos = chest.getComponent(CentralMassComponent.class);
                        double x = pos.getCentralX();
                        double y = pos.getCentralY();
                        writer.write(x + " " + y);
                        if (i < Chests.size() - 1) {
                            writer.newLine();
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
            SystemHub.getInstance().getSystem(SceneManagementSystem.class).requestSceneChange(new EnemyIslandScene(new BorderPane(), 1920, 1080, event.getSpawn()));
        }
    }

    @Override
    public void onExit() {
        ((LabelEntity) EntityHub.getInstance().getEntitiesWithType(EnterEnemyIslandLabel.class).getFirst()).removeFromUI();
    }
}
