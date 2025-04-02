package Game.Misc.Scenes;

import Game.Entities.*;
import Game.Entities.Labels.DungeonLabel;
import Game.Entities.Labels.GoHomeLabel;
import Game.Misc.EventHandling.EventListeners.EnterDungeonEventListener;
import Game.Misc.EventHandling.EventListeners.GoHomeEventListener;
import Game.Misc.EventHandling.Events.EnterDungeonEvent;
import Game.Misc.EventHandling.Events.GoHomeEvent;
import Game.Systems.EventTileSystem;
import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.InteractiveComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.AccelerationComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.PlayerComponent;
import inf.elte.hu.gameengine_javafx.Components.UIComponents.LabelComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Core.ResourceHub;
import inf.elte.hu.gameengine_javafx.Core.SystemHub;
import inf.elte.hu.gameengine_javafx.Entities.CameraEntity;
import inf.elte.hu.gameengine_javafx.Entities.PlayerEntity;
import inf.elte.hu.gameengine_javafx.Entities.WorldEntity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Misc.Config;
import inf.elte.hu.gameengine_javafx.Misc.InputHandlers.MouseInputHandler;
import inf.elte.hu.gameengine_javafx.Misc.Layers.uiRoot;
import inf.elte.hu.gameengine_javafx.Misc.Scenes.GameScene;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.GameLoopStartUp;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.ResourceStartUp;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.SystemStartUp;
import inf.elte.hu.gameengine_javafx.Misc.Time;
import inf.elte.hu.gameengine_javafx.Systems.InputHandlingSystem;
import inf.elte.hu.gameengine_javafx.Systems.PathfindingSystem;
import inf.elte.hu.gameengine_javafx.Systems.PhysicsSystems.CollisionSystem;
import inf.elte.hu.gameengine_javafx.Systems.PhysicsSystems.MovementDeterminerSystem;
import inf.elte.hu.gameengine_javafx.Systems.PhysicsSystems.MovementSystem;
import inf.elte.hu.gameengine_javafx.Systems.RenderingSystems.*;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.ResourceSystem;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.SoundSystem;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.WorldLoaderSystem;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
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

        new ResourceStartUp();
        WorldEntity.getInstance("/assets/maps/enemyIsland.txt", "/assets/tileSets/gameTileSet.txt");

        new PlayerEntity(spawn.getX(), spawn.getY(), "idle", "/assets/images/Gentlie/Gentlie_Down_Idle.png", Config.scaledTileSize * 0.75 * 0.55, Config.scaledTileSize * 0.75);

        new WaterEntity();

        new IglooEntity(6*Config.scaledTileSize, Config.scaledTileSize, 2*Config.scaledTileSize, 2*Config.scaledTileSize);
        new ShipEntity(0, 1.5*Config.scaledTileSize, 2*Config.scaledTileSize, 2*Config.scaledTileSize);


        DungeonLabel dungeonLabel = new DungeonLabel("Press 'E' to enter the dungeon", 6* Config.scaledTileSize, 3*Config.scaledTileSize, Config.scaledTileSize * 0.75, Config.scaledTileSize * 0.75);
        dungeonLabel.removeFromUI();
        dungeonLabel.getComponent(LabelComponent.class).getUIElement().setTextAlignment(TextAlignment.CENTER);

        GoHomeLabel goHomeLabel = new GoHomeLabel("Press 'E' to go home", 2* Config.scaledTileSize, 3*Config.scaledTileSize, Config.scaledTileSize * 0.75, Config.scaledTileSize * 0.75);
        goHomeLabel.removeFromUI();
        goHomeLabel.getComponent(LabelComponent.class).getUIElement().setTextAlignment(TextAlignment.CENTER);

        new EntryEntity(7* Config.scaledTileSize -Config.scaledTileSize *0.25-1, 2*Config.scaledTileSize +Config.scaledTileSize *0.25-1, Config.scaledTileSize * 0.75, Config.scaledTileSize * 0.75, new EnterDungeonEvent(), new EnterDungeonEventListener());
        new EntryEntity(2*Config.scaledTileSize, 2*Config.scaledTileSize+Config.scaledTileSize/2, Config.scaledTileSize/2, Config.scaledTileSize /2, new GoHomeEvent(), new GoHomeEventListener());

        CameraEntity.getInstance(1920, 1080, 16* Config.scaledTileSize, 16*Config.scaledTileSize);
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
        systemHub.addSystem(RenderSystem.class, new RenderSystem(),3);
        systemHub.addSystem(LightingSystem.class, new LightingSystem(),4);
        systemHub.addSystem(PathfindingSystem.class, new PathfindingSystem(),5);
        systemHub.addSystem(MovementSystem.class, new MovementSystem(),6);
        systemHub.addSystem(ParticleSystem.class, new ParticleSystem(),7);
        systemHub.addSystem(InputHandlingSystem.class, new InputHandlingSystem(),8);
        systemHub.addSystem(CollisionSystem.class, new CollisionSystem(),9);
        systemHub.addSystem(ResourceSystem.class, new ResourceSystem(),10);
        systemHub.addSystem(CameraSystem.class, new CameraSystem(), 11);
        systemHub.addSystem(SoundSystem.class, new SoundSystem(), 12);
        systemHub.addSystem(WorldLoaderSystem.class, new WorldLoaderSystem(), 13);
    }

    private void interactionSetup() {
        PlayerEntity player = (PlayerEntity)EntityHub.getInstance().getEntitiesWithComponent(PlayerComponent.class).getFirst();
        InteractiveComponent playerInteractiveComponent = player.getComponent(InteractiveComponent.class);
        playerInteractiveComponent.mapInput(KeyCode.LEFT, 10, () -> moveLeft(player), () -> counterLeft(player));
        playerInteractiveComponent.mapInput(KeyCode.RIGHT, 10, () -> moveRight(player), () -> counterRight(player));
        playerInteractiveComponent.mapInput(MouseButton.PRIMARY, 100, () -> {player.getComponent(PositionComponent.class).setLocalX(MouseInputHandler.getInstance().getMouseX(), player); player.getComponent(PositionComponent.class).setLocalY(MouseInputHandler.getInstance().getMouseY(), player);});
    }

    private void moveLeft(Entity e) {
        double dx = -4 * Time.getInstance().getDeltaTime() * Config.getTileScale();
        e.getComponent(AccelerationComponent.class).getAcceleration().setDx(dx);
    }

    private void moveRight(Entity e) {
        double dx = 4 * Time.getInstance().getDeltaTime() * Config.getTileScale();
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
