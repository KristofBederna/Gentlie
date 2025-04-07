package inf.elte.hu.gameengine_javafx.Misc.Scenes;

import inf.elte.hu.gameengine_javafx.Components.InteractiveComponent;
import inf.elte.hu.gameengine_javafx.Components.PathfindingComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.AccelerationComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.PlayerComponent;
import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.StateComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.MapMeshComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.WorldDimensionComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Core.EntityManager;
import inf.elte.hu.gameengine_javafx.Core.ResourceHub;
import inf.elte.hu.gameengine_javafx.Core.SystemHub;
import inf.elte.hu.gameengine_javafx.Entities.*;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Rectangle;
import inf.elte.hu.gameengine_javafx.Misc.*;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.EventListeners.FullScreenToggleEventListener;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.EventListeners.ResolutionChangeEventListener;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.EventListeners.TestEventListener;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.EventManager;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.Events.FullScreenToggleEvent;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.Events.ResolutionChangeEvent;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.Events.TestEvent;
import inf.elte.hu.gameengine_javafx.Misc.InputHandlers.MouseInputHandler;
import inf.elte.hu.gameengine_javafx.Misc.Layers.uiRoot;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.GameLoopStartUp;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.ResourceStartUp;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.SystemStartUp;
import inf.elte.hu.gameengine_javafx.Systems.InputHandlingSystem;
import inf.elte.hu.gameengine_javafx.Systems.PathfindingSystem;
import inf.elte.hu.gameengine_javafx.Systems.PhysicsSystems.CollisionSystem;
import inf.elte.hu.gameengine_javafx.Systems.PhysicsSystems.MovementSystem;
import inf.elte.hu.gameengine_javafx.Systems.RenderingSystems.*;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.*;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.Random;


public class TestScene extends GameScene {
    public TestScene(Parent parent, double width, double height) {
        super(parent, width, height);
        setup();
    }

    @Override
    public void setup() {
        getStylesheets().add(Objects.requireNonNull(getClass().getResource("/assets/styles/styles.css")).toExternalForm());
        new ResourceStartUp();
        WorldEntity.getInstance("/assets/maps/hardForAIMap.txt", "/assets/tileSets/testTiles.txt");
        entitySetup();
        cameraSetup();
        interactionSetup();
        new SystemStartUp(this::SystemStartUp);
        new GameLoopStartUp();
    }

    @Override
    public void breakdown() {
        EntityHub entityHub = EntityHub.getInstance();
        entityHub.removeEntityManager(TileEntity.class);
        entityHub.removeEntityManager(DummyEntity.class);
        EntityManager<TileEntity> tileEntityManager = entityHub.getEntityManager(TileEntity.class);
        if (tileEntityManager != null) {
            tileEntityManager.unloadAll();
        }
        EntityManager<DummyEntity> dummyEntityManager = entityHub.getEntityManager(DummyEntity.class);
        if (dummyEntityManager != null) {
            dummyEntityManager.unloadAll();
        }
        EntityManager<PlayerEntity> playerEntityManager = entityHub.getEntityManager(PlayerEntity.class);
        if (playerEntityManager != null) {
            playerEntityManager.unloadAll();
        }
        WorldEntity.resetInstance();
        if (EntityHub.getInstance().getEntitiesWithComponent(PlayerComponent.class).getFirst() != null) {
            InteractiveComponent interactiveComponent = EntityHub.getInstance().getEntitiesWithComponent(PlayerComponent.class).getFirst().getComponent(InteractiveComponent.class);
            if (interactiveComponent != null) {
                interactiveComponent.clearMappings();
            }
        }
        CameraEntity.resetInstance();
        SystemHub.getInstance().shutDownSystems();
        GameLoopStartUp.stopGameLoop();
        ResourceHub.getInstance().clearResources();
        uiRoot.getInstance().unloadAll();
    }

