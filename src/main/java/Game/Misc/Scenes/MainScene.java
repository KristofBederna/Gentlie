package Game.Misc.Scenes;

import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Core.ResourceHub;
import inf.elte.hu.gameengine_javafx.Core.SystemHub;
import inf.elte.hu.gameengine_javafx.Entities.CameraEntity;
import inf.elte.hu.gameengine_javafx.Entities.UIEntities.ButtonEntity;
import inf.elte.hu.gameengine_javafx.Entities.UIEntities.LabelEntity;
import inf.elte.hu.gameengine_javafx.Misc.BackgroundMusic;
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
        new SystemStartUp(this::systemStartUp);
        new ResourceStartUp();

        BackgroundMusic music = new BackgroundMusic("/assets/sound/backgroundMusic/Waddle_Wars.wav", "Waddle_Wars", 1.0f, 0.0f, true);
        BackgroundMusic music2 = new BackgroundMusic("/assets/sound/backgroundMusic/Waddle_Wars_2.wav", "Waddle_Wars_2", 1.0f, 0.0f, true);
        BackgroundMusic music3 = new BackgroundMusic("/assets/sound/backgroundMusic/Waddle_Wars_3.wav", "Waddle_Wars_2", 1.0f, 0.0f, true);

        LabelEntity label = new LabelEntity("Gentile", (double) 1920 /2-20, (double) 1080 /2-250, 200, 0);

        ButtonEntity start = new ButtonEntity("Start Game", (double) 1920 /2-50, (double) 1080 /2-150, 200, 80, () -> SystemHub.getInstance().getSystem(SceneManagementSystem.class).requestSceneChange(new HomeScene(new BorderPane(), 1920, 1080)));
        ButtonEntity settings = new ButtonEntity("Settings", (double) 1920 /2-50, (double) 1080 /2-50, 200, 80, () -> System.out.println("Clicked"));
        ButtonEntity exit = new ButtonEntity("Exit", (double) 1920 /2-50, (double) 1080 /2+50, 200, 80, () -> System.exit(0));

        start.addStyleClass("main-menu-button");
        settings.addStyleClass("main-menu-button");
        exit.addStyleClass("main-menu-button");
        label.addStyleClass("main-menu-label");
        this.getRoot().getStyleClass().add("main-menu-scene");

        if (GameLoopStartUp.getGameLoop() == null) {
            new GameLoopStartUp();
        } else {
            GameLoopStartUp.getGameLoop().setRunning(true);
        }
    }

    private void systemStartUp() {
        //Define systems to be started up here
        SystemHub systemHub = SystemHub.getInstance();
        systemHub.addSystem(ResourceSystem.class, new ResourceSystem(),1);
        systemHub.addSystem(BackgroundMusicSystem.class, new BackgroundMusicSystem(), 2);
        systemHub.addSystem(RenderSystem.class, new RenderSystem(), 3);
    }

    @Override
    public void breakdown() {
        EntityHub.getInstance().unloadAll();
        EntityHub.resetInstance();
        CameraEntity.resetInstance();
        SystemHub.getInstance().shutDownSystems();
        GameLoopStartUp.stopGameLoop();
        ResourceHub.getInstance().clearResources();
        ResourceHub.resetInstance();
        this.getRoot().getStyleClass().clear();
        uiRoot.getInstance().getStylesheets().clear();
        uiRoot.getInstance().unloadAll();
        System.gc();
    }
}
