package Game.Misc;

import Game.Misc.Scenes.MainScene;
import inf.elte.hu.gameengine_javafx.Components.InteractiveComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.AccelerationComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.PlayerComponent;
import inf.elte.hu.gameengine_javafx.Components.UIComponents.CheckBoxComponent;
import inf.elte.hu.gameengine_javafx.Components.UIComponents.ComboBoxComponent;
import inf.elte.hu.gameengine_javafx.Components.UIComponents.SliderComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Core.ResourceHub;
import inf.elte.hu.gameengine_javafx.Core.SystemHub;
import inf.elte.hu.gameengine_javafx.Entities.CameraEntity;
import inf.elte.hu.gameengine_javafx.Entities.PlayerEntity;
import inf.elte.hu.gameengine_javafx.Entities.UIEntities.*;
import inf.elte.hu.gameengine_javafx.Entities.WorldEntity;
import inf.elte.hu.gameengine_javafx.Misc.Configs.DisplayConfig;
import inf.elte.hu.gameengine_javafx.Misc.Configs.MapConfig;
import inf.elte.hu.gameengine_javafx.Misc.Configs.ResourceConfig;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.EventListeners.FullScreenToggleEventListener;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.EventListeners.ResolutionChangeEventListener;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.Events.FullScreenToggleEvent;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.Events.ResolutionChangeEvent;
import inf.elte.hu.gameengine_javafx.Misc.Layers.GameCanvas;
import inf.elte.hu.gameengine_javafx.Misc.Layers.uiRoot;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.GameLoopStartUp;
import inf.elte.hu.gameengine_javafx.Misc.Time;
import inf.elte.hu.gameengine_javafx.Misc.Tuple;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.SceneManagementSystem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class UtilityFunctions {
    public static void showSettingsMenu(InteractiveComponent playerInteractiveComponent) {
        playerInteractiveComponent.mapInput(KeyCode.ESCAPE, 100, () -> {
            Time.getInstance().setTimeScale(0.0);
            ButtonEntity start = new ButtonEntity("Back to main menu", DisplayConfig.resolution.first() / 2 - 50 * DisplayConfig.relativeWidthRatio, DisplayConfig.resolution.second() / 2 - 150 * DisplayConfig.relativeHeightRatio, 200 * DisplayConfig.relativeWidthRatio, 80 * DisplayConfig.relativeHeightRatio, () -> {
                SystemHub.getInstance().getSystem(SceneManagementSystem.class).requestSceneChange(new MainScene(new BorderPane(), DisplayConfig.resolution.first(), DisplayConfig.resolution.second()));
                Time.getInstance().setTimeScale(1.0);
            });
            ButtonEntity settings = new ButtonEntity("Settings", DisplayConfig.resolution.first() / 2 - 50 * DisplayConfig.relativeWidthRatio, DisplayConfig.resolution.second() / 2 - 50 * DisplayConfig.relativeHeightRatio, 200 * DisplayConfig.relativeWidthRatio, 80 * DisplayConfig.relativeHeightRatio, () -> {
                showDetailedSettingsMenu();
                backButton();
            });
            ButtonEntity exit = new ButtonEntity("Back", DisplayConfig.resolution.first() / 2 - 50 * DisplayConfig.relativeWidthRatio, DisplayConfig.resolution.second() / 2 + 50 * DisplayConfig.relativeHeightRatio, 200 * DisplayConfig.relativeWidthRatio, 80 * DisplayConfig.relativeHeightRatio, () -> {
                uiRoot.getInstance().unloadAll();
                Time.getInstance().setTimeScale(1.0);
            });
        });
    }

    public static void showDetailedSettingsMenu() {
        uiRoot.getInstance().unloadAll();
        LabelEntity label = new LabelEntity("Settings", DisplayConfig.resolution.first() / 2 - 20 * DisplayConfig.relativeWidthRatio, DisplayConfig.resolution.second() / 2 - 250 * DisplayConfig.relativeHeightRatio, 200 * DisplayConfig.relativeWidthRatio, 0);

        LabelEntity soundLabel = new LabelEntity("Master volume: ", DisplayConfig.resolution.first() / 2 - 200 * DisplayConfig.relativeWidthRatio, DisplayConfig.resolution.second() / 2 - 150 * DisplayConfig.relativeHeightRatio, 200 * DisplayConfig.relativeWidthRatio, 0);
        SliderEntity sound = new SliderEntity(DisplayConfig.resolution.first() / 2 - 20 * DisplayConfig.relativeWidthRatio, DisplayConfig.resolution.second() / 2 - 150 * DisplayConfig.relativeHeightRatio, 200 * DisplayConfig.relativeWidthRatio, 0, 0.0f, 1.0f, ResourceConfig.masterVolume);

        Slider slider2 = sound.getComponent(SliderComponent.class).getUIElement();
        slider2.valueProperty().addListener((observable, oldValue, newValue) -> {
            ResourceConfig.masterVolume = newValue.floatValue();
        });

        LabelEntity musicLabel = new LabelEntity("Music volume: ", DisplayConfig.resolution.first() / 2 - 200 * DisplayConfig.relativeWidthRatio, DisplayConfig.resolution.second() / 2 - 100 * DisplayConfig.relativeHeightRatio, 200 * DisplayConfig.relativeWidthRatio, 0);
        SliderEntity music = new SliderEntity(DisplayConfig.resolution.first() / 2 - 20 * DisplayConfig.relativeWidthRatio, DisplayConfig.resolution.second() / 2 - 100 * DisplayConfig.relativeHeightRatio, 200 * DisplayConfig.relativeWidthRatio, 0, 0.0f, 1.0f, ResourceConfig.backgroundMusicVolume);

        Slider slider = music.getComponent(SliderComponent.class).getUIElement();
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            ResourceConfig.backgroundMusicVolume = newValue.floatValue();
        });

        ObservableList<Tuple<Double, Double>> options = FXCollections.observableArrayList();
        options.add(new Tuple<>(600.0, 800.0));
        options.add(new Tuple<>(1024.0, 768.0));
        options.add(new Tuple<>(1280.0, 720.0));
        options.add(new Tuple<>(1440.0, 900.0));
        options.add(new Tuple<>(1920.0, 1080.0));
        options.add(new Tuple<>(2560.0, 1440.0));
        LabelEntity resolutionLabel = new LabelEntity("Resolution: ", DisplayConfig.resolution.first() / 2 - 200 * DisplayConfig.relativeWidthRatio, DisplayConfig.resolution.second() / 2 - 50 * DisplayConfig.relativeHeightRatio, 200 * DisplayConfig.relativeWidthRatio, 0);
        ComboBoxEntity<Tuple<Double, Double>> resolution = new ComboBoxEntity<>(new ComboBoxComponent<>(DisplayConfig.resolution.first() / 2 - 20 * DisplayConfig.relativeWidthRatio, DisplayConfig.resolution.second() / 2 - 50 * DisplayConfig.relativeHeightRatio, 200 * DisplayConfig.relativeWidthRatio, 0, options));

        ComboBox<Tuple<Double, Double>> comboBox = (ComboBox<Tuple<Double, Double>>) resolution.getComponent(ComboBoxComponent.class).getUIElement();

        comboBox.setValue(DisplayConfig.resolution);

        comboBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Tuple<Double, Double> item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.first().intValue() + "x" + item.second().intValue());
                }
            }
        });

        comboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Tuple<Double, Double> item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.first().intValue() + "x" + item.second().intValue());
                }
            }
        });

        comboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                DisplayConfig.resolution = new Tuple<>(newVal.first(), newVal.second());
                new ResolutionChangeEventListener().onEvent(new ResolutionChangeEvent(newVal.first(), newVal.second()));
            }
        });

        LabelEntity fullScreenLabel = new LabelEntity("Fullscreen mode: ", DisplayConfig.resolution.first() / 2 - 200 * DisplayConfig.relativeWidthRatio, DisplayConfig.resolution.second() / 2, 200 * DisplayConfig.relativeWidthRatio, 0);
        CheckBoxEntity fullscreen = new CheckBoxEntity("", DisplayConfig.resolution.first() / 2 - 20 * DisplayConfig.relativeWidthRatio, DisplayConfig.resolution.second() / 2, 200 * DisplayConfig.relativeWidthRatio, 0);

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

    public static void backButton() {
        ButtonEntity back = new ButtonEntity("Back", DisplayConfig.resolution.first() / 2 - 50 * DisplayConfig.relativeWidthRatio, DisplayConfig.resolution.second() / 2 + 350 * DisplayConfig.relativeHeightRatio, 200 * DisplayConfig.relativeWidthRatio, 80 * DisplayConfig.relativeHeightRatio, () -> {
            uiRoot.getInstance().unloadAll();
            ButtonEntity start = new ButtonEntity("Back to main menu", DisplayConfig.resolution.first() / 2 - 50 * DisplayConfig.relativeWidthRatio, DisplayConfig.resolution.second() / 2 - 150 * DisplayConfig.relativeHeightRatio, 200 * DisplayConfig.relativeWidthRatio, 80 * DisplayConfig.relativeHeightRatio, () -> {
                SystemHub.getInstance().getSystem(SceneManagementSystem.class).requestSceneChange(new MainScene(new BorderPane(), DisplayConfig.resolution.first(), DisplayConfig.resolution.second()));
                Time.getInstance().setTimeScale(1.0);
            });
            ButtonEntity settings = new ButtonEntity("Settings", DisplayConfig.resolution.first() / 2 - 50 * DisplayConfig.relativeWidthRatio, DisplayConfig.resolution.second() / 2 - 50 * DisplayConfig.relativeHeightRatio, 200 * DisplayConfig.relativeWidthRatio, 80 * DisplayConfig.relativeHeightRatio, () -> {
                showDetailedSettingsMenu();
                backButton();
            });
            ButtonEntity exit = new ButtonEntity("Back", DisplayConfig.resolution.first() / 2 - 50 * DisplayConfig.relativeWidthRatio, DisplayConfig.resolution.second() / 2 + 50 * DisplayConfig.relativeHeightRatio, 200 * DisplayConfig.relativeWidthRatio, 80 * DisplayConfig.relativeHeightRatio, () -> {
                uiRoot.getInstance().unloadAll();
                Time.getInstance().setTimeScale(1.0);
            });
        });
    }

    public static void setUpMovement(InteractiveComponent playerInteractiveComponent, PlayerEntity player) {
        setUpLeftRightMovement(playerInteractiveComponent, player);
        setUpDownUpMovement(playerInteractiveComponent, player);
    }

    public static void setUpLeftRightMovement(InteractiveComponent playerInteractiveComponent, PlayerEntity player) {
        playerInteractiveComponent.mapInput(KeyCode.LEFT, 10, () -> UtilityFunctions.moveLeft(player), () -> UtilityFunctions.counterLeft(player));
        playerInteractiveComponent.mapInput(KeyCode.RIGHT, 10, () -> UtilityFunctions.moveRight(player), () -> UtilityFunctions.counterRight(player));
    }

    public static void setUpDownUpMovement(InteractiveComponent playerInteractiveComponent, PlayerEntity player) {
        playerInteractiveComponent.mapInput(KeyCode.UP, 10, () -> UtilityFunctions.moveUp(player), () -> UtilityFunctions.counterUp(player));
        playerInteractiveComponent.mapInput(KeyCode.DOWN, 10, () -> UtilityFunctions.moveDown(player), () -> UtilityFunctions.counterDown(player));
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
        System.gc();
    }
}
