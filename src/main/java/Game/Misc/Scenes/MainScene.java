package Game.Misc.Scenes;

import Game.Misc.UtilityFunctions;
import Game.Systems.CustomRenderSystem;
import inf.elte.hu.gameengine_javafx.Core.SystemHub;
import inf.elte.hu.gameengine_javafx.Entities.UIEntities.ButtonEntity;
import inf.elte.hu.gameengine_javafx.Entities.UIEntities.LabelEntity;
import inf.elte.hu.gameengine_javafx.Misc.BackgroundMusic;
import inf.elte.hu.gameengine_javafx.Misc.BackgroundMusicStore;
import inf.elte.hu.gameengine_javafx.Misc.Configs.DisplayConfig;
import inf.elte.hu.gameengine_javafx.Misc.Scenes.GameScene;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.GameLoopStartUp;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.ResourceStartUp;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.SystemStartUp;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.BackgroundMusicSystem;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.ResourceSystem;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.SceneManagementSystem;
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
        LabelEntity label = new LabelEntity("GENTLIE", DisplayConfig.resolution.first() / 2 - 70 * DisplayConfig.relativeWidthRatio, DisplayConfig.resolution.second() / 2 - 250 * DisplayConfig.relativeHeightRatio, 200 * DisplayConfig.relativeWidthRatio, 0);

        ButtonEntity start = new ButtonEntity("Start Game", DisplayConfig.resolution.first() / 2 - 100 * DisplayConfig.relativeWidthRatio, DisplayConfig.resolution.second() / 2 - 150 * DisplayConfig.relativeHeightRatio, 200 * DisplayConfig.relativeWidthRatio, 80 * DisplayConfig.relativeHeightRatio, () -> SystemHub.getInstance().getSystem(SceneManagementSystem.class).requestSceneChange(new SaveCreatorScene(new BorderPane(), DisplayConfig.resolution.first(), DisplayConfig.resolution.second())));
        ButtonEntity continueGame = new ButtonEntity("Continue Game", DisplayConfig.resolution.first() / 2 - 100 * DisplayConfig.relativeWidthRatio, DisplayConfig.resolution.second() / 2 - 50 * DisplayConfig.relativeHeightRatio, 200 * DisplayConfig.relativeWidthRatio, 80 * DisplayConfig.relativeHeightRatio, () -> SystemHub.getInstance().getSystem(SceneManagementSystem.class).requestSceneChange(new LoadSelectorScene(new BorderPane(), DisplayConfig.resolution.first(), DisplayConfig.resolution.second())));
        ButtonEntity settings = new ButtonEntity("Settings", DisplayConfig.resolution.first() / 2 - 100 * DisplayConfig.relativeWidthRatio, DisplayConfig.resolution.second() / 2 + 50 * DisplayConfig.relativeHeightRatio, 200 * DisplayConfig.relativeWidthRatio, 80 * DisplayConfig.relativeHeightRatio, () -> SystemHub.getInstance().getSystem(SceneManagementSystem.class).requestSceneChange(new SettingsScene(new BorderPane(), DisplayConfig.resolution.first(), DisplayConfig.resolution.second())));
        ButtonEntity exit = new ButtonEntity("Exit", DisplayConfig.resolution.first() / 2 - 100 * DisplayConfig.relativeWidthRatio, DisplayConfig.resolution.second() / 2 + 150 * DisplayConfig.relativeHeightRatio, 200 * DisplayConfig.relativeWidthRatio, 80 * DisplayConfig.relativeHeightRatio, () -> System.exit(0));

        start.addStyleClass("main-menu-button");
        continueGame.addStyleClass("main-menu-button");
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

    @Override
    protected void systemStartUp() {
        //Define systems to be started up here
        SystemHub systemHub = SystemHub.getInstance();
        systemHub.addSystem(ResourceSystem.class, new ResourceSystem(),1);
        if (systemHub.getSystem(BackgroundMusicSystem.class) == null) {
            systemHub.addSystem(BackgroundMusicSystem.class, new BackgroundMusicSystem(), 2);
        }
        systemHub.addSystem(CustomRenderSystem.class, new CustomRenderSystem(), 3);
    }

    @Override
    public void breakdown() {
        UtilityFunctions.defaultBreakdownMethod();
    }
}
