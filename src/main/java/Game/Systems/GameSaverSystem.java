package Game.Systems;

import Game.Misc.GameSaver;
import Game.Misc.Scenes.DungeonScene;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.SystemHub;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.SceneManagementSystem;

public class GameSaverSystem extends GameSystem {
    long lastSaveTime = System.currentTimeMillis();

    @Override
    public void start() {
        this.active = true;
    }

    @Override
    public void update() {
        if (System.currentTimeMillis() - lastSaveTime < 500) {
            return;
        }
        GameSaver.saveEntityStats();
        GameSaver.saveShopPrices();
        GameSaver.saveTime();
        if (SystemHub.getInstance().getSystem(SceneManagementSystem.class).getCurrentScene() instanceof DungeonScene) {
            GameSaver.saveDungeonState();
        }
        lastSaveTime = System.currentTimeMillis();
    }

    public void manualSave() {
        GameSaver.saveEntityStats();
        GameSaver.saveShopPrices();
        if (SystemHub.getInstance().getSystem(SceneManagementSystem.class).getCurrentScene() instanceof DungeonScene) {
            GameSaver.saveDungeonState();
        }
    }
}
