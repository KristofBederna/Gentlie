package Game.Misc.Scenes;

import Game.Misc.GameSaver;
import Game.Misc.PlayerStats;
import Game.Misc.UtilityFunctions;
import Game.Systems.CustomRenderSystem;
import inf.elte.hu.gameengine_javafx.Components.UIComponents.ButtonComponent;
import inf.elte.hu.gameengine_javafx.Core.SystemHub;
import inf.elte.hu.gameengine_javafx.Entities.UIEntities.ButtonEntity;
import inf.elte.hu.gameengine_javafx.Misc.Configs.DisplayConfig;
import inf.elte.hu.gameengine_javafx.Misc.Scenes.GameScene;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.GameLoopStartUp;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.ResourceStartUp;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.SystemStartUp;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.ResourceSystem;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.SceneManagementSystem;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class LoadSelectorScene extends GameScene {

    public LoadSelectorScene(Parent parent, double width, double height) {
        super(parent, width, height);
    }

    @Override
    public void setup() {
        getStylesheets().add(Objects.requireNonNull(getClass().getResource("/assets/styles/mainMenu.css")).toExternalForm());
        new SystemStartUp(this::systemStartUp);
        new ResourceStartUp();

        declareEntities();

        this.getRoot().getStyleClass().add("main-menu-scene");

        if (GameLoopStartUp.getGameLoop() == null) {
            new GameLoopStartUp();
        } else {
            GameLoopStartUp.getGameLoop().setRunning(true);
        }
    }

    private void declareEntities() {
        double startY = 150 * DisplayConfig.relativeHeightRatio;
        double gap = 100 * DisplayConfig.relativeHeightRatio;

        File saveDir = new File(System.getProperty("user.dir"), "savefiles");

        if (!saveDir.exists()) {
            System.err.println("savefiles directory does not exist. Creating it...");

            boolean created = saveDir.mkdir();
            if (!created) {
                System.err.println("Failed to create folder: " + saveDir.getAbsolutePath());
            }
        }

        for (int i = 1; i <= 5; i++) {
            File saveFolder = new File(saveDir, "savefile_" + i);
            if (!saveFolder.exists()) {
                boolean created = saveFolder.mkdir();
                if (!created) {
                    System.err.println("Failed to create folder: " + saveFolder.getAbsolutePath());
                }
            }
        }

        if (saveDir.isDirectory()) {
            File[] allFiles = saveDir.listFiles();
            if (allFiles != null) {
                if (allFiles.length > 0) {
                    for (int i = 0; i < allFiles.length; i++) {
                        File folder = allFiles[i];
                        if (folder.isDirectory()) {
                            String folderName = "Save " + (i + 1);

                            ButtonEntity saveButton = new ButtonEntity(
                                    folderName,
                                    DisplayConfig.resolution.first() / 2 - 100 * DisplayConfig.relativeWidthRatio,
                                    startY + i * gap,
                                    200 * DisplayConfig.relativeWidthRatio,
                                    80 * DisplayConfig.relativeHeightRatio,
                                    () -> startLoad(folder)
                            );
                            if (folder.listFiles() == null || folder.listFiles().length == 0) {
                                saveButton.getComponent(ButtonComponent.class).getUIElement().setDisable(true);
                            }
                            saveButton.addStyleClass("main-menu-button");
                        }
                    }
                } else {
                    System.err.println("No save folders found in: " + saveDir.getAbsolutePath());
                }
            } else {
                System.err.println("savefiles is not a directory or is empty.");
            }
        } else {
            System.err.println("The path " + saveDir.getAbsolutePath() + " is not a directory.");
        }

        ButtonEntity exit = new ButtonEntity(
                "Back",
                DisplayConfig.resolution.first() / 2 - 100 * DisplayConfig.relativeWidthRatio,
                DisplayConfig.resolution.second() / 2 + 350 * DisplayConfig.relativeHeightRatio,
                200 * DisplayConfig.relativeWidthRatio,
                80 * DisplayConfig.relativeHeightRatio,
                () -> SystemHub.getInstance()
                        .getSystem(SceneManagementSystem.class)
                        .requestSceneChange(new MainScene(new BorderPane(), DisplayConfig.resolution.first(), DisplayConfig.resolution.second()))
        );
        exit.addStyleClass("main-menu-button");
    }

    private void startLoad(File saveFolder) {
        Path projectRoot = Paths.get(System.getProperty("user.dir"));

        Path savePath = saveFolder.toPath();

        Path relativePath = projectRoot.relativize(savePath);

        PlayerStats.currentSave = relativePath.toString().replace("\\", "/");
        GameSaver.loadEntityStats();
        GameSaver.loadShopPrices();
        GameSaver.loadTime();
    }

    @Override
    protected void systemStartUp() {
        SystemHub systemHub = SystemHub.getInstance();
        systemHub.addSystem(ResourceSystem.class, new ResourceSystem(), 1);
        systemHub.addSystem(CustomRenderSystem.class, new CustomRenderSystem(), 2);
    }

    @Override
    public void breakdown() {
        UtilityFunctions.defaultBreakdownMethod();
    }
}
