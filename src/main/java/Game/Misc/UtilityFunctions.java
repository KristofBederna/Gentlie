package Game.Misc;

import Game.Misc.Scenes.MainScene;
import inf.elte.hu.gameengine_javafx.Components.InteractiveComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.AccelerationComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.PlayerComponent;
import inf.elte.hu.gameengine_javafx.Components.UIComponents.ButtonComponent;
import inf.elte.hu.gameengine_javafx.Components.UIComponents.CheckBoxComponent;
import inf.elte.hu.gameengine_javafx.Components.UIComponents.LabelComponent;
import inf.elte.hu.gameengine_javafx.Components.UIComponents.SliderComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Core.ResourceHub;
import inf.elte.hu.gameengine_javafx.Core.SystemHub;
import inf.elte.hu.gameengine_javafx.Entities.CameraEntity;
import inf.elte.hu.gameengine_javafx.Entities.PlayerEntity;
import inf.elte.hu.gameengine_javafx.Entities.UIEntities.ButtonEntity;
import inf.elte.hu.gameengine_javafx.Entities.UIEntities.CheckBoxEntity;
import inf.elte.hu.gameengine_javafx.Entities.UIEntities.LabelEntity;
import inf.elte.hu.gameengine_javafx.Entities.UIEntities.SliderEntity;
import inf.elte.hu.gameengine_javafx.Entities.WorldEntity;
import inf.elte.hu.gameengine_javafx.Misc.BackgroundMusicStore;
import inf.elte.hu.gameengine_javafx.Misc.Configs.DisplayConfig;
import inf.elte.hu.gameengine_javafx.Misc.Configs.MapConfig;
import inf.elte.hu.gameengine_javafx.Misc.Configs.ResourceConfig;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.EventListeners.FullScreenToggleEventListener;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.Events.FullScreenToggleEvent;
import inf.elte.hu.gameengine_javafx.Misc.Layers.GameCanvas;
import inf.elte.hu.gameengine_javafx.Misc.Layers.uiRoot;
import inf.elte.hu.gameengine_javafx.Misc.SoundEffectStore;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.GameLoopStartUp;
import inf.elte.hu.gameengine_javafx.Misc.Time;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.SceneManagementSystem;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UtilityFunctions {
    private static VBox createSettingsMenu() {
        VBox menu = new VBox(20);
        menu.setAlignment(Pos.CENTER);

        ButtonEntity start = new ButtonEntity("Back to main menu",
                DisplayConfig.resolution.first() / 2 - 50,
                DisplayConfig.resolution.second() / 2 - 150,
                200,
                80,
                () -> {
                    uiRoot.getInstance().unload(menu);
                    SystemHub.getInstance().getSystem(SceneManagementSystem.class)
                            .requestSceneChange(new MainScene(new BorderPane(), DisplayConfig.resolution.first(), DisplayConfig.resolution.second()));
                    Time.getInstance().setTimeScale(1.0);
                });

        ButtonEntity settings = new ButtonEntity("Settings",
                DisplayConfig.resolution.first() / 2 - 50,
                DisplayConfig.resolution.second() / 2 - 50,
                200,
                80,
                () -> {
                    uiRoot.getInstance().unload(menu);
                    showDetailedSettingsMenu();
                });

        ButtonEntity exit = new ButtonEntity("Back",
                DisplayConfig.resolution.first() / 2 - 50,
                DisplayConfig.resolution.second() / 2 + 50,
                200,
                80,
                () -> {
                    uiRoot.getInstance().unload(menu);
                    Time.getInstance().setTimeScale(1.0);
                });

        Platform.runLater(() -> {
            menu.setPrefSize(DisplayConfig.resolution.first(), DisplayConfig.resolution.second());
            menu.getChildren().addAll(
                    start.getComponent(ButtonComponent.class).getNode(),
                    settings.getComponent(ButtonComponent.class).getNode(),
                    exit.getComponent(ButtonComponent.class).getNode()
            );
            uiRoot.getInstance().getChildren().add(menu);
        });

        return menu;
    }


    public static void showSettingsMenu(InteractiveComponent playerInteractiveComponent) {
        playerInteractiveComponent.mapInput(KeyCode.ESCAPE, 100, () -> {
            Time.getInstance().setTimeScale(0.0);
            createSettingsMenu();
        });
    }
    public static void showDetailedSettingsMenu() {
        VBox menu = new VBox(20);
        menu.setAlignment(Pos.CENTER);
        LabelEntity label = new LabelEntity("Settings", DisplayConfig.resolution.first() / 2 - 20, DisplayConfig.resolution.second() / 2 - 250, 200, 0);

        LabelEntity soundLabel = new LabelEntity("Master volume: ", DisplayConfig.resolution.first() / 2 - 200, DisplayConfig.resolution.second() / 2 - 150, 200, 0);
        SliderEntity sound = new SliderEntity(DisplayConfig.resolution.first() / 2 - 20, DisplayConfig.resolution.second() / 2 - 150, 200, 0, 0.0f, 1.0f, ResourceConfig.masterVolume);
        sound.getComponent(SliderComponent.class).getUIElement().valueProperty().addListener((obs, oldVal, newVal) -> {
            ResourceConfig.masterVolume = newVal.floatValue();
        });

        LabelEntity musicLabel = new LabelEntity("Music volume: ", DisplayConfig.resolution.first() / 2 - 200, DisplayConfig.resolution.second() / 2 - 100, 200, 0);
        SliderEntity music = new SliderEntity(DisplayConfig.resolution.first() / 2 - 20, DisplayConfig.resolution.second() / 2 - 100, 200, 0, 0.0f, 1.0f, ResourceConfig.backgroundMusicVolume);
        music.getComponent(SliderComponent.class).getUIElement().valueProperty().addListener((obs, oldVal, newVal) -> {
            ResourceConfig.backgroundMusicVolume = newVal.floatValue();
        });
        LabelEntity fullScreenLabel = new LabelEntity("Fullscreen mode: ", DisplayConfig.resolution.first() / 2 - 200, DisplayConfig.resolution.second() / 2, 200, 0);
        CheckBoxEntity fullscreen = new CheckBoxEntity("", DisplayConfig.resolution.first() / 2 - 20, DisplayConfig.resolution.second() / 2, 200, 0);
        CheckBox checkBox = fullscreen.getComponent(CheckBoxComponent.class).getUIElement();
        checkBox.setSelected(DisplayConfig.fullScreenMode);
        checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                DisplayConfig.fullScreenMode = newVal;
                Stage stage = (Stage) checkBox.getScene().getWindow();
                if (stage != null) {
                    new FullScreenToggleEventListener().onEvent(new FullScreenToggleEvent(stage));
                } else {
                    System.err.println("Stage is null");
                }
            }
        });

        ButtonEntity back = new ButtonEntity(
                "Back",
                DisplayConfig.resolution.first() / 2 - 100,
                DisplayConfig.resolution.second() / 2 + 350,
                200,
                80,
                () -> {
                    uiRoot.getInstance().unload(menu);
                    createSettingsMenu();
                }
        );

        Platform.runLater(() -> {
            menu.getChildren().addAll(
                    label.getComponent(LabelComponent.class).getNode(),
                    soundLabel.getComponent(LabelComponent.class).getNode(),
                    sound.getComponent(SliderComponent.class).getNode(),
                    musicLabel.getComponent(LabelComponent.class).getNode(),
                    music.getComponent(SliderComponent.class).getNode(),
                    fullScreenLabel.getComponent(LabelComponent.class).getNode(),
                    fullscreen.getComponent(CheckBoxComponent.class).getNode(),
                    back.getComponent(ButtonComponent.class).getNode()
            );
            menu.setPrefSize(DisplayConfig.resolution.first() / 6, DisplayConfig.resolution.second() / 4);
            menu.setLayoutX(DisplayConfig.resolution.first() / 2 - DisplayConfig.resolution.first() / 12);
            menu.setLayoutY(DisplayConfig.resolution.second() / 2 - DisplayConfig.resolution.first() / 8);
            uiRoot.getInstance().getChildren().add(menu);
        });
    }


    public static void setUpMovement(InteractiveComponent playerInteractiveComponent, PlayerEntity player) {
        setUpLeftRightMovement(playerInteractiveComponent, player);
        setUpDownUpMovement(playerInteractiveComponent, player);
    }

    public static void setUpLeftRightMovement(InteractiveComponent playerInteractiveComponent, PlayerEntity player) {
        playerInteractiveComponent.mapInput(KeyCode.A, 10, () -> inf.elte.hu.gameengine_javafx.Misc.UtilityFunctions.moveLeft(player), () -> inf.elte.hu.gameengine_javafx.Misc.UtilityFunctions.counterLeft(player));
        playerInteractiveComponent.mapInput(KeyCode.D, 10, () -> inf.elte.hu.gameengine_javafx.Misc.UtilityFunctions.moveRight(player), () -> inf.elte.hu.gameengine_javafx.Misc.UtilityFunctions.counterRight(player));
    }

    public static void setUpDownUpMovement(InteractiveComponent playerInteractiveComponent, PlayerEntity player) {
        playerInteractiveComponent.mapInput(KeyCode.W, 10, () -> inf.elte.hu.gameengine_javafx.Misc.UtilityFunctions.moveUp(player), () -> inf.elte.hu.gameengine_javafx.Misc.UtilityFunctions.counterUp(player));
        playerInteractiveComponent.mapInput(KeyCode.S, 10, () -> inf.elte.hu.gameengine_javafx.Misc.UtilityFunctions.moveDown(player), () -> inf.elte.hu.gameengine_javafx.Misc.UtilityFunctions.counterDown(player));
    }

    public static void moveUp(Entity e) {
        double dy = -4 * Time.getInstance().getDeltaTime();
        e.getComponent(AccelerationComponent.class).getAcceleration().setDy(dy);
    }

    public static void moveDown(Entity e) {
        double dy = 4 * Time.getInstance().getDeltaTime();
        e.getComponent(AccelerationComponent.class).getAcceleration().setDy(dy);
    }

    public static void moveLeft(Entity e) {
        double dx = -4 * Time.getInstance().getDeltaTime();
        e.getComponent(AccelerationComponent.class).getAcceleration().setDx(dx);
    }

    public static void moveRight(Entity e) {
        double dx = 4 * Time.getInstance().getDeltaTime();
        e.getComponent(AccelerationComponent.class).getAcceleration().setDx(dx);
    }

    public static void counterUp(Entity e) {
        e.getComponent(AccelerationComponent.class).getAcceleration().setDy(0);
    }

    public static void counterDown(Entity e) {
        e.getComponent(AccelerationComponent.class).getAcceleration().setDy(0);
    }

    public static void counterRight(Entity e) {
        e.getComponent(AccelerationComponent.class).getAcceleration().setDx(0);
    }

    public static void counterLeft(Entity e) {
        e.getComponent(AccelerationComponent.class).getAcceleration().setDx(0);
    }

    public static void setUpCamera(double width, double height, double worldWidth, double worldHeight) {
        CameraEntity.getInstance(width, height, worldWidth * MapConfig.scaledTileSize, worldHeight * MapConfig.scaledTileSize);
        CameraEntity.getInstance().attachTo(EntityHub.getInstance().getEntitiesWithComponent(PlayerComponent.class).getFirst());
    }

    public static void defaultBreakdownMethod() {
        EntityHub.getInstance().unloadAll();
        EntityHub.resetInstance();
        CameraEntity.resetInstance();
        WorldEntity.resetInstance();
        SystemHub.getInstance().shutDownSystems();
        GameLoopStartUp.stopGameLoop();
        ResourceHub.getInstance().clearResources();
        ResourceHub.resetInstance();
        uiRoot.getInstance().unloadAll();
        GameCanvas.getInstance().getGraphicsContext2D().clearRect(0, 0, GameCanvas.getInstance().getWidth(), GameCanvas.getInstance().getHeight());
        SoundEffectStore.resetInstance();
        BackgroundMusicStore.resetInstance();
        System.gc();
    }

    public static void showDetailedSettingsMenuWithoutBack() {
        LabelEntity label = new LabelEntity("Settings", DisplayConfig.resolution.first() / 2 - 20, DisplayConfig.resolution.second() / 2 - 250, 200, 0);

        LabelEntity soundLabel = new LabelEntity("Master volume: ", DisplayConfig.resolution.first() / 2 - 200, DisplayConfig.resolution.second() / 2 - 150, 200, 0);
        SliderEntity sound = new SliderEntity(DisplayConfig.resolution.first() / 2 - 20, DisplayConfig.resolution.second() / 2 - 150, 200, 0, 0.0f, 1.0f, ResourceConfig.masterVolume);
        sound.getComponent(SliderComponent.class).getUIElement().valueProperty().addListener((obs, oldVal, newVal) -> {
            ResourceConfig.masterVolume = newVal.floatValue();
        });

        LabelEntity musicLabel = new LabelEntity("Music volume: ", DisplayConfig.resolution.first() / 2 - 200, DisplayConfig.resolution.second() / 2 - 100, 200, 0);
        SliderEntity music = new SliderEntity(DisplayConfig.resolution.first() / 2 - 20, DisplayConfig.resolution.second() / 2 - 100, 200, 0, 0.0f, 1.0f, ResourceConfig.backgroundMusicVolume);
        music.getComponent(SliderComponent.class).getUIElement().valueProperty().addListener((obs, oldVal, newVal) -> {
            ResourceConfig.backgroundMusicVolume = newVal.floatValue();
        });
        LabelEntity fullScreenLabel = new LabelEntity("Fullscreen mode: ", DisplayConfig.resolution.first() / 2 - 200, DisplayConfig.resolution.second() / 2 - 50, 200, 0);
        CheckBoxEntity fullscreen = new CheckBoxEntity("", DisplayConfig.resolution.first() / 2 - 20, DisplayConfig.resolution.second() / 2 - 50, 200, 0);
        CheckBox checkBox = fullscreen.getComponent(CheckBoxComponent.class).getUIElement();
        checkBox.setSelected(DisplayConfig.fullScreenMode);
        checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                DisplayConfig.fullScreenMode = newVal;
                Stage stage = (Stage) checkBox.getScene().getWindow();
                if (stage != null) {
                    new FullScreenToggleEventListener().onEvent(new FullScreenToggleEvent(stage));
                } else {
                    System.err.println("Stage is null");
                }
            }
        });
    }
}