    private void SystemStartUp() {
        //Define systems to be started up here
        SystemHub systemHub = SystemHub.getInstance();
        systemHub.addSystem(AnimationSystem.class, new AnimationSystem(), 1);
        systemHub.addSystem(RenderSystem.class, new RenderSystem(), 2);
        systemHub.addSystem(LightingSystem.class, new LightingSystem(), 3);
        systemHub.addSystem(PathfindingSystem.class, new PathfindingSystem(), 4);
        systemHub.addSystem(MovementSystem.class, new MovementSystem(), 5);
        systemHub.addSystem(ParticleSystem.class, new ParticleSystem(), 6);
        systemHub.addSystem(InputHandlingSystem.class, new InputHandlingSystem(), 7);
        systemHub.addSystem(CollisionSystem.class, new CollisionSystem(), 8);
        systemHub.addSystem(ResourceSystem.class, new ResourceSystem(), 9);
        systemHub.addSystem(CameraSystem.class, new CameraSystem(), 10);
        systemHub.addSystem(SoundSystem.class, new SoundSystem(), 11);
        systemHub.addSystem(DynamicWorldLoaderSystem.class, new DynamicWorldLoaderSystem(2, 2), 12);
    }

    private void cameraSetup() {
        CameraEntity.getInstance(Config.resolution.first(), Config.resolution.second(), WorldEntity.getInstance().getComponent(WorldDimensionComponent.class).getWorldWidth() * Config.scaledTileSize, WorldEntity.getInstance().getComponent(WorldDimensionComponent.class).getWorldHeight() * Config.scaledTileSize);
        CameraEntity.getInstance().attachTo(EntityHub.getInstance().getEntitiesWithComponent(PlayerComponent.class).getFirst());
    }

    private void entitySetup() {
        new PlayerEntity(420, 120, "idle", "/assets/images/PlayerIdle.png", 0.8 * Config.scaledTileSize, 0.8 * Config.scaledTileSize);
        new DummyEntity(220, 220, "idle", "/assets/images/PlayerIdle.png", 0.8 * Config.scaledTileSize, 0.8 * Config.scaledTileSize);
        new ParticleEmitterEntity(400, 400, new ParticleEntity(0, 0, 2, 2, new Rectangle(new Point(0, 0), 2, 2), Color.ORANGE, Color.TRANSPARENT, 300), Direction.RIGHT, 50, 100);
        new ParticleEmitterEntity(5*Config.scaledTileSize, 500, new ParticleEntity(0, 0, 20, 20, "/assets/images/snowflake.png", 2000), Direction.ALL, 20, 1000);
        new LightingEntity(250, 250, LightType.POINT, 0.01, Color.YELLOW, 100, 100);
        new LightingEntity(1050, 550, LightType.POINT, 0.01, Color.YELLOW, 100, 100);
        new LightingEntity(750, 650, LightType.POINT, 0.01, Color.YELLOW, 100, 100);
        new LightingEntity(250, 550, LightType.POINT, 0.01, Color.YELLOW, 100, 100);
    }

