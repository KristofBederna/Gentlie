package Game.Misc.Scenes;

import inf.elte.hu.gameengine_javafx.Components.UIComponents.CheckBoxComponent;
import inf.elte.hu.gameengine_javafx.Components.UIComponents.ComboBoxComponent;
import inf.elte.hu.gameengine_javafx.Components.UIComponents.SliderComponent;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Core.ResourceHub;
import inf.elte.hu.gameengine_javafx.Core.SystemHub;
import inf.elte.hu.gameengine_javafx.Entities.CameraEntity;
import inf.elte.hu.gameengine_javafx.Entities.UIEntities.*;
import inf.elte.hu.gameengine_javafx.Entities.WorldEntity;
import inf.elte.hu.gameengine_javafx.Misc.BackgroundMusic;
import inf.elte.hu.gameengine_javafx.Misc.Config;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.EventListeners.FullScreenToggleEventListener;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.EventListeners.ResolutionChangeEventListener;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.Events.FullScreenToggleEvent;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.Events.ResolutionChangeEvent;
import inf.elte.hu.gameengine_javafx.Misc.Layers.GameCanvas;
import inf.elte.hu.gameengine_javafx.Misc.Layers.uiRoot;
import inf.elte.hu.gameengine_javafx.Misc.Scenes.GameScene;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.GameLoopStartUp;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.ResourceStartUp;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.SystemStartUp;
import inf.elte.hu.gameengine_javafx.Misc.Tuple;
import inf.elte.hu.gameengine_javafx.Systems.RenderingSystems.RenderSystem;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.BackgroundMusicSystem;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.ResourceSystem;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.SceneManagementSystem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.Slider;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.Objects;

public class SettingsScene extends GameScene {
    /**
     * Constructs a new {@code GameScene} with the specified parent node, width, and height.
     *
     * @param parent The root node of the scene.
     * @param width  The width of the scene in pixels.
     * @param height The height of the scene in pixels.
     */
    public SettingsScene(Parent parent, double width, double height) {
        super(parent, width, height);
    }

    @Override
    public void setup() {
        getStylesheets().add(Objects.requireNonNull(getClass().getResource("/assets/styles/mainMenu.css")).toExternalForm());
        new SystemStartUp(this::systemStartUp);
        new ResourceStartUp();

        LabelEntity label = new LabelEntity("Settings", Config.resolution.first()/2 - 20*Config.relativeWidthRatio, Config.resolution.second()/2 - 250*Config.relativeHeightRatio, 200*Config.relativeWidthRatio, 0);

        ButtonEntity exit = new ButtonEntity("Back", Config.resolution.first()/2 - 50*Config.relativeWidthRatio, Config.resolution.second()/2 + 350*Config.relativeHeightRatio, 200*Config.relativeWidthRatio, 80*Config.relativeHeightRatio, () -> SystemHub.getInstance().getSystem(SceneManagementSystem.class).requestSceneChange(new MainScene(new BorderPane(), Config.resolution.first(), Config.resolution.second())));

        LabelEntity soundLabel = new LabelEntity("Master volume: ", Config.resolution.first()/2 - 200*Config.relativeWidthRatio, Config.resolution.second()/2 - 150*Config.relativeHeightRatio, 200*Config.relativeWidthRatio, 0);
        SliderEntity sound = new SliderEntity(Config.resolution.first()/2 - 20*Config.relativeWidthRatio, Config.resolution.second()/2 - 150*Config.relativeHeightRatio, 200*Config.relativeWidthRatio, 0, 0.0f, 1.0f, Config.masterVolume);

        Slider slider2 = sound.getComponent(SliderComponent.class).getUIElement();
        slider2.valueProperty().addListener((observable, oldValue, newValue) -> {
            Config.masterVolume = newValue.floatValue();
        });

        LabelEntity musicLabel = new LabelEntity("Music volume: ", Config.resolution.first()/2 - 200*Config.relativeWidthRatio, Config.resolution.second()/2 - 100*Config.relativeHeightRatio, 200*Config.relativeWidthRatio, 0);
        SliderEntity music = new SliderEntity(Config.resolution.first()/2 - 20*Config.relativeWidthRatio, Config.resolution.second()/2 - 100*Config.relativeHeightRatio, 200*Config.relativeWidthRatio, 0, 0.0f, 1.0f, Config.backgroundMusicVolume);

        Slider slider = music.getComponent(SliderComponent.class).getUIElement();
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            Config.backgroundMusicVolume = newValue.floatValue();
        });

        ObservableList<Tuple<Double, Double>> options = FXCollections.observableArrayList();
        options.add(new Tuple<>(600.0, 800.0));
        options.add(new Tuple<>(1024.0, 768.0));
        options.add(new Tuple<>(1280.0, 720.0));
        options.add(new Tuple<>(1440.0, 900.0));
        options.add(new Tuple<>(1920.0, 1080.0));
        options.add(new Tuple<>(2560.0, 1440.0));
        LabelEntity resolutionLabel = new LabelEntity("Resolution: ", Config.resolution.first()/2 - 200*Config.relativeWidthRatio, Config.resolution.second()/2 - 50*Config.relativeHeightRatio, 200*Config.relativeWidthRatio, 0);
        ComboBoxEntity<Tuple<Double,Double>> resolution = new ComboBoxEntity<>(new ComboBoxComponent<>(Config.resolution.first()/2 - 20*Config.relativeWidthRatio, Config.resolution.second()/2 - 50*Config.relativeHeightRatio, 200*Config.relativeWidthRatio, 0, options));

        ComboBox<Tuple<Double, Double>> comboBox = (ComboBox<Tuple<Double, Double>>) resolution.getComponent(ComboBoxComponent.class).getUIElement();

        comboBox.setValue(Config.resolution);

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
                Config.resolution = new Tuple<>(newVal.first(), newVal.second());
                new ResolutionChangeEventListener().onEvent(new ResolutionChangeEvent(newVal.first(), newVal.second()));
            }
        });

        LabelEntity fullScreenLabel = new LabelEntity("Fullscreen mode: ", Config.resolution.first()/2 - 200*Config.relativeWidthRatio, Config.resolution.second()/2, 200*Config.relativeWidthRatio, 0);
        CheckBoxEntity fullscreen = new CheckBoxEntity("",Config.resolution.first()/2 - 20*Config.relativeWidthRatio, Config.resolution.second()/2, 200*Config.relativeWidthRatio, 0);

        CheckBox checkBox = fullscreen.getComponent(CheckBoxComponent.class).getUIElement();

        checkBox.setSelected(Config.fullScreenMode);

        checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                Config.fullScreenMode = newVal;

                Stage stage = (Stage) checkBox.getScene().getWindow();

                if (stage != null) {
                    new FullScreenToggleEventListener().onEvent(new FullScreenToggleEvent(stage));
                } else {
                    System.err.println("Stage is null");
                }
            }
        });


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
        systemHub.addSystem(RenderSystem.class, new RenderSystem(), 2);
    }

    @Override
    public void breakdown() {
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
