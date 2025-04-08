package Game.Misc.Scenes;

import Game.Misc.UtilityFunctions;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Core.ResourceHub;
import inf.elte.hu.gameengine_javafx.Core.SystemHub;
import inf.elte.hu.gameengine_javafx.Entities.CameraEntity;
import inf.elte.hu.gameengine_javafx.Entities.UIEntities.ButtonEntity;
import inf.elte.hu.gameengine_javafx.Entities.UIEntities.LabelEntity;
import inf.elte.hu.gameengine_javafx.Entities.WorldEntity;
import inf.elte.hu.gameengine_javafx.Misc.BackgroundMusic;
import inf.elte.hu.gameengine_javafx.Misc.BackgroundMusicStore;
import inf.elte.hu.gameengine_javafx.Misc.Config;
import inf.elte.hu.gameengine_javafx.Misc.Layers.GameCanvas;
import inf.elte.hu.gameengine_javafx.Misc.Layers.GameLayer;
import inf.elte.hu.gameengine_javafx.Misc.Layers.uiRoot;
import inf.elte.hu.gameengine_javafx.Misc.Scenes.GameScene;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.GameLoopStartUp;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.ResourceStartUp;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.SystemStartUp;
import inf.elte.hu.gameengine_javafx.Systems.InputHandlingSystem;
import inf.elte.hu.gameengine_javafx.Systems.PathfindingSystem;
import inf.elte.hu.gameengine_javafx.Systems.PhysicsSystems.CollisionSystem;
import inf.elte.hu.gameengine_javafx.Systems.PhysicsSystems.MovementSystem;
import inf.elte.hu.gameengine_javafx.Systems.RenderingSystems.*;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.*;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.util.Objects;

public class MainScene extends GameScene {
    public MainScene(Parent parent, double width, double height) {
        super(parent, width, height);
        setup();
    }

    @Override
    public void setup() {
        getStylesheets().add(Objects.requireNonNull(getClass().getResource("/assets/styles/mainMenu.css")).toExternalForm());
        new ResourceStartUp();

        loadBackgroundMusic();

        declareEntities();

        new SystemStartUp(this::systemStartUp);

        this.getRoot().getStyleClass().add("main-menu-scene");

        if (GameLoopStartUp.getGameLoop() == null) {
            new GameLoopStartUp();
        } else {
            GameLoopStartUp.getGameLoop().setRunning(true);
        }
    }

    private void declareEntities() {
        LabelEntity label = new LabelEntity("Gentile", Config.resolution.first()/2 - 20*Config.relativeWidthRatio, Config.resolution.second()/2 - 250*Config.relativeHeightRatio, 200*Config.relativeWidthRatio, 0);

        ButtonEntity start = new ButtonEntity("Start Game", Config.resolution.first()/2 - 50*Config.relativeWidthRatio, Config.resolution.second()/2 - 150*Config.relativeHeightRatio, 200*Config.relativeWidthRatio, 80*Config.relativeHeightRatio, () -> SystemHub.getInstance().getSystem(SceneManagementSystem.class).requestSceneChange(new HomeScene(new BorderPane(), Config.resolution.first(), Config.resolution.second())));
        ButtonEntity settings = new ButtonEntity("Settings", Config.resolution.first()/2 - 50*Config.relativeWidthRatio, Config.resolution.second()/2 - 50*Config.relativeHeightRatio, 200*Config.relativeWidthRatio, 80*Config.relativeHeightRatio, () -> SystemHub.getInstance().getSystem(SceneManagementSystem.class).requestSceneChange(new SettingsScene(new BorderPane(), Config.resolution.first(), Config.resolution.second())));
        ButtonEntity exit = new ButtonEntity("Exit", Config.resolution.first()/2 - 50*Config.relativeWidthRatio, Config.resolution.second()/2 + 50*Config.relativeHeightRatio, 200*Config.relativeWidthRatio, 80*Config.relativeHeightRatio, () -> System.exit(0));

        start.addStyleClass("main-menu-button");
        settings.addStyleClass("main-menu-button");
        exit.addStyleClass("main-menu-button");
        label.addStyleClass("main-menu-label");
    }

    private void loadBackgroundMusic() {
        if (BackgroundMusicStore.getInstance().getBackgroundMusics().isEmpty()) {
            new BackgroundMusic("/assets/sound/backgroundMusic/Waddle_Wars.wav", "Waddle_Wars", 1.0f, 0.0f, true);
            new BackgroundMusic("/assets/sound/backgroundMusic/Waddle_Wars_2.wav", "Waddle_Wars_2", 1.0f, 0.0f, true);
            new BackgroundMusic("/assets/sound/backgroundMusic/Waddle_Wars_3.wav", "Waddle_Wars_3", 1.0f, 0.0f, true);
        }
    }

    private void systemStartUp() {
        //Define systems to be started up here
        SystemHub systemHub = SystemHub.getInstance();
        systemHub.addSystem(ResourceSystem.class, new ResourceSystem(),1);
        if (systemHub.getSystem(BackgroundMusicSystem.class) == null) {
            systemHub.addSystem(BackgroundMusicSystem.class, new BackgroundMusicSystem(), 2);
        }
        systemHub.addSystem(RenderSystem.class, new RenderSystem(), 3);
    }

    @Override
    public void breakdown() {
        UtilityFunctions.defaultBreakdownMethod();
    }
}
