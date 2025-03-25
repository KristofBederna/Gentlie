package inf.elte.hu.gameengine_javafx.Systems.RenderingSystems;

import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.DimensionComponent;
import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.RenderingComponents.ImageComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.WorldDimensionComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Core.SystemHub;
import inf.elte.hu.gameengine_javafx.Entities.CameraEntity;
import inf.elte.hu.gameengine_javafx.Entities.WorldEntity;
import inf.elte.hu.gameengine_javafx.Misc.Config;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.InfiniteWorldLoaderSystem;

/**
 * The CameraSystem class is responsible for updating the position of the camera in the game world.
 * It tracks the player entity and ensures that the camera follows the player by adjusting the camera's position
 * based on the player's current position and image dimensions.
 *
 * The system updates the camera's position so that the player is always centered in the viewport,
 * accounting for the camera's dimension and the player's current position.
 */
public class CameraSystem extends GameSystem {

    /**
     * Starts the CameraSystem by activating it.
     */
    @Override
    public void start() {
        this.active = true;
    }

    /**
     * Updates the camera's position based on the player's current position and image dimensions.
     * If the camera or player is invalid (missing position or image component), the update is skipped.
     * The camera's position is adjusted to center the player in the viewport.
     */
    @Override
    public void update() {
        if (CameraEntity.getInstance() == null) {
            return;
        }
        if (CameraEntity.getInstance().getOwner() == null) {
            return;
        }

        updateWorldBoundary();

        PositionComponent playerPos = CameraEntity.getInstance().getOwner().getComponent(PositionComponent.class);
        ImageComponent playerImg = CameraEntity.getInstance().getOwner().getComponent(ImageComponent.class);
        CameraEntity cameraEntity = CameraEntity.getInstance();

        if (playerPos == null || playerImg == null) return;

        updateCameraPosition(playerPos, playerImg, cameraEntity);

        // Updates the entity manager to mark the camera entity as the last used entity
        EntityHub.getInstance().getEntityManager(CameraEntity.getInstance().getOwner().getClass()).updateLastUsed(CameraEntity.getInstance().getOwner().getId());
    }

    private static void updateWorldBoundary() {
        if (CameraEntity.getInstance().getComponent(WorldDimensionComponent.class).getWorldHeight() == 0 || CameraEntity.getInstance().getComponent(WorldDimensionComponent.class).getWorldWidth() == 0) {
            CameraEntity.getInstance().getComponent(WorldDimensionComponent.class).setWorldHeight(WorldEntity.getInstance().getComponent(WorldDimensionComponent.class).getWorldHeight()* Config.tileSize);
            CameraEntity.getInstance().getComponent(WorldDimensionComponent.class).setWorldWidth(WorldEntity.getInstance().getComponent(WorldDimensionComponent.class).getWorldWidth()* Config.tileSize);
        }
    }

    /**
     * Calculates and updates the camera's position to keep the player centered in the viewport.
     *
     * @param playerPos The position component of the player entity.
     * @param playerImg The image component of the player entity to determine its dimensions.
     * @param cameraEntity The camera entity whose position needs to be updated.
     */
    private static void updateCameraPosition(PositionComponent playerPos, ImageComponent playerImg, CameraEntity cameraEntity) {
        double playerCenterX = playerPos.getGlobalX() + playerImg.getWidth() / 2;
        double playerCenterY = playerPos.getGlobalY() + playerImg.getHeight() / 2;

        // Calculate new camera position so the player is centered
        double newX = playerCenterX - cameraEntity.getComponent(DimensionComponent.class).getWidth() / 2;
        double newY = playerCenterY - cameraEntity.getComponent(DimensionComponent.class).getHeight() / 2;

        if (SystemHub.getInstance().getSystem(InfiniteWorldLoaderSystem.class) == null) {
            cameraEntity.setClampedPosition(newX, newY);
        } else {
            cameraEntity.setPosition(newX, newY);
        }
    }
}
