package Game.Misc.Scenes;

import Game.Entities.*;
import Game.Entities.Labels.EnterEnemyIslandLabel;
import Game.Entities.Labels.EnterHomeLabel;
import Game.Entities.Labels.EnterInnLabel;
import Game.Misc.EventHandling.EventListeners.EnterEnemyIslandEventListener;
import Game.Misc.EventHandling.EventListeners.EnterHomeEventListener;
import Game.Misc.EventHandling.EventListeners.EnterInnListener;
import Game.Misc.EventHandling.Events.EnterEnemyIslandEvent;
import Game.Misc.EventHandling.Events.EnterHomeEvent;
import Game.Misc.EventHandling.Events.EnterInnEvent;
import Game.Systems.DayNightCycleSystem;
import Game.Systems.EventTileSystem;
import Game.Systems.PenguinMoverSystem;
import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.InteractiveComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.AccelerationComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.PlayerComponent;
import inf.elte.hu.gameengine_javafx.Components.UIComponents.LabelComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Core.ResourceHub;
import inf.elte.hu.gameengine_javafx.Core.SystemHub;
import inf.elte.hu.gameengine_javafx.Entities.*;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Misc.Config;
import inf.elte.hu.gameengine_javafx.Misc.Direction;
import inf.elte.hu.gameengine_javafx.Misc.InputHandlers.MouseInputHandler;
import inf.elte.hu.gameengine_javafx.Misc.Layers.uiRoot;
import inf.elte.hu.gameengine_javafx.Misc.Scenes.GameScene;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.GameLoopStartUp;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.ResourceStartUp;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.SystemStartUp;
import inf.elte.hu.gameengine_javafx.Misc.Time;
import inf.elte.hu.gameengine_javafx.Systems.InputHandlingSystem;
import inf.elte.hu.gameengine_javafx.Systems.PhysicsSystems.CollisionSystem;
import inf.elte.hu.gameengine_javafx.Systems.PhysicsSystems.MovementDeterminerSystem;
import inf.elte.hu.gameengine_javafx.Systems.PhysicsSystems.MovementSystem;
import inf.elte.hu.gameengine_javafx.Systems.PlatformerPathfindingSystem;
import inf.elte.hu.gameengine_javafx.Systems.RenderingSystems.*;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.ResourceSystem;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.SoundSystem;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.WorldLoaderSystem;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
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
        Config.wallTiles = List.of(3, 5, 6, 7);
        Config.setTileScale(1.5);

        new ResourceStartUp();
        WorldEntity.getInstance("/assets/maps/homeIsland.txt", "/assets/tileSets/gameTileSet.txt");

        new PlayerEntity(spawn.getX(), spawn.getY(), "idle", "/assets/images/Gentlie/Gentlie_Down_1.png",  Config.scaledTileSize * 0.75 * 0.55,  Config.scaledTileSize * 0.75);

        Random random = new Random();
        new PenguinEntity(random.nextInt(2, 10)*Config.scaledTileSize, 2*Config.scaledTileSize, "idle", "/assets/images/Penguins/Penguin_Down_1.png",  Config.scaledTileSize*0.55,  Config.scaledTileSize);
        new PenguinEntity(random.nextInt(2, 10)*Config.scaledTileSize, 2*Config.scaledTileSize, "idle", "/assets/images/Penguins/Penguin_Down_1.png",  Config.scaledTileSize*0.55,  Config.scaledTileSize);
        new PenguinEntity(random.nextInt(2, 10)*Config.scaledTileSize, 2*Config.scaledTileSize, "idle", "/assets/images/Penguins/Penguin_Down_1.png",  Config.scaledTileSize*0.55,  Config.scaledTileSize);

        new WaterEntity();
        new SkyBoxEntity();

        new IglooEntity(6*Config.scaledTileSize, Config.scaledTileSize, 2*Config.scaledTileSize, 2*Config.scaledTileSize);
        new ShipEntity(14*Config.scaledTileSize, 1.5*Config.scaledTileSize, 2*Config.scaledTileSize, 2*Config.scaledTileSize);
        new InnEntity(2*Config.scaledTileSize, Config.scaledTileSize, 2*Config.scaledTileSize, 2*Config.scaledTileSize);

        new ParticleEmitterEntity(0, -300, new ParticleEntity(0, 0, 20, 20, "/assets/images/snowflake.png", 3000), Direction.RIGHT, 1, 60);
        new ParticleEmitterEntity(2400, -300, new ParticleEntity(0, 0, 20, 20, "/assets/images/snowflake.png", 3000), Direction.LEFT, 1, 60);


        EnterHomeLabel homeLabel = new EnterHomeLabel("Press 'E' to enter your home", 6* Config.scaledTileSize, 3*Config.scaledTileSize, Config.scaledTileSize * 0.75, Config.scaledTileSize * 0.75);
        homeLabel.removeFromUI();
        homeLabel.getComponent(LabelComponent.class).getUIElement().setTextAlignment(TextAlignment.CENTER);

        EnterInnLabel innLabel = new EnterInnLabel("Press 'E' to enter the inn", 3* Config.scaledTileSize, 3*Config.scaledTileSize, Config.scaledTileSize * 0.75, Config.scaledTileSize * 0.75);
        innLabel.removeFromUI();
        innLabel.getComponent(LabelComponent.class).getUIElement().setTextAlignment(TextAlignment.CENTER);

        EnterEnemyIslandLabel enterEnemyIslandLabel = new EnterEnemyIslandLabel("Press 'E' to go on an adventure", 10 * Config.scaledTileSize, 3*Config.scaledTileSize, Config.scaledTileSize * 0.75, Config.scaledTileSize * 0.75);
        enterEnemyIslandLabel.removeFromUI();
        enterEnemyIslandLabel.getComponent(LabelComponent.class).getUIElement().setTextAlignment(TextAlignment.CENTER);


        new EntryEntity(7* Config.scaledTileSize -Config.scaledTileSize *0.25-1, 2*Config.scaledTileSize +Config.scaledTileSize *0.25-1, Config.scaledTileSize * 0.75, Config.scaledTileSize * 0.75, new EnterHomeEvent(), new EnterHomeEventListener());
        new EntryEntity(2* Config.scaledTileSize +Config.scaledTileSize *0.25-1, 2*Config.scaledTileSize +Config.scaledTileSize *0.25-1, Config.scaledTileSize * 0.75, Config.scaledTileSize * 0.75, new EnterInnEvent(), new EnterInnListener());
        new EntryEntity(14*Config.scaledTileSize, 2.5*Config.scaledTileSize, 2*Config.scaledTileSize, Config.scaledTileSize /2, new EnterEnemyIslandEvent(new Point(3*Config.scaledTileSize, 2*Config.scaledTileSize +Config.scaledTileSize *0.25-1)), new EnterEnemyIslandEventListener());

        CameraEntity.getInstance(1920, 1080, 16 * Config.scaledTileSize, 16 * Config.scaledTileSize);
        CameraEntity.getInstance().attachTo(EntityHub.getInstance().getEntitiesWithComponent(PlayerComponent.class).getFirst());

        new SystemStartUp(this::SystemStartUp);

        interactionSetup();

        GameLoopStartUp.restartLoop();
    }

    private void SystemStartUp() {
        //Define systems to be started up here
        SystemHub systemHub = SystemHub.getInstance();
        systemHub.addSystem(MovementDeterminerSystem.class, new MovementDeterminerSystem(),0);
        systemHub.addSystem(EventTileSystem.class, new EventTileSystem(),1);
        systemHub.addSystem(AnimationSystem.class, new AnimationSystem(), 2);
        systemHub.addSystem(DayNightCycleSystem.class, new DayNightCycleSystem(), 3);
        systemHub.addSystem(RenderSystem.class, new RenderSystem(),4);
        systemHub.addSystem(PenguinMoverSystem.class, new PenguinMoverSystem(),5);
        systemHub.addSystem(PlatformerPathfindingSystem.class, new PlatformerPathfindingSystem(),6);
        systemHub.addSystem(MovementSystem.class, new MovementSystem(),7);
        systemHub.addSystem(ParticleSystem.class, new ParticleSystem(),8);
        systemHub.addSystem(InputHandlingSystem.class, new InputHandlingSystem(),9);
        systemHub.addSystem(CollisionSystem.class, new CollisionSystem(),10);
        systemHub.addSystem(ResourceSystem.class, new ResourceSystem(),11);
        systemHub.addSystem(CameraSystem.class, new CameraSystem(), 12);
        systemHub.addSystem(SoundSystem.class, new SoundSystem(), 13);
        systemHub.addSystem(WorldLoaderSystem.class, new WorldLoaderSystem(), 14);
    }

    private void interactionSetup() {
        PlayerEntity player = (PlayerEntity)EntityHub.getInstance().getEntitiesWithComponent(PlayerComponent.class).getFirst();
        InteractiveComponent playerInteractiveComponent = player.getComponent(InteractiveComponent.class);
        playerInteractiveComponent.mapInput(KeyCode.LEFT, 10, () -> moveLeft(player), () -> counterLeft(player));
        playerInteractiveComponent.mapInput(KeyCode.RIGHT, 10, () -> moveRight(player), () -> counterRight(player));
        playerInteractiveComponent.mapInput(MouseButton.PRIMARY, 100, () -> {player.getComponent(PositionComponent.class).setLocalX(MouseInputHandler.getInstance().getMouseX(), player); player.getComponent(PositionComponent.class).setLocalY(MouseInputHandler.getInstance().getMouseY(), player);});
    }

    private void moveLeft(Entity e) {
        double dx = -4 * Time.getInstance().getDeltaTime();
        e.getComponent(AccelerationComponent.class).getAcceleration().setDx(dx);
    }

    private void moveRight(Entity e) {
        double dx = 4 * Time.getInstance().getDeltaTime();
        e.getComponent(AccelerationComponent.class).getAcceleration().setDx(dx);
    }

    private void counterRight(Entity e) {
        e.getComponent(AccelerationComponent.class).getAcceleration().setDx(0);
    }

    private void counterLeft(Entity e) {
        e.getComponent(AccelerationComponent.class).getAcceleration().setDx(0);
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
        System.gc();
    }
}
