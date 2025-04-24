package inf.elte.hu.gameengine_javafx;

import inf.elte.hu.gameengine_javafx.Core.SystemHub;
import inf.elte.hu.gameengine_javafx.Misc.Configs.DisplayConfig;
import inf.elte.hu.gameengine_javafx.Misc.Layers.GameCanvas;
import inf.elte.hu.gameengine_javafx.Misc.Layers.GameLayer;
import inf.elte.hu.gameengine_javafx.Misc.Layers.uiRoot;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.SystemStartUp;
import inf.elte.hu.gameengine_javafx.Misc.UtilityFunctions;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.SceneManagementSystem;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        startUpGame(stage);
    }

    public static void main(String[] args) {
        launch();
    }

    private void startUpGame(Stage stage) {
        SystemStartUp systemStartUp = new SystemStartUp(() -> {
        });
        systemStartUp.startUpSceneManagementSystem();
        SceneManagementSystem sceneManagementSystem = SystemHub.getInstance().getSystem(SceneManagementSystem.class);
        sceneManagementSystem.start();
        sceneManagementSystem.setStage(stage);
        stage.setFullScreen(DisplayConfig.fullScreenMode);
        stage.setFullScreenExitHint("");
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.setResizable(false);

        BorderPane root = (BorderPane) sceneManagementSystem.getCurrentScene().getRoot();

        GameCanvas gameCanvas = GameCanvas.createInstance(DisplayConfig.resolution.first(), DisplayConfig.resolution.second());
        uiRoot UIRoot = uiRoot.getInstance();
        GameLayer gameLayer = GameLayer.getInstance();
        gameLayer.getChildren().addAll(gameCanvas, UIRoot);
        UIRoot.setFocusTraversable(true);

        root.setCenter(gameLayer);

        stageSetup();
    }

    private void stageSetup() {
        Stage stage = SystemHub.getInstance().getSystem(SceneManagementSystem.class).getStage();
        Scene scene = SystemHub.getInstance().getSystem(SceneManagementSystem.class).getCurrentScene();

        // Set window title here
        stage.setTitle(DisplayConfig.windowTitle);

        // Assigns the stage as the parent container of the scene
        stage.setScene(scene);

        // Define on close behaviour here
        stage.setOnCloseRequest(event -> {
            UtilityFunctions.shutDownMethod();
        });

        // Makes the window visible
        Platform.runLater(stage::show);
    }
}
