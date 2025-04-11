package inf.elte.hu.gameengine_javafx.Systems.ResourceSystems;

import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.HitBoxComponents.HitBoxComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.DimensionComponent;
import inf.elte.hu.gameengine_javafx.Components.RenderingComponents.ImageComponent;
import inf.elte.hu.gameengine_javafx.Components.TileValueComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.WorldDataComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Core.EntityManager;
import inf.elte.hu.gameengine_javafx.Entities.CameraEntity;
import inf.elte.hu.gameengine_javafx.Entities.TileEntity;
import inf.elte.hu.gameengine_javafx.Entities.WorldEntity;
import inf.elte.hu.gameengine_javafx.Misc.Config;
import inf.elte.hu.gameengine_javafx.Misc.MapClasses.Chunk;
import inf.elte.hu.gameengine_javafx.Misc.MapClasses.MapLoader;
import inf.elte.hu.gameengine_javafx.Misc.MapClasses.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The WorldLoaderSystem is responsible for loading the world map data from a file and managing tile entities in the game world.
 * It handles the initial map data loading, the chunk-based map division, and dynamically loads and unloads tiles based on the camera's viewport.
 */
public class WorldLoaderSystem extends GameSystem {

    /**
     * Starts the system, loading the world map data from the file and dividing the map into chunks.
     * This method also initializes the tile entities for the map.
     */
    @Override
    public void start() {
        this.active = true;
        MapLoader.loadMap();
    }

    /**
     * Updates the world by dynamically loading and unloading tiles based on the camera's position and viewport.
     * Tiles outside the camera's view are unloaded, while new tiles inside the viewport are loaded.
     */
    @Override
    public void update() {
        CameraEntity camera = CameraEntity.getInstance();
        WorldEntity map = WorldEntity.getInstance();
        if (map == null || camera == null) return;

        double camX = camera.getComponent(PositionComponent.class).getGlobalX();
        double camY = camera.getComponent(PositionComponent.class).getGlobalY();
        double camWidth = camera.getComponent(DimensionComponent.class).getWidth();
        double camHeight = camera.getComponent(DimensionComponent.class).getHeight();

        // Get the tile manager to manage the loaded tiles
        EntityManager<TileEntity> tileManager = EntityHub.getInstance().getEntityManager(TileEntity.class);
        List<TileEntity> toRemove = new ArrayList<>();

        // Unload tiles that are outside of the camera's viewport
        for (TileEntity tile : tileManager.getEntities().values()) {
            double tileX = tile.getComponent(PositionComponent.class).getGlobalX();
            double tileY = tile.getComponent(PositionComponent.class).getGlobalY();

            if (tileX + Config.scaledTileSize < camX || tileX > camX + camWidth || tileY + Config.scaledTileSize < camY || tileY > camY + camHeight) {
                toRemove.add(tile);
            }
        }

        // Remove the tiles that are outside of the camera's viewport
        for (TileEntity tile : toRemove) {
            tileManager.unload(tile.getId());
        }

        // Get the world data from the map
        World worldData = map.getComponent(WorldDataComponent.class).getMapData();
        Set<String> existingTiles = tileManager.getEntities().values().stream()
                .map(t -> t.getComponent(PositionComponent.class).getGlobalX() + "," + t.getComponent(PositionComponent.class).getGlobalY())
                .collect(Collectors.toSet());

        // Load tiles that are inside the camera's viewport
        for (Chunk row : worldData.getWorld().values()) {
            for (List<TileEntity> tiles : row.getChunk()) {
                for (TileEntity tileEntity : tiles) {
                    double tileX = tileEntity.getComponent(PositionComponent.class).getGlobalX();
                    double tileY = tileEntity.getComponent(PositionComponent.class).getGlobalY();

                    if (tileX + Config.scaledTileSize >= camX && tileX <= camX + camWidth && tileY + Config.scaledTileSize >= camY && tileY <= camY + camHeight) {

                        String key = tileX + "," + tileY;
                        if (!existingTiles.contains(key)) {
                            boolean hasHitBox = tileEntity.getComponent(HitBoxComponent.class) != null;
                            TileEntity newTile = new TileEntity(tileEntity.getComponent(TileValueComponent.class).getTileValue(), tileX, tileY, tileEntity.getComponent(ImageComponent.class).getImagePath(), Config.scaledTileSize, Config.scaledTileSize, hasHitBox);
                            tileManager.register(newTile);
                        }
                    }
                }
            }
        }
    }
}
