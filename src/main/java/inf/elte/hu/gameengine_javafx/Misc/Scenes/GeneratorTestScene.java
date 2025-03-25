package inf.elte.hu.gameengine_javafx.Misc.Scenes;

import inf.elte.hu.gameengine_javafx.Components.InteractiveComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.PlayerComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.StateComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.VelocityComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Core.EntityManager;
import inf.elte.hu.gameengine_javafx.Core.ResourceHub;
import inf.elte.hu.gameengine_javafx.Core.SystemHub;
import inf.elte.hu.gameengine_javafx.Entities.*;
import inf.elte.hu.gameengine_javafx.Misc.Config;
import inf.elte.hu.gameengine_javafx.Misc.Layers.uiRoot;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.GameLoopStartUp;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.ResourceStartUp;
import inf.elte.hu.gameengine_javafx.Misc.Time;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;

public class GeneratorTestScene extends GameScene {
    public GeneratorTestScene(Parent parent, double width, double height) {
        super(parent, width, height);
        setup();
    }

    @Override
    public void setup() {
        new ResourceStartUp();
        WorldEntity.getInstance(10, 10, "/assets/maps/spawn.txt", "/assets/tileSets/testTiles.txt");
        entitySetup();
        cameraSetup();
        interactionSetup();
        new GameLoopStartUp();
    }

    private void cameraSetup() {
        CameraEntity.getInstance(1920, 1080);
        CameraEntity.getInstance().attachTo(EntityHub.getInstance().getEntitiesWithComponent(PlayerComponent.class).getFirst());
    }

    private void entitySetup() {
        new PlayerEntity(220, 220, "idle", "/assets/images/PlayerIdle.png", 0.8 * Config.tileSize, 0.8 * Config.tileSize);
    }

    private void interactionSetup() {
        PlayerEntity player = (PlayerEntity) EntityHub.getInstance().getEntitiesWithComponent(PlayerComponent.class).getFirst();
        InteractiveComponent playerInteractiveComponent = player.getComponent(InteractiveComponent.class);
        playerInteractiveComponent.mapInput(KeyCode.UP, 10, () -> moveUp(player), () -> counterVertical(player));
        playerInteractiveComponent.mapInput(KeyCode.DOWN, 10, () -> moveDown(player), () -> counterVertical(player));
        playerInteractiveComponent.mapInput(KeyCode.LEFT, 10, () -> moveLeft(player), () -> counterHorizontal(player));
        playerInteractiveComponent.mapInput(KeyCode.RIGHT, 10, () -> moveRight(player), () -> counterHorizontal(player));
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
}
