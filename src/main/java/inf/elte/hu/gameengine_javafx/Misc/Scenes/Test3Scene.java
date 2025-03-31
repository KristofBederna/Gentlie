package inf.elte.hu.gameengine_javafx.Misc.Scenes;

import inf.elte.hu.gameengine_javafx.Components.InteractiveComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.PlayerComponent;
import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.StateComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.VelocityComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Core.EntityManager;
import inf.elte.hu.gameengine_javafx.Core.ResourceHub;
import inf.elte.hu.gameengine_javafx.Core.SystemHub;
import inf.elte.hu.gameengine_javafx.Entities.*;
import inf.elte.hu.gameengine_javafx.Misc.Config;
import inf.elte.hu.gameengine_javafx.Misc.InputHandlers.MouseInputHandler;
import inf.elte.hu.gameengine_javafx.Misc.Layers.uiRoot;
import inf.elte.hu.gameengine_javafx.Misc.SoundEffect;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.GameLoopStartUp;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.ResourceStartUp;
import inf.elte.hu.gameengine_javafx.Misc.Time;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.SceneManagementSystem;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;

import java.util.Objects;


public class Test3Scene extends GameScene {
    public Test3Scene(Parent parent, double width, double height) {
        super(parent, width, height);
    }

    @Override
    public void setup() {
        getStylesheets().add(Objects.requireNonNull(getClass().getResource("/assets/styles/styles.css")).toExternalForm());
        new ResourceStartUp();
        WorldEntity.getInstance(30, 15, "/assets/maps/testMap.txt", "/assets/tileSets/testTiles2.txt");
        Entity entity2 = entitySetup();
        cameraSetup();
        interactionSetup(entity2);
        //new SystemStartUp();
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

    private void cameraSetup() {
        CameraEntity.getInstance(1920, 1080, 30 * Config.scaledTileSize, 15 * Config.scaledTileSize);
        CameraEntity.getInstance().attachTo(EntityHub.getInstance().getEntitiesWithComponent(PlayerComponent.class).getFirst());
    }

    private Entity entitySetup() {
        new PlayerEntity(420, 120, "idle", "/assets/images/PlayerIdle.png", 0.8 * Config.scaledTileSize, 0.8 * Config.scaledTileSize);
        DummyEntity entity2 = new DummyEntity(100, 100, "idle", "/assets/images/PlayerIdle.png", 80, 80);
        return entity2;
    }

    private void interactionSetup(Entity entity2) {
        PlayerEntity player = (PlayerEntity) EntityHub.getInstance().getEntitiesWithComponent(PlayerComponent.class).getFirst();
        InteractiveComponent dummyInteractiveComponent = player.getComponent(InteractiveComponent.class);
        dummyInteractiveComponent.mapInput(KeyCode.UP, 10, () -> moveUp(player), () -> counterVertical(player));
        dummyInteractiveComponent.mapInput(KeyCode.DOWN, 10, () -> moveDown(player), () -> counterVertical(player));
        dummyInteractiveComponent.mapInput(KeyCode.LEFT, 10, () -> moveLeft(player), () -> counterHorizontal(player));
        dummyInteractiveComponent.mapInput(KeyCode.RIGHT, 10, () -> moveRight(player), () -> counterHorizontal(player));
        dummyInteractiveComponent.mapInput(MouseButton.PRIMARY, 100, () -> {
            player.getComponent(PositionComponent.class).setLocalX(MouseInputHandler.getInstance().getMouseX(), player);
            player.getComponent(PositionComponent.class).setLocalY(MouseInputHandler.getInstance().getMouseY(), player);
        });
        //dummyInteractiveComponent.mapInput(MouseButton.PRIMARY, () -> System.out.println(MouseInputHandler.getInstance().getMouseX() + " " + MouseInputHandler.getInstance().getMouseY()));
        dummyInteractiveComponent.mapInput(MouseButton.SECONDARY, 400, () -> new SoundEffect(player, "/assets/sound/sfx/explosion.wav", "explosion", 1f, 0f, 1000, false));
        dummyInteractiveComponent.mapInput(KeyCode.F2, 10, () -> CameraEntity.getInstance().attachTo(entity2), () -> CameraEntity.getInstance().attachTo(player));
        dummyInteractiveComponent.mapInput(KeyCode.BACK_SPACE, 100, () -> SystemHub.getInstance().getSystem(SceneManagementSystem.class).requestSceneChange(new Test2Scene(new BorderPane(), 1920, 1080)));
    }


    private void moveUp(Entity e) {
        double dy = -400 * Time.getInstance().getDeltaTime();
        e.getComponent(VelocityComponent.class).getVelocity().setDy(dy);
        e.getComponent(StateComponent.class).setCurrentState("up");
    }

    private void moveDown(Entity e) {
        double dy = 400 * Time.getInstance().getDeltaTime();
        e.getComponent(VelocityComponent.class).getVelocity().setDy(dy);
        e.getComponent(StateComponent.class).setCurrentState("down");
    }

    private void moveLeft(Entity e) {
        double dx = -400 * Time.getInstance().getDeltaTime();
        e.getComponent(VelocityComponent.class).getVelocity().setDx(dx);
        e.getComponent(StateComponent.class).setCurrentState("left");
    }

    private void moveRight(Entity e) {
        double dx = 400 * Time.getInstance().getDeltaTime();
        e.getComponent(VelocityComponent.class).getVelocity().setDx(dx);
        e.getComponent(StateComponent.class).setCurrentState("right");
    }

    private void counterVertical(Entity e) {
        e.getComponent(VelocityComponent.class).getVelocity().setDy(0);
        e.getComponent(StateComponent.class).setCurrentState("idle");
    }


    private void counterHorizontal(Entity e) {
        e.getComponent(VelocityComponent.class).getVelocity().setDx(0);
        e.getComponent(StateComponent.class).setCurrentState("idle");
    }
}

