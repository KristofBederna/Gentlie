package Game.Misc.Scenes;

import Game.Misc.UtilityFunctions;
import Game.Systems.CustomRenderSystem;
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

        declareEntities();

        this.getRoot().getStyleClass().add("main-menu-scene");

        if (GameLoopStartUp.getGameLoop() == null) {
            new GameLoopStartUp();
        } else {
            GameLoopStartUp.getGameLoop().setRunning(true);
        }
    }

    private void declareEntities() {
        UtilityFunctions.showDetailedSettingsMenuWithoutBack();
        ButtonEntity exit = new ButtonEntity("Back", DisplayConfig.resolution.first() / 2 - 100 * DisplayConfig.relativeWidthRatio, DisplayConfig.resolution.second() / 2 + 350 * DisplayConfig.relativeHeightRatio, 200 * DisplayConfig.relativeWidthRatio, 80 * DisplayConfig.relativeHeightRatio, () -> SystemHub.getInstance().getSystem(SceneManagementSystem.class).requestSceneChange(new MainScene(new BorderPane(), DisplayConfig.resolution.first(), DisplayConfig.resolution.second())));

        exit.addStyleClass("main-menu-button");
    }

    @Override
    protected void systemStartUp() {
        //Define systems to be started up here
        SystemHub systemHub = SystemHub.getInstance();
        systemHub.addSystem(ResourceSystem.class, new ResourceSystem(),1);
        systemHub.addSystem(CustomRenderSystem.class, new CustomRenderSystem(), 2);
    }

    @Override
    public void breakdown() {
        UtilityFunctions.defaultBreakdownMethod();
    }
}
