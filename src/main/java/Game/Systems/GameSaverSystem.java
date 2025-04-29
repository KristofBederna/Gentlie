package Game.Systems;

import Game.Misc.GameSaver;
import Game.Misc.Scenes.DungeonScene;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.SystemHub;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.SceneManagementSystem;

/**
 * System responsible for periodically and manually saving game state, including entity stats,
 * shop prices, time, and dungeon state (if applicable).
 */
public class GameSaverSystem extends GameSystem {
    long lastSaveTime = System.currentTimeMillis();

    /**
     * Activates the system at startup.
     */
    @Override
    public void start() {
        this.active = true;
    }

    /**
     * Automatically saves game data at least every 500 milliseconds.
     * Saves entity stats, shop prices, time, and dungeon state if the current scene is a dungeon.
     */
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

    /**
     * Manually saves the game state.
     * Saves entity stats, shop prices, and dungeon state if the current scene is a dungeon.
     */
    public void manualSave() {
        GameSaver.saveEntityStats();
        GameSaver.saveShopPrices();

        if (SystemHub.getInstance().getSystem(SceneManagementSystem.class).getCurrentScene() instanceof DungeonScene) {
            GameSaver.saveDungeonState();
        }
    }
}
