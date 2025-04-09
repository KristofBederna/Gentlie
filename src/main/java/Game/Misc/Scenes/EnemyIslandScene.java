package Game.Misc.Scenes;

import Game.Entities.*;
import Game.Entities.Labels.DungeonLabel;
import Game.Entities.Labels.GoHomeLabel;
import Game.Entities.Labels.GoldLabel;
import Game.Entities.Labels.HealthLabel;
import Game.Misc.EventHandling.EventListeners.EnterDungeonEventListener;
import Game.Misc.EventHandling.EventListeners.GoHomeEventListener;
import Game.Misc.EventHandling.Events.EnterDungeonEvent;
import Game.Misc.EventHandling.Events.GoHomeEvent;
import Game.Misc.PlayerStats;
import Game.Misc.UtilityFunctions;
import Game.Systems.DayNightCycleSystem;
import Game.Systems.EventTileSystem;
import Game.Systems.UserInterfaceSystem;
import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.InteractiveComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.PlayerComponent;
import inf.elte.hu.gameengine_javafx.Components.UIComponents.LabelComponent;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Core.SystemHub;
import inf.elte.hu.gameengine_javafx.Entities.ParticleEmitterEntity;
import inf.elte.hu.gameengine_javafx.Entities.ParticleEntity;
import inf.elte.hu.gameengine_javafx.Entities.PlayerEntity;
import inf.elte.hu.gameengine_javafx.Entities.WorldEntity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Misc.Config;
import inf.elte.hu.gameengine_javafx.Misc.Direction;
import inf.elte.hu.gameengine_javafx.Misc.InputHandlers.MouseInputHandler;
import inf.elte.hu.gameengine_javafx.Misc.Scenes.GameScene;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.GameLoopStartUp;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.ResourceStartUp;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.SystemStartUp;
import inf.elte.hu.gameengine_javafx.Systems.InputHandlingSystem;
import inf.elte.hu.gameengine_javafx.Systems.PathfindingSystem;
import inf.elte.hu.gameengine_javafx.Systems.PhysicsSystems.CollisionSystem;
import inf.elte.hu.gameengine_javafx.Systems.PhysicsSystems.MovementDeterminerSystem;
import inf.elte.hu.gameengine_javafx.Systems.PhysicsSystems.MovementSystem;
import inf.elte.hu.gameengine_javafx.Systems.RenderingSystems.AnimationSystem;
import inf.elte.hu.gameengine_javafx.Systems.RenderingSystems.CameraSystem;
import inf.elte.hu.gameengine_javafx.Systems.RenderingSystems.ParticleSystem;
import inf.elte.hu.gameengine_javafx.Systems.RenderingSystems.RenderSystem;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.ResourceSystem;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.SoundSystem;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.WorldLoaderSystem;
import javafx.scene.Parent;
import javafx.scene.input.MouseButton;
import javafx.scene.text.TextAlignment;

import java.util.List;

public class EnemyIslandScene extends GameScene {
    Point spawn;

    /**
     * Constructs a new {@code GameScene} with the specified parent node, width, and height.
     *
     * @param parent The root node of the scene.
     * @param width  The width of the scene in pixels.
     * @param height The height of the scene in pixels.
     */
    public EnemyIslandScene(Parent parent, double width, double height, Point spawn) {
        super(parent, width, height);
        this.spawn = spawn;
    }

    @Override
    public void setup() {
        Config.wallTiles = List.of(3, 5, 6, 7);
        Config.setTileScale(1.5);

        declareEntities();

        UtilityFunctions.setUpCamera(1920, 1080, 16, 16);

        new SystemStartUp(this::SystemStartUp);

        interactionSetup();

        GameLoopStartUp.restartLoop();
    }