    private void interactionSetup() {
        PlayerEntity player = (PlayerEntity) EntityHub.getInstance().getEntitiesWithComponent(PlayerComponent.class).getFirst();

        DummyEntity entity2 = (DummyEntity) EntityHub.getInstance().getEntitiesWithType(DummyEntity.class).getFirst();

        InteractiveComponent playerInteractiveComponent = player.getComponent(InteractiveComponent.class);
        playerInteractiveComponent.mapInput(KeyCode.UP, 10, () -> moveUp(player), () -> counterUp(player));
        playerInteractiveComponent.mapInput(KeyCode.DOWN, 10, () -> moveDown(player), () -> counterDown(player));
        playerInteractiveComponent.mapInput(KeyCode.LEFT, 10, () -> moveLeft(player), () -> counterLeft(player));
        playerInteractiveComponent.mapInput(KeyCode.RIGHT, 10, () -> moveRight(player), () -> counterRight(player));
        playerInteractiveComponent.mapInput(MouseButton.PRIMARY, 100, () -> {
            player.getComponent(PositionComponent.class).setLocalX(MouseInputHandler.getInstance().getMouseX(), player);
            player.getComponent(PositionComponent.class).setLocalY(MouseInputHandler.getInstance().getMouseY(), player);
        });
        //playerInteractiveComponent.mapInput(MouseButton.PRIMARY, () -> System.out.println(MouseInputHandler.getInstance().getMouseX() + " " + MouseInputHandler.getInstance().getMouseY()));
        playerInteractiveComponent.mapInput(MouseButton.SECONDARY, 400, () -> new SoundEffect(player, "/assets/sound/sfx/explosion.wav", "explosion", 1f, 0f, 1000, false), () -> SoundEffectStore.getInstance().remove("explosion"));
        playerInteractiveComponent.mapInput(KeyCode.F2, 10, () -> CameraEntity.getInstance().attachTo(entity2), () -> CameraEntity.getInstance().attachTo(player));
        playerInteractiveComponent.mapInput(KeyCode.F3, 100, () -> SystemHub.getInstance().getSystem(SceneManagementSystem.class).requestSceneChange(new Test2Scene(new BorderPane(), 1920, 1080)));

        EventManager eventManager = new EventManager();
        eventManager.registerListener(TestEvent.class, new TestEventListener());
        eventManager.registerListener(FullScreenToggleEvent.class, new FullScreenToggleEventListener());
        eventManager.registerListener(ResolutionChangeEvent.class, new ResolutionChangeEventListener());

        playerInteractiveComponent.mapInput(KeyCode.E, 100, () -> eventManager.fireEvent(new TestEvent()));

        playerInteractiveComponent.mapInput(KeyCode.F11, 100, () -> eventManager.fireEvent(new FullScreenToggleEvent((Stage) getWindow())));

        playerInteractiveComponent.mapInput(KeyCode.F10, 100, () -> eventManager.fireEvent(new ResolutionChangeEvent(1280, 720)));
        playerInteractiveComponent.mapInput(KeyCode.F12, 100, () -> eventManager.fireEvent(new ResolutionChangeEvent(1920, 1080)));

        playerInteractiveComponent.mapInput(KeyCode.F4, 1000, () -> {
            Random random = new Random();
            Point target = null;
            while (target == null) {
                target = WorldEntity.getInstance().getComponent(MapMeshComponent.class).getMapCoordinates().get(random.nextInt(32)).get(random.nextInt(32));
            }
            PathfindingComponent pathfinding = entity2.getComponent(PathfindingComponent.class);
            if (pathfinding != null && pathfinding.getEnd() != null) {
                pathfinding.setEnd(target);
                pathfinding.resetPathing(entity2);
            }
        });
    }


    private void moveUp(Entity e) {
        double dy = -4 * Time.getInstance().getDeltaTime();
        e.getComponent(AccelerationComponent.class).getAcceleration().setDy(dy);
        e.getComponent(StateComponent.class).setCurrentState("up");
    }

    private void moveDown(Entity e) {
        double dy = 4 * Time.getInstance().getDeltaTime();
        e.getComponent(AccelerationComponent.class).getAcceleration().setDy(dy);
        e.getComponent(StateComponent.class).setCurrentState("down");
    }

    private void moveLeft(Entity e) {
        double dx = -4 * Time.getInstance().getDeltaTime();
        e.getComponent(AccelerationComponent.class).getAcceleration().setDx(dx);
        e.getComponent(StateComponent.class).setCurrentState("left");
    }

    private void moveRight(Entity e) {
        double dx = 4 * Time.getInstance().getDeltaTime();
        e.getComponent(AccelerationComponent.class).getAcceleration().setDx(dx);
        e.getComponent(StateComponent.class).setCurrentState("right");
    }

    private void counterUp(Entity e) {
        e.getComponent(AccelerationComponent.class).getAcceleration().setDy(0);
        e.getComponent(StateComponent.class).setCurrentState("idle");
    }

    private void counterDown(Entity e) {
        e.getComponent(AccelerationComponent.class).getAcceleration().setDy(0);
        e.getComponent(StateComponent.class).setCurrentState("idle");
    }

    private void counterRight(Entity e) {
        e.getComponent(AccelerationComponent.class).getAcceleration().setDx(0);
        e.getComponent(StateComponent.class).setCurrentState("idle");
    }

    private void counterLeft(Entity e) {
        e.getComponent(AccelerationComponent.class).getAcceleration().setDx(0);
        e.getComponent(StateComponent.class).setCurrentState("idle");
    }
}
