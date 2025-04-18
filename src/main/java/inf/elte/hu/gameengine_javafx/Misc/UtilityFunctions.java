package inf.elte.hu.gameengine_javafx.Misc;

import inf.elte.hu.gameengine_javafx.Components.InteractiveComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.AccelerationComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.PlayerComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Core.ResourceHub;
import inf.elte.hu.gameengine_javafx.Core.SystemHub;
import inf.elte.hu.gameengine_javafx.Entities.CameraEntity;
import inf.elte.hu.gameengine_javafx.Entities.PlayerEntity;
import inf.elte.hu.gameengine_javafx.Entities.WorldEntity;
import inf.elte.hu.gameengine_javafx.Misc.Configs.MapConfig;
import inf.elte.hu.gameengine_javafx.Misc.Layers.GameCanvas;
import inf.elte.hu.gameengine_javafx.Misc.Layers.uiRoot;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.GameLoopStartUp;
import javafx.scene.input.KeyCode;

public class UtilityFunctions {
    public static void setUpMovement(InteractiveComponent playerInteractiveComponent, PlayerEntity player) {
        setUpLeftRightMovement(playerInteractiveComponent, player);
        setUpDownUpMovement(playerInteractiveComponent, player);
    }

    public static void setUpLeftRightMovement(InteractiveComponent playerInteractiveComponent, PlayerEntity player) {
        playerInteractiveComponent.mapInput(KeyCode.LEFT, 10, () -> UtilityFunctions.moveLeft(player), () -> UtilityFunctions.counterLeft(player));
        playerInteractiveComponent.mapInput(KeyCode.RIGHT, 10, () -> UtilityFunctions.moveRight(player), () -> UtilityFunctions.counterRight(player));
    }

    public static void setUpDownUpMovement(InteractiveComponent playerInteractiveComponent, PlayerEntity player) {
        playerInteractiveComponent.mapInput(KeyCode.UP, 10, () -> UtilityFunctions.moveUp(player), () -> UtilityFunctions.counterUp(player));
        playerInteractiveComponent.mapInput(KeyCode.DOWN, 10, () -> UtilityFunctions.moveDown(player), () -> UtilityFunctions.counterDown(player));
    }

    public static void moveUp(Entity e) {
        double dy = -4 * Time.getInstance().getDeltaTime();
        e.getComponent(AccelerationComponent.class).getAcceleration().setDy(dy);
    }

    public static void moveDown(Entity e) {
        double dy = 4 * Time.getInstance().getDeltaTime();
        e.getComponent(AccelerationComponent.class).getAcceleration().setDy(dy);
    }

    public static void moveLeft(Entity e) {
        double dx = -4 * Time.getInstance().getDeltaTime();
        e.getComponent(AccelerationComponent.class).getAcceleration().setDx(dx);
    }

    public static void moveRight(Entity e) {
        double dx = 4 * Time.getInstance().getDeltaTime();
        e.getComponent(AccelerationComponent.class).getAcceleration().setDx(dx);
    }

    public static void counterUp(Entity e) {
        e.getComponent(AccelerationComponent.class).getAcceleration().setDy(0);
    }

    public static void counterDown(Entity e) {
        e.getComponent(AccelerationComponent.class).getAcceleration().setDy(0);
    }

    public static void counterRight(Entity e) {
        e.getComponent(AccelerationComponent.class).getAcceleration().setDx(0);
    }

    public static void counterLeft(Entity e) {
        e.getComponent(AccelerationComponent.class).getAcceleration().setDx(0);
    }

    public static void setUpCamera(double width, double height, double worldWidth, double worldHeight) {
        CameraEntity.getInstance(width, height, worldWidth * MapConfig.scaledTileSize, worldHeight * MapConfig.scaledTileSize);
        CameraEntity.getInstance().attachTo(EntityHub.getInstance().getEntitiesWithComponent(PlayerComponent.class).getFirst());
    }

    public static void defaultBreakdownMethod() {
        EntityHub.getInstance().unloadAll();
        EntityHub.resetInstance();
        CameraEntity.resetInstance();
        WorldEntity.resetInstance();
        SystemHub.getInstance().shutDownSystems();
        GameLoopStartUp.stopGameLoop();
        ResourceHub.getInstance().clearResources();
        ResourceHub.resetInstance();
        uiRoot.getInstance().unloadAll();
        GameCanvas.getInstance().getGraphicsContext2D().clearRect(0, 0, GameCanvas.getInstance().getWidth(), GameCanvas.getInstance().getHeight());
        System.gc();
    }

    public static void shutDownMethod() {
        System.exit(0);
    }
}
