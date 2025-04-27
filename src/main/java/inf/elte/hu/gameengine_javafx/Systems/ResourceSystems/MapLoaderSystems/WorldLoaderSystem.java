package inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.MapLoaderSystems;

import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.DimensionComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.WorldDataComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.WorldDimensionComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Entities.CameraEntity;
import inf.elte.hu.gameengine_javafx.Entities.WorldEntity;
import inf.elte.hu.gameengine_javafx.Misc.Configs.MapConfig;
import inf.elte.hu.gameengine_javafx.Misc.MapClasses.Chunk;
import inf.elte.hu.gameengine_javafx.Misc.MapClasses.MapLoader;
import inf.elte.hu.gameengine_javafx.Misc.Tuple;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * The WorldLoaderSystem is responsible for loading the world map data from a file and managing tile entities in the game world.
 * It handles the initial map data loading, the chunk-based map division, and dynamically loads and unloads tiles based on the camera's viewport.
 */
public class WorldLoaderSystem extends GameSystem {
    private final int width = (int) WorldEntity.getInstance().getComponent(WorldDimensionComponent.class).getWorldWidth();
    private final int height = (int) WorldEntity.getInstance().getComponent(WorldDimensionComponent.class).getWorldHeight();

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
     * Updates the system by checking the player's position and loading/unloading chunks accordingly.
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

        int playerChunkX = Math.floorDiv((int) (camX + camWidth / 2), (int) (MapConfig.chunkWidth * MapConfig.scaledTileSize));
        int playerChunkY = Math.floorDiv((int) (camY + camHeight / 2), (int) (MapConfig.chunkHeight * MapConfig.scaledTileSize));

        loadSurroundingChunks(playerChunkX, playerChunkY);
        unloadFarChunks(playerChunkX, playerChunkY);
    }

    /**
     * Loads the chunks surrounding the player based on the player's chunk coordinates.
     *
     * @param playerChunkX The player's current chunk X coordinate.
     * @param playerChunkY The player's current chunk Y coordinate.
     */
    private void loadSurroundingChunks(int playerChunkX, int playerChunkY) {
        Set<Tuple<Integer, Integer>> loadedChunks = WorldEntity.getInstance().getComponent(WorldDataComponent.class).getMapData().getWorld().keySet();
        for (int dx = -MapConfig.loadDistance; dx <= MapConfig.loadDistance; dx++) {
            for (int dy = -MapConfig.loadDistance; dy <= MapConfig.loadDistance; dy++) {
                int chunkX = playerChunkX + dx;
                int chunkY = playerChunkY + dy;
                if (chunkX >= 0 && chunkX < width / MapConfig.chunkWidth && chunkY >= 0 && chunkY < height / MapConfig.chunkHeight) {
                    Tuple<Integer, Integer> chunkKey = new Tuple<>(chunkX, chunkY);
                    if (!loadedChunks.contains(chunkKey)) {
                        loadChunk(chunkX, chunkY);
                    }
                }
            }
        }
    }

    /**
     * Unloads chunks that are too far away from the player and stores them in the saved chunks map.
     *
     * @param playerChunkX The player's current chunk X coordinate.
     * @param playerChunkY The player's current chunk Y coordinate.
     */
    private void unloadFarChunks(int playerChunkX, int playerChunkY) {
        Iterator<Map.Entry<Tuple<Integer, Integer>, Chunk>> iterator = WorldEntity.getInstance().getComponent(WorldDataComponent.class).getMapData().getWorld().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Tuple<Integer, Integer>, Chunk> entry = iterator.next();
            int chunkX = entry.getKey().first();
            int chunkY = entry.getKey().second();
            if (Math.abs(chunkX - playerChunkX) > MapConfig.loadDistance || Math.abs(chunkY - playerChunkY) > MapConfig.loadDistance) {
                WorldEntity.getInstance().getComponent(WorldDataComponent.class).getMapData().getSavedChunks().put(entry.getKey(), entry.getValue());
                iterator.remove();
            }
        }
    }

    /**
     * Loads a chunk from saved data or generates it if it doesn't exist yet.
     *
     * @param chunkX The X coordinate of the chunk to load or generate.
     * @param chunkY The Y coordinate of the chunk to load or generate.
     */
    private void loadChunk(int chunkX, int chunkY) {
        Tuple<Integer, Integer> chunkKey = new Tuple<>(chunkX, chunkY);

        if (WorldEntity.getInstance().getComponent(WorldDataComponent.class).getMapData().getSavedChunks().containsKey(chunkKey)) {
            WorldEntity.getInstance().getComponent(WorldDataComponent.class).getMapData().addChunk(chunkX, chunkY, WorldEntity.getInstance().getComponent(WorldDataComponent.class).getMapData().getSavedChunks().get(chunkKey));
        } else {
            System.err.println("Chunk not found, bad map loading or map not big enough");
        }
    }
}
