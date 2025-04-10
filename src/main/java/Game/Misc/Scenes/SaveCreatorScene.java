package Game.Misc.Scenes;

import Game.Misc.UtilityFunctions;
import inf.elte.hu.gameengine_javafx.Core.SystemHub;
import inf.elte.hu.gameengine_javafx.Entities.UIEntities.ButtonEntity;
import inf.elte.hu.gameengine_javafx.Entities.UIEntities.LabelEntity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Misc.Config;
import inf.elte.hu.gameengine_javafx.Misc.Scenes.GameScene;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.GameLoopStartUp;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.ResourceStartUp;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.SystemStartUp;
import inf.elte.hu.gameengine_javafx.Systems.RenderingSystems.RenderSystem;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.ResourceSystem;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.SceneManagementSystem;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.io.File;
import java.util.Objects;

public class SaveCreatorScene extends GameScene {

    public SaveCreatorScene(Parent parent, double width, double height) {
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
        double startY = 150 * Config.relativeHeightRatio;
        double gap = 100 * Config.relativeHeightRatio;

        File saveDir = new File(System.getProperty("user.dir"), "src/main/java/Game/savefiles");

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
                                    Config.resolution.first() / 2 - 50 * Config.relativeWidthRatio,
                                    startY + i * gap,
                                    200 * Config.relativeWidthRatio,
                                    80 * Config.relativeHeightRatio,
                                    () -> startLoad(folder)
                            );
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
                Config.resolution.first() / 2 - 50 * Config.relativeWidthRatio,
                Config.resolution.second() / 2 + 350 * Config.relativeHeightRatio,
                200 * Config.relativeWidthRatio,
                80 * Config.relativeHeightRatio,
                () -> SystemHub.getInstance()
                        .getSystem(SceneManagementSystem.class)
                        .requestSceneChange(new MainScene(new BorderPane(), Config.resolution.first(), Config.resolution.second()))
        );
        exit.addStyleClass("main-menu-button");
    }

    private void startLoad(File saveFolder) {
        LabelEntity label = new LabelEntity("Are you sure you want to overwrite the save file at " + saveFolder.getName() + "?",
                Config.resolution.first() / 2 - 350 * Config.relativeWidthRatio,
                Config.resolution.second() / 2 + 175 * Config.relativeHeightRatio,
                200 * Config.relativeWidthRatio,
                80 * Config.relativeHeightRatio);
        ButtonEntity yes = new ButtonEntity("Yes",
                Config.resolution.first() / 2 - 175 * Config.relativeWidthRatio,
                Config.resolution.second() / 2 + 265 * Config.relativeHeightRatio,
                200 * Config.relativeWidthRatio,
                80 * Config.relativeHeightRatio,
                () -> {
                    SystemHub.getInstance().getSystem(SceneManagementSystem.class).requestSceneChange(new HomeScene(new BorderPane(), Config.resolution.first(), Config.resolution.second(), new Point(10 * 100 + 100 / 2, 3 * 100)));
                });
        ButtonEntity no = new ButtonEntity("No",
                Config.resolution.first() / 2 + 75 * Config.relativeWidthRatio,
                Config.resolution.second() / 2 + 265 * Config.relativeHeightRatio,
                200 * Config.relativeWidthRatio,
                80 * Config.relativeHeightRatio,
                () -> {
                    SystemHub.getInstance().getSystem(SceneManagementSystem.class).requestSceneChange(new SaveCreatorScene(new BorderPane(), Config.resolution.first(), Config.resolution.second()));
                });
        label.addStyleClass("main-menu-label");
        yes.addStyleClass("main-menu-button");
        no.addStyleClass("main-menu-button");
        // SaveManager.loadSaveFromFolder(saveFolder);
    }

    private void systemStartUp() {
        SystemHub systemHub = SystemHub.getInstance();
        systemHub.addSystem(ResourceSystem.class, new ResourceSystem(), 1);
        systemHub.addSystem(RenderSystem.class, new RenderSystem(), 2);
    }

    @Override
    public void breakdown() {
        UtilityFunctions.defaultBreakdownMethod();
    }
}
