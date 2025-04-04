package Game.Misc.Scenes;

import Game.Entities.Labels.EnterEnemyIslandLabel;
import Game.Entities.EntryEntity;
import Game.Entities.SnowBallEntity;
import Game.Misc.EventHandling.EventListeners.EnterEnemyIslandEventListener;
import Game.Misc.EventHandling.Events.EnterEnemyIslandEvent;
import Game.Systems.*;
import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.InteractiveComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.AccelerationComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.CentralMassComponent;
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
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.text.TextAlignment;

import java.util.List;

public class DungeonScene extends GameScene {
    public DungeonScene(Parent parent, double width, double height) {
        super(parent, width, height);
    }

    @Override
    public void setup() {
        Config.wallTiles = List.of(0, 1, 3);
        Config.setTileScale(2.0);

        new ResourceStartUp();

        WorldEntity.getInstance(32, 32, "/assets/tileSets/gameTileSet.txt");

        new PlayerEntity(Config.scaledTileSize + Config.scaledTileSize /2, Config.scaledTileSize + Config.scaledTileSize /2, "idle", "/assets/images/Gentlie/Gentlie_Down_Idle.png", Config.scaledTileSize * 0.8 * 0.55, Config.scaledTileSize * 0.8);

        new EntryEntity(0, Config.scaledTileSize, Config.scaledTileSize, Config.scaledTileSize*3, new EnterEnemyIslandEvent(new Point(4*150, 2*150 +150 *0.25-1)), new EnterEnemyIslandEventListener());
        EnterEnemyIslandLabel enterEnemyIslandLabel = new EnterEnemyIslandLabel("Press 'E' to leave dungeon", Config.scaledTileSize, 3*Config.scaledTileSize, Config.scaledTileSize * 0.75, Config.scaledTileSize * 0.75);
        enterEnemyIslandLabel.removeFromUI();
        enterEnemyIslandLabel.getComponent(LabelComponent.class).getUIElement().setTextAlignment(TextAlignment.CENTER);


        CameraEntity.getInstance(1920, 1080, 32 * Config.scaledTileSize, 32 * Config.scaledTileSize);
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
        systemHub.addSystem(PolarBearMoverSystem.class, new PolarBearMoverSystem(),4);
        systemHub.addSystem(PathfindingSystem.class, new PathfindingSystem(),5);
        systemHub.addSystem(MovementSystem.class, new MovementSystem(),6);
        systemHub.addSystem(ParticleSystem.class, new ParticleSystem(),7);
        systemHub.addSystem(InputHandlingSystem.class, new InputHandlingSystem(),8);
        systemHub.addSystem(CustomCollisionSystem.class, new CustomCollisionSystem(),9);
        systemHub.addSystem(ResourceSystem.class, new ResourceSystem(),10);
        systemHub.addSystem(CameraSystem.class, new CameraSystem(), 11);
        systemHub.addSystem(SoundSystem.class, new SoundSystem(), 12);
        systemHub.addSystem(DungeonGeneratorSystem.class, new DungeonGeneratorSystem(2, 2), 13);
        systemHub.addSystem(PolarBearSpawnerSystem.class, new PolarBearSpawnerSystem(), 14);
        systemHub.addSystem(RemoveDeadObjectSystem.class, new RemoveDeadObjectSystem(), 15);
    }

    private void interactionSetup() {
        PlayerEntity player = (PlayerEntity)EntityHub.getInstance().getEntitiesWithComponent(PlayerComponent.class).getFirst();
        InteractiveComponent playerInteractiveComponent = player.getComponent(InteractiveComponent.class);
        playerInteractiveComponent.mapInput(KeyCode.UP, 10, () -> moveUp(player), () -> counterUp(player));
        playerInteractiveComponent.mapInput(KeyCode.DOWN, 10, () -> moveDown(player), () -> counterDown(player));
        playerInteractiveComponent.mapInput(KeyCode.LEFT, 10, () -> moveLeft(player), () -> counterLeft(player));
        playerInteractiveComponent.mapInput(KeyCode.RIGHT, 10, () -> moveRight(player), () -> counterRight(player));
        playerInteractiveComponent.mapInput(MouseButton.PRIMARY, 100, () -> {player.getComponent(PositionComponent.class).setLocalX(MouseInputHandler.getInstance().getMouseX(), player); player.getComponent(PositionComponent.class).setLocalY(MouseInputHandler.getInstance().getMouseY(), player);});
        playerInteractiveComponent.mapInput(KeyCode.ENTER, 1000, () -> new SnowBallEntity(player.getComponent(CentralMassComponent.class).getCentralX(), player.getComponent(CentralMassComponent.class).getCentralY(), Config.scaledTileSize/6, Config.scaledTileSize/6));
    }

    private void moveUp(Entity e) {
        double dy = -4 * Time.getInstance().getDeltaTime();
        e.getComponent(AccelerationComponent.class).getAcceleration().setDy(dy);
    }

    private void moveDown(Entity e) {
        double dy = 4 * Time.getInstance().getDeltaTime();
        e.getComponent(AccelerationComponent.class).getAcceleration().setDy(dy);
    }

    private void moveLeft(Entity e) {
        double dx = -4 * Time.getInstance().getDeltaTime();
        e.getComponent(AccelerationComponent.class).getAcceleration().setDx(dx);
    }

    private void moveRight(Entity e) {
        double dx = 4 * Time.getInstance().getDeltaTime();
        e.getComponent(AccelerationComponent.class).getAcceleration().setDx(dx);
    }

    private void counterUp(Entity e) {
        e.getComponent(AccelerationComponent.class).getAcceleration().setDy(0);
    }

    private void counterDown(Entity e) {
        e.getComponent(AccelerationComponent.class).getAcceleration().setDy(0);
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