    private void declareEntities() {
        new ResourceStartUp();
        WorldEntity.getInstance("/assets/maps/enemyIsland.txt", "/assets/tileSets/gameTileSet.txt");

        new PlayerEntity(spawn.getX(), spawn.getY(), "idle", "/assets/images/Gentlie/Gentlie_Down_Idle.png", Config.scaledTileSize * 0.75 * 0.55, Config.scaledTileSize * 0.75);
        new GoldLabel(String.valueOf(PlayerStats.gold), 100, 100, 100, 100);
        new HealthLabel(String.valueOf(PlayerStats.health), 100, 200, 100, 100);
        new WaterEntity();
        new SkyBoxEntity();

        new IglooEntity(6*Config.scaledTileSize, Config.scaledTileSize, 2*Config.scaledTileSize, 2*Config.scaledTileSize);
        new ShipEntity(0, 1.5*Config.scaledTileSize, 2*Config.scaledTileSize, 2*Config.scaledTileSize);

        new ParticleEmitterEntity(0, -300, new ParticleEntity(0, 0, 20, 20, "/assets/images/snowflake.png", 3000), Direction.RIGHT, 1, 60);
        new ParticleEmitterEntity(2400, -300, new ParticleEntity(0, 0, 20, 20, "/assets/images/snowflake.png", 3000), Direction.LEFT, 1, 60);

        DungeonLabel dungeonLabel = new DungeonLabel("Press 'E' to enter the dungeon", 6* Config.scaledTileSize, 3*Config.scaledTileSize, Config.scaledTileSize * 0.75, Config.scaledTileSize * 0.75);
        dungeonLabel.removeFromUI();
        dungeonLabel.getComponent(LabelComponent.class).getUIElement().setTextAlignment(TextAlignment.CENTER);

        GoHomeLabel goHomeLabel = new GoHomeLabel("Press 'E' to go home", 2* Config.scaledTileSize, 3*Config.scaledTileSize, Config.scaledTileSize * 0.75, Config.scaledTileSize * 0.75);
        goHomeLabel.removeFromUI();
        goHomeLabel.getComponent(LabelComponent.class).getUIElement().setTextAlignment(TextAlignment.CENTER);

        new EventTriggerEntity(7* Config.scaledTileSize -Config.scaledTileSize *0.25-1, 2*Config.scaledTileSize +Config.scaledTileSize *0.25-1, Config.scaledTileSize * 0.75, Config.scaledTileSize * 0.75, new EnterDungeonEvent(), new EnterDungeonEventListener());
        new EventTriggerEntity(2*Config.scaledTileSize, 2*Config.scaledTileSize+Config.scaledTileSize/2, Config.scaledTileSize/2, Config.scaledTileSize /2, new GoHomeEvent(), new GoHomeEventListener());
    }

    private void SystemStartUp() {
        //Define systems to be started up here
        SystemHub systemHub = SystemHub.getInstance();
        systemHub.addSystem(MovementDeterminerSystem.class, new MovementDeterminerSystem(),0);
        systemHub.addSystem(EventTileSystem.class, new EventTileSystem(),1);
        systemHub.addSystem(AnimationSystem.class, new AnimationSystem(), 2);
        systemHub.addSystem(DayNightCycleSystem.class, new DayNightCycleSystem(),3);
        systemHub.addSystem(PathfindingSystem.class, new PathfindingSystem(), 4);
        systemHub.addSystem(MovementSystem.class, new MovementSystem(), 5);
        systemHub.addSystem(ParticleSystem.class, new ParticleSystem(), 6);
        systemHub.addSystem(InputHandlingSystem.class, new InputHandlingSystem(), 7);
        systemHub.addSystem(CollisionSystem.class, new CollisionSystem(), 8);
        systemHub.addSystem(ResourceSystem.class, new ResourceSystem(), 9);
        systemHub.addSystem(CameraSystem.class, new CameraSystem(), 10);
        systemHub.addSystem(SoundSystem.class, new SoundSystem(), 11);
        systemHub.addSystem(WorldLoaderSystem.class, new WorldLoaderSystem(), 12);
        systemHub.addSystem(UserInterfaceSystem.class, new UserInterfaceSystem(), 13);
        systemHub.addSystem(RenderSystem.class, new RenderSystem(), 14);
    }

    private void interactionSetup() {
        PlayerEntity player = (PlayerEntity)EntityHub.getInstance().getEntitiesWithComponent(PlayerComponent.class).getFirst();
        InteractiveComponent playerInteractiveComponent = player.getComponent(InteractiveComponent.class);

        UtilityFunctions.setUpLeftRightMovement(playerInteractiveComponent, player);
        UtilityFunctions.showSettingsMenu(playerInteractiveComponent);

        playerInteractiveComponent.mapInput(MouseButton.PRIMARY, 100, () -> {player.getComponent(PositionComponent.class).setLocalX(MouseInputHandler.getInstance().getMouseX(), player); player.getComponent(PositionComponent.class).setLocalY(MouseInputHandler.getInstance().getMouseY(), player);});
    }

    @Override
    public void breakdown() {
        UtilityFunctions.defaultBreakdownMethod();
    }
}
