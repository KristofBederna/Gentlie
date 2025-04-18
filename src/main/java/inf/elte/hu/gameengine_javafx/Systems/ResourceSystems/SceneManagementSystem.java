package inf.elte.hu.gameengine_javafx.Systems.ResourceSystems;

import Game.Misc.Scenes.MainScene;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Misc.Configs.DisplayConfig;
import inf.elte.hu.gameengine_javafx.Misc.Scenes.GameScene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class SceneManagementSystem extends GameSystem {
    private GameScene currentScene;
    private GameScene nextScene;
    private Stage stage;

    @Override
    public void start() {
        active = true;
        // Initialize with a default scene
        currentScene = new MainScene(new BorderPane(), DisplayConfig.resolution.first(), DisplayConfig.resolution.second());
        currentScene.setup();
    }

    @Override
    public void update() {
        if (nextScene != null) {
            switchScene();
        }
    }

    public void switchScene() {
        if (nextScene != null) {
            // Run breakdown on the current scene to clean up
            currentScene.breakdown();

            // Set the current scene to the next scene
            currentScene = nextScene;

            // Perform any setup for the new scene
            currentScene.setup();

            // Reset nextScene to null
            nextScene = null;
        }
    }

    public void requestSceneChange(GameScene newScene) {
        nextScene = newScene;
    }

    @Override
    public void abort() {
        active = false;
        if (currentScene != null) {
            currentScene.breakdown();
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public GameScene getCurrentScene() {
        return currentScene;
    }

    public Stage getStage() {
        return stage;
    }
}
