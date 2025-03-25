package inf.elte.hu.gameengine_javafx.Systems.ResourceSystems;

import Game.Misc.Scenes.MainScene;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Misc.Scenes.GameScene;
import inf.elte.hu.gameengine_javafx.Misc.Scenes.GeneratorTestScene;
import inf.elte.hu.gameengine_javafx.Misc.Scenes.TestScene;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class SceneManagementSystem extends GameSystem {
    private GameScene currentScene;
    private GameScene nextScene;
    private Stage stage;

    public SceneManagementSystem() {
        // Initialize with a default scene
        currentScene = new MainScene(new BorderPane(), 1920, 1080);
    }

    @Override
    public void start() {
        active = true;
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

    public Scene getCurrentScene() {
        return currentScene;
    }

    public Stage getStage() {
        return stage;
    }
}
