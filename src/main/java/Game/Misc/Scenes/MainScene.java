package Game.Misc.Scenes;

import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Core.ResourceHub;
import inf.elte.hu.gameengine_javafx.Core.SystemHub;
import inf.elte.hu.gameengine_javafx.Entities.CameraEntity;
import inf.elte.hu.gameengine_javafx.Entities.UIEntities.ButtonEntity;
import inf.elte.hu.gameengine_javafx.Entities.UIEntities.LabelEntity;
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
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.ResourceSystem;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.SceneManagementSystem;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.SoundSystem;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.WorldLoaderSystem;
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

        LabelEntity label = new LabelEntity("Gentile", (double) 1920 /2-20, (double) 1080 /2-250, 200, 0);

        ButtonEntity start = new ButtonEntity("Start Game", (double) 1920 /2-50, (double) 1080 /2-150, 200, 80, () -> SystemHub.getInstance().getSystem(SceneManagementSystem.class).requestSceneChange(new HomeScene(new BorderPane(), 1920, 1080)));
        ButtonEntity settings = new ButtonEntity("Settings", (double) 1920 /2-50, (double) 1080 /2-50, 200, 80, () -> System.out.println("Clicked"));
        ButtonEntity exit = new ButtonEntity("Exit", (double) 1920 /2-50, (double) 1080 /2+50, 200, 80, () -> System.exit(0));

        start.addStyleClass("main-menu-button");
        settings.addStyleClass("main-menu-button");
        exit.addStyleClass("main-menu-button");
        label.addStyleClass("main-menu-label");
        this.getRoot().getStyleClass().add("main-menu-scene");

        new GameLoopStartUp();
    }

    private void systemStartUp() {
        //Define systems to be started up here
        SystemHub systemHub = SystemHub.getInstance();
        systemHub.addSystem(ResourceSystem.class, new ResourceSystem(),1);
        systemHub.addSystem(SoundSystem.class, new SoundSystem(), 2);
    }

    @Override
    public void breakdown() {
        EntityHub.getInstance().unloadAll();
        EntityHub.getInstance().removeAllEntityManagers();
        CameraEntity.resetInstance();
        SystemHub.getInstance().shutDownSystems();
        GameLoopStartUp.stopGameLoop();
        ResourceHub.getInstance().clearResources();
        uiRoot.getInstance().unloadAll();
    }
}
