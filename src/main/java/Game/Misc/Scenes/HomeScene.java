package Game.Misc.Scenes;

import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.InteractiveComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.AccelerationComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.PlayerComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.StateComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Core.SystemHub;
import inf.elte.hu.gameengine_javafx.Entities.CameraEntity;
import inf.elte.hu.gameengine_javafx.Entities.PlayerEntity;
import inf.elte.hu.gameengine_javafx.Entities.WorldEntity;
import inf.elte.hu.gameengine_javafx.Misc.Config;
import inf.elte.hu.gameengine_javafx.Misc.InputHandlers.MouseInputHandler;
import inf.elte.hu.gameengine_javafx.Misc.Scenes.GameScene;
import inf.elte.hu.gameengine_javafx.Misc.SoundEffect;
import inf.elte.hu.gameengine_javafx.Misc.SoundEffectStore;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.GameLoopStartUp;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.ResourceStartUp;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.SystemStartUp;
import inf.elte.hu.gameengine_javafx.Misc.Time;
import inf.elte.hu.gameengine_javafx.Systems.InputHandlingSystem;
import inf.elte.hu.gameengine_javafx.Systems.PathfindingSystem;
import inf.elte.hu.gameengine_javafx.Systems.PhysicsSystems.CollisionSystem;
import inf.elte.hu.gameengine_javafx.Systems.PhysicsSystems.MovementSystem;
import inf.elte.hu.gameengine_javafx.Systems.RenderingSystems.*;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.*;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

public class HomeScene extends GameScene {
    public HomeScene(Parent parent, double width, double height) {
        super(parent, width, height);
    }

    @Override
    public void setup() {
        new ResourceStartUp();
        WorldEntity.getInstance("/assets/maps/gentlieHome.txt", "/assets/tileSets/gameTileSet.txt");

        new PlayerEntity(1920/2, 1080/2, "idle", "/assets/images/Gentlie/Gentlie_Down_Idle.png", Config.tileSize*2, Config.tileSize*2);

        CameraEntity.getInstance(1920, 1080, 16* Config.tileSize, 16*Config.tileSize);
        CameraEntity.getInstance().attachTo(EntityHub.getInstance().getEntitiesWithComponent(PlayerComponent.class).getFirst());

        interactionSetup();

        new SystemStartUp(this::SystemStartUp);

        new GameLoopStartUp();
    }

    private void SystemStartUp() {
        //Define systems to be started up here
        SystemHub systemHub = SystemHub.getInstance();
        systemHub.addSystem(AnimationSystem.class, new AnimationSystem(), 1);
        systemHub.addSystem(RenderSystem.class, new RenderSystem(),2);
        systemHub.addSystem(LightingSystem.class, new LightingSystem(),3);
        systemHub.addSystem(PathfindingSystem.class, new PathfindingSystem(),4);
        systemHub.addSystem(MovementSystem.class, new MovementSystem(),5);
        systemHub.addSystem(ParticleSystem.class, new ParticleSystem(),6);
        systemHub.addSystem(InputHandlingSystem.class, new InputHandlingSystem(),7);
        systemHub.addSystem(CollisionSystem.class, new CollisionSystem(),8);
        systemHub.addSystem(ResourceSystem.class, new ResourceSystem(),9);
        systemHub.addSystem(CameraSystem.class, new CameraSystem(), 10);
        systemHub.addSystem(SoundSystem.class, new SoundSystem(), 11);
        systemHub.addSystem(WorldLoaderSystem.class, new WorldLoaderSystem(), 12);
    }

    private void interactionSetup() {
        PlayerEntity player = (PlayerEntity)EntityHub.getInstance().getEntitiesWithComponent(PlayerComponent.class).getFirst();
        InteractiveComponent playerInteractiveComponent = player.getComponent(InteractiveComponent.class);
        playerInteractiveComponent.mapInput(KeyCode.UP, 10, () -> moveUp(player), () -> counterUp(player));
        playerInteractiveComponent.mapInput(KeyCode.DOWN, 10, () -> moveDown(player), () -> counterDown(player));
        playerInteractiveComponent.mapInput(KeyCode.LEFT, 10, () -> moveLeft(player), () -> counterLeft(player));
        playerInteractiveComponent.mapInput(KeyCode.RIGHT, 10, () -> moveRight(player), () -> counterRight(player));
        playerInteractiveComponent.mapInput(MouseButton.PRIMARY, 100, () -> {player.getComponent(PositionComponent.class).setLocalX(MouseInputHandler.getInstance().getMouseX(), player); player.getComponent(PositionComponent.class).setLocalY(MouseInputHandler.getInstance().getMouseY(), player);});
        //playerInteractiveComponent.mapInput(MouseButton.PRIMARY, () -> System.out.println(MouseInputHandler.getInstance().getMouseX() + " " + MouseInputHandler.getInstance().getMouseY()));
        playerInteractiveComponent.mapInput(MouseButton.SECONDARY, 400, () -> new SoundEffect(player, "/assets/sound/sfx/explosion.wav", "explosion", 1f, 0f, 1000, false), () -> SoundEffectStore.getInstance().remove("explosion"));
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

    @Override
    public void breakdown() {

    }
}
