package Game.Misc.Scenes;

import Game.Entities.*;
import Game.Entities.Labels.*;
import Game.Misc.EventHandling.EventListeners.EnterEnemyIslandEventListener;
import Game.Misc.EventHandling.EventListeners.EnterHomeEventListener;
import Game.Misc.EventHandling.EventListeners.EnterInnListener;
import Game.Misc.EventHandling.Events.EnterEnemyIslandEvent;
import Game.Misc.EventHandling.Events.EnterHomeEvent;
import Game.Misc.EventHandling.Events.EnterInnEvent;
import Game.Misc.PlayerStats;
import Game.Misc.UtilityFunctions;
import Game.Systems.*;
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
import inf.elte.hu.gameengine_javafx.Misc.Configs.MapConfig;
import inf.elte.hu.gameengine_javafx.Misc.Direction;
import inf.elte.hu.gameengine_javafx.Misc.Scenes.GameScene;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.GameLoopStartUp;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.ResourceStartUp;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.SystemStartUp;
import inf.elte.hu.gameengine_javafx.Systems.InputHandlingSystem;
import inf.elte.hu.gameengine_javafx.Systems.PathfindingSystems.PathfindingSystem;
import inf.elte.hu.gameengine_javafx.Systems.PhysicsSystems.CollisionSystem;
import inf.elte.hu.gameengine_javafx.Systems.PhysicsSystems.MovementDeterminerSystem;
import inf.elte.hu.gameengine_javafx.Systems.PhysicsSystems.MovementSystem;
import inf.elte.hu.gameengine_javafx.Systems.RenderingSystems.AnimationSystem;
import inf.elte.hu.gameengine_javafx.Systems.RenderingSystems.CameraSystem;
import inf.elte.hu.gameengine_javafx.Systems.RenderingSystems.ParticleSystem;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.MapLoaderSystems.WorldLoaderSystem;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.ResourceSystem;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.SoundSystems.SoundSystem;
import javafx.scene.Parent;
import javafx.scene.text.TextAlignment;

import java.util.List;
import java.util.Random;

public class HomeIslandScene extends GameScene {
    Point spawn;

    /**
     * Constructs a new {@code GameScene} with the specified parent node, width, and height.
     *
     * @param parent The root node of the scene.
     * @param width  The width of the scene in pixels.
     * @param height The height of the scene in pixels.
     */
    public HomeIslandScene(Parent parent, double width, double height, Point spawn) {
        super(parent, width, height);
        this.spawn = spawn;
    }

    @Override
    public void setup() {
        MapConfig.wallTiles = List.of(3, 5, 6, 7);
        MapConfig.setTileScale(1.5);

        new ResourceStartUp();
        WorldEntity.getInstance("/assets/maps/homeIsland.txt", "/assets/tileSets/gameTileSet.txt");

        declareEntities();

        UtilityFunctions.setUpCamera(1920, 1080, 16, 16);

        new SystemStartUp(this::systemStartUp);

        interactionSetup();

        GameLoopStartUp.restartLoop();
    }

