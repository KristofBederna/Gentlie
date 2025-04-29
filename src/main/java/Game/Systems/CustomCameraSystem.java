package Game.Systems;

import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.DimensionComponent;
import inf.elte.hu.gameengine_javafx.Components.RenderingComponents.ImageComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.WorldDimensionComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.SystemHub;
import inf.elte.hu.gameengine_javafx.Entities.CameraEntity;
import inf.elte.hu.gameengine_javafx.Entities.WorldEntity;
import inf.elte.hu.gameengine_javafx.Misc.Configs.MapConfig;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.MapLoaderSystems.InfiniteWorldLoaderSystem;

public class CustomCameraSystem extends GameSystem {

    @Override
    public void start() {
        this.active = true;
    }

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
    }

    private static void updateWorldBoundary() {
        if (CameraEntity.getInstance().getComponent(WorldDimensionComponent.class).getWorldHeight() == 0 || CameraEntity.getInstance().getComponent(WorldDimensionComponent.class).getWorldWidth() == 0) {
            CameraEntity.getInstance().getComponent(WorldDimensionComponent.class).setWorldHeight(WorldEntity.getInstance().getComponent(WorldDimensionComponent.class).getWorldHeight() * MapConfig.scaledTileSize);
            CameraEntity.getInstance().getComponent(WorldDimensionComponent.class).setWorldWidth(WorldEntity.getInstance().getComponent(WorldDimensionComponent.class).getWorldWidth() * MapConfig.scaledTileSize);
        }
    }

    private static void updateCameraPosition(PositionComponent playerPos, ImageComponent playerImg, CameraEntity cameraEntity) {
        double playerCenterX = playerPos.getGlobalX() + playerImg.getWidth() / 2;
        double playerCenterY = playerPos.getGlobalY() + playerImg.getHeight() / 2;

        double newX = playerCenterX - cameraEntity.getComponent(DimensionComponent.class).getWidth() / 2;
        double newY = playerCenterY - cameraEntity.getComponent(DimensionComponent.class).getHeight() / 2;

        if (SystemHub.getInstance().getSystem(InfiniteWorldLoaderSystem.class) == null) {
            cameraEntity.setClampedPosition(newX, newY);
            cameraEntity.moveUIRootToMatch(newX, newY);
        } else {
            cameraEntity.setPosition(newX, newY);
        }
    }
}
