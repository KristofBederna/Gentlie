package inf.elte.hu.gameengine_javafx.Entities;

import inf.elte.hu.gameengine_javafx.Components.Default.ParentComponent;
import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.DimensionComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.WorldDimensionComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Misc.Layers.uiRoot;
import javafx.application.Platform;

public class CameraEntity extends Entity {
    private static CameraEntity instance;

    private CameraEntity(double width, double height, double worldWidth, double worldHeight) {
        this.addComponent(new DimensionComponent(width, height));
        this.getComponent(PositionComponent.class).setLocalPosition(0, 0, this);
        this.addComponent(new WorldDimensionComponent(worldWidth, worldHeight));

        addToManager();
    }

    private CameraEntity(double width, double height) {
        this.addComponent(new DimensionComponent(width, height));
        this.getComponent(PositionComponent.class).setLocalPosition(0, 0, this);

        addToManager();
    }

    public static CameraEntity getInstance(double width, double height, double worldWidth, double worldHeight) {
        if (instance == null) {
            instance = new CameraEntity(width, height, worldWidth, worldHeight);
        }
        return instance;
    }

    public static CameraEntity getInstance(double width, double height) {
        if (instance == null) {
            instance = new CameraEntity(width, height);
        }
        return instance;
    }

    public static CameraEntity getInstance() {
        return instance;
    }

    public static void resetInstance() {
        instance = null;
    }

    public void setPosition(double x, double y) {
        this.getComponent(PositionComponent.class).setLocalPosition(x, y, this);
    }

    /**
     * Sets the position based on the edges of the world, so that the camera doesn't run off the edge.
     */
    public void setClampedPosition(double x, double y) {
        double clampedX = calculateClampedX(x);
        double clampedY = calculateClampedY(y);
        this.getComponent(PositionComponent.class).setLocalPosition(clampedX, clampedY, this);
    }

    /**
     * Moves the UI root to match the position of the camera, so that the drawn out UI elements are in the correct position.
     */
    public void moveUIRootToMatch(double x, double y) {
        Platform.runLater(() -> {
            double clampedX = calculateClampedX(x);
            double clampedY = calculateClampedY(y);

            uiRoot.getInstance().setLayoutX(clampedX);
            uiRoot.getInstance().setLayoutY(clampedY);
        });
    }

    private double calculateClampedY(double y) {
        return Math.max(0, Math.min(y, this.getComponent(WorldDimensionComponent.class).getWorldHeight() - this.getComponent(DimensionComponent.class).getHeight()));
    }

    private double calculateClampedX(double x) {
        return Math.max(0, Math.min(x, this.getComponent(WorldDimensionComponent.class).getWorldWidth() - this.getComponent(DimensionComponent.class).getWidth()));
    }

    public void setWidth(double width) {
        this.getComponent(DimensionComponent.class).setWidth(width);
    }

    public void setHeight(double height) {
        this.getComponent(DimensionComponent.class).setHeight(height);
    }

    /**
     * Returns whether and entity is inside the camera's viewport.
     *
     * @param entityX      X cooridnate of the entity.
     * @param entityY      Y coordinate of the entity.
     * @param entityWidth  Width of the entity.
     * @param entityHeight Height of the entity.
     * @return {@code True} if the entity is inside(even 1 pixel), {@code False} if not.
     */
    public boolean isPositionInsideViewport(double entityX, double entityY, double entityWidth, double entityHeight) {
        double renderX = getRenderX(entityX);
        double renderY = getRenderY(entityY);

        return renderX + entityWidth >= 0 && renderX <= this.getComponent(DimensionComponent.class).getWidth() &&
                renderY + entityHeight >= 0 && renderY <= this.getComponent(DimensionComponent.class).getHeight();
    }

    /**
     * @param entityY The entity's Y coordinate.
     * @return The entity's rendering position(Y coordinate).
     */
    public static double getRenderY(double entityY) {
        CameraEntity cameraEntity = CameraEntity.getInstance();
        if (cameraEntity == null) {
            return entityY;
        }
        return entityY - cameraEntity.getComponent(PositionComponent.class).getGlobalY();
    }

    /**
     * @param entityX The entity's X coordinate.
     * @return The entity's rendering position(X coordinate).
     */
    public static double getRenderX(double entityX) {
        CameraEntity cameraEntity = CameraEntity.getInstance();
        if (cameraEntity == null) {
            return entityX;
        }
        return entityX - cameraEntity.getComponent(PositionComponent.class).getGlobalX();
    }

    /**
     * Attaches the camera to the entity.
     * @param entity The entity to attach the camera to.
     */
    public void attachTo(Entity entity) {
        this.getComponent(ParentComponent.class).removeAllChildren();
        this.getComponent(ParentComponent.class).addChild(entity);
    }

    public Entity getOwner() {
        return this.getComponent(ParentComponent.class).getChildren().stream().toList().getFirst();
    }
}