    private void declareEntities() {
        new PlayerEntity(spawn.getX(), spawn.getY(), "idle", "/assets/images/Gentlie/Gentlie_Down_1.png", MapConfig.scaledTileSize * 0.75 * 0.55, MapConfig.scaledTileSize * 0.75);

        Random random = new Random();
        new PenguinEntity(random.nextInt(2, 10) * MapConfig.scaledTileSize, 2 * MapConfig.scaledTileSize, "idle", "/assets/images/Penguins/Penguin_Down_1.png", MapConfig.scaledTileSize * 0.55, MapConfig.scaledTileSize);
        new PenguinEntity(random.nextInt(2, 10) * MapConfig.scaledTileSize, 2 * MapConfig.scaledTileSize, "idle", "/assets/images/Penguins/Penguin_Down_1.png", MapConfig.scaledTileSize * 0.55, MapConfig.scaledTileSize);
        new PenguinEntity(random.nextInt(2, 10) * MapConfig.scaledTileSize, 2 * MapConfig.scaledTileSize, "idle", "/assets/images/Penguins/Penguin_Down_1.png", MapConfig.scaledTileSize * 0.55, MapConfig.scaledTileSize);
        new GoldLabel(String.valueOf(PlayerStats.gold), 100, 100, 100, 100);
        new HealthLabel(String.format("%.0f", PlayerStats.health), 100, 200, 100, 100);
        new WaterEntity();
        new SkyBoxEntity();

        new IglooEntity(6 * MapConfig.scaledTileSize, MapConfig.scaledTileSize, 2 * MapConfig.scaledTileSize, 2 * MapConfig.scaledTileSize);
        new IglooEntity(8.5 * MapConfig.scaledTileSize, 0.5*MapConfig.scaledTileSize, 2.5 * MapConfig.scaledTileSize, 2.5 * MapConfig.scaledTileSize);
        new IglooEntity(11.5 * MapConfig.scaledTileSize, 1.5*MapConfig.scaledTileSize, 1.5 * MapConfig.scaledTileSize, 1.5 * MapConfig.scaledTileSize);
        new ShipEntity(14 * MapConfig.scaledTileSize, 1.5 * MapConfig.scaledTileSize, 2 * MapConfig.scaledTileSize, 2 * MapConfig.scaledTileSize);
        new InnEntity(2 * MapConfig.scaledTileSize, MapConfig.scaledTileSize, 2 * MapConfig.scaledTileSize, 2 * MapConfig.scaledTileSize);

        new ParticleEmitterEntity(1200, -1500, new ParticleEntity(0, 0, 20, 20, "/assets/images/snowflake.png", 3000), Direction.DOWN, 1, 60);

        EnterHomeLabel homeLabel = new EnterHomeLabel("Press 'E' to enter your home", 6 * MapConfig.scaledTileSize, 3 * MapConfig.scaledTileSize, MapConfig.scaledTileSize * 0.75, MapConfig.scaledTileSize * 0.75);
        homeLabel.removeFromUI();
        homeLabel.getComponent(LabelComponent.class).getUIElement().setTextAlignment(TextAlignment.CENTER);

        EnterInnLabel innLabel = new EnterInnLabel("Press 'E' to enter the inn", 3 * MapConfig.scaledTileSize, 3 * MapConfig.scaledTileSize, MapConfig.scaledTileSize * 0.75, MapConfig.scaledTileSize * 0.75);
        innLabel.removeFromUI();
        innLabel.getComponent(LabelComponent.class).getUIElement().setTextAlignment(TextAlignment.CENTER);

        EnterEnemyIslandLabel enterEnemyIslandLabel = new EnterEnemyIslandLabel("Press 'E' to go on an adventure", 10 * MapConfig.scaledTileSize - 0.3 * MapConfig.scaledTileSize, 3 * MapConfig.scaledTileSize, MapConfig.scaledTileSize * 0.75, MapConfig.scaledTileSize * 0.75);
        enterEnemyIslandLabel.removeFromUI();
        enterEnemyIslandLabel.getComponent(LabelComponent.class).getUIElement().setTextAlignment(TextAlignment.CENTER);


        new EventTriggerEntity(7 * MapConfig.scaledTileSize - MapConfig.scaledTileSize * 0.25 - 1, 2 * MapConfig.scaledTileSize + MapConfig.scaledTileSize * 0.25 - 1, MapConfig.scaledTileSize * 0.75, MapConfig.scaledTileSize * 0.75, new EnterHomeEvent(new Point(6 * MapConfig.scaledTileSize + MapConfig.scaledTileSize * 0.33, 8 * MapConfig.scaledTileSize)), new EnterHomeEventListener());
        new EventTriggerEntity(2 * MapConfig.scaledTileSize + MapConfig.scaledTileSize * 0.25 - 1, 2 * MapConfig.scaledTileSize + MapConfig.scaledTileSize * 0.25 - 1, MapConfig.scaledTileSize * 0.75, MapConfig.scaledTileSize * 0.75, new EnterInnEvent(), new EnterInnListener());
        new EventTriggerEntity(14 * MapConfig.scaledTileSize, 2.5 * MapConfig.scaledTileSize, 2 * MapConfig.scaledTileSize, MapConfig.scaledTileSize / 2, new EnterEnemyIslandEvent(new Point(3 * MapConfig.scaledTileSize, 2 * MapConfig.scaledTileSize + MapConfig.scaledTileSize * 0.25 - 1)), new EnterEnemyIslandEventListener());
    }

    @Override
    protected void systemStartUp() {
        //Define systems to be started up here
        SystemHub systemHub = SystemHub.getInstance();
        systemHub.addSystem(MovementDeterminerSystem.class, new MovementDeterminerSystem(),0);
        systemHub.addSystem(EventTileSystem.class, new EventTileSystem(),1);
        systemHub.addSystem(AnimationSystem.class, new AnimationSystem(), 2);
        systemHub.addSystem(DayNightCycleSystem.class, new DayNightCycleSystem(), 3);
        systemHub.addSystem(PathfindingSystem.class, new PathfindingSystem(), 4);
        systemHub.addSystem(PenguinMoverSystem.class, new PenguinMoverSystem(),5);
        systemHub.addSystem(MovementSystem.class, new MovementSystem(), 6);
        systemHub.addSystem(ParticleSystem.class, new ParticleSystem(), 7);
        systemHub.addSystem(InputHandlingSystem.class, new InputHandlingSystem(), 8);
        systemHub.addSystem(CollisionSystem.class, new CollisionSystem(), 9);
        systemHub.addSystem(ResourceSystem.class, new ResourceSystem(), 10);
        systemHub.addSystem(CameraSystem.class, new CameraSystem(), 11);
        systemHub.addSystem(SoundSystem.class, new SoundSystem(), 12);
        systemHub.addSystem(WorldLoaderSystem.class, new WorldLoaderSystem(), 13);
        systemHub.addSystem(UserInterfaceSystem.class, new UserInterfaceSystem(), 14);
        systemHub.addSystem(CustomRenderSystem.class, new CustomRenderSystem(), 15);
        systemHub.addSystem(GameSaverSystem.class, new GameSaverSystem(), 16);
        systemHub.addSystem(StepSoundEffectSystem.class, new StepSoundEffectSystem(), 17);
    }

    private void interactionSetup() {
        PlayerEntity player = (PlayerEntity)EntityHub.getInstance().getEntitiesWithComponent(PlayerComponent.class).getFirst();
        InteractiveComponent playerInteractiveComponent = player.getComponent(InteractiveComponent.class);

        UtilityFunctions.setUpLeftRightMovement(playerInteractiveComponent, player);
        UtilityFunctions.showSettingsMenu(playerInteractiveComponent);
    }

    @Override
    public void breakdown() {
        UtilityFunctions.defaultBreakdownMethod();
    }
}
