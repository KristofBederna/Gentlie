package inf.elte.hu.gameengine_javafx.Systems.ResourceSystems;

import inf.elte.hu.gameengine_javafx.Components.HitBoxComponents.HitBoxComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.CentralMassComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.DimensionComponent;
import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.MapMeshComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.WorldDataComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Entities.CameraEntity;
import inf.elte.hu.gameengine_javafx.Entities.TileEntity;
import inf.elte.hu.gameengine_javafx.Entities.WorldEntity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Misc.Config;
import inf.elte.hu.gameengine_javafx.Misc.MapClasses.Chunk;
import inf.elte.hu.gameengine_javafx.Misc.MapClasses.World;
import inf.elte.hu.gameengine_javafx.Misc.MapClasses.WorldGenerator;
import inf.elte.hu.gameengine_javafx.Misc.Tuple;

import java.util.*;

/**
 * The InfiniteWorldLoaderSystem is responsible for dynamically loading and unloading chunks of the game world
 * based on the player's camera position. It ensures that the world is rendered efficiently by loading only the
 * nearby chunks and unloading distant ones.
 */
public class InfiniteWorldLoaderSystem extends GameSystem {

    /**
     * Starts the InfiniteWorldLoaderSystem by loading a set of initial chunks surrounding the player.
     * This method is invoked at the start of the game or when the system is initialized.
     */
    @Override
    public void start() {
        this.active = true;
        WorldEntity map = WorldEntity.getInstance();
        if (map == null) return;

        World worldData = map.getComponent(WorldDataComponent.class).getMapData();

        // Load an initial grid of chunks around the player
        for (int cx = -1; cx <= 1; cx++) {
            for (int cy = -1; cy <= 1; cy++) {
                loadOrGenerateChunk(worldData, cx, cy);
            }
        }
    }

    /**
     * Updates the world by loading new chunks and unloading faraway ones based on the camera's position.
     * This method is called every frame to ensure that the world around the player is up-to-date.
     */
    @Override
    public void update() {
        CameraEntity camera = CameraEntity.getInstance();
        WorldEntity map = WorldEntity.getInstance();
        if (map == null || camera == null) return;

        // Get the camera's position and dimensions
        double camX = camera.getComponent(PositionComponent.class).getGlobalX();
        double camY = camera.getComponent(PositionComponent.class).getGlobalY();
        double camWidth = camera.getComponent(DimensionComponent.class).getWidth();
        double camHeight = camera.getComponent(DimensionComponent.class).getHeight();

        World worldData = map.getComponent(WorldDataComponent.class).getMapData();
        Set<Tuple<Integer, Integer>> loadedChunks = worldData.getWorld().keySet();

        // Calculate the player's current chunk position
        int playerChunkX = Math.floorDiv((int) (camX + camWidth / 2), Config.chunkWidth * Config.tileSize);
        int playerChunkY = Math.floorDiv((int) (camY + camHeight / 2), Config.chunkHeight * Config.tileSize);

        // Load chunks within a specified distance from the player
        for (int dx = -Config.loadDistance; dx <= Config.loadDistance; dx++) {
            for (int dy = -Config.loadDistance; dy <= Config.loadDistance; dy++) {
                int chunkX = playerChunkX + dx;
                int chunkY = playerChunkY + dy;
                Tuple<Integer, Integer> chunkKey = new Tuple<>(chunkX, chunkY);

                if (!loadedChunks.contains(chunkKey)) {
                    loadOrGenerateChunk(worldData, chunkX, chunkY);
                }
            }
        }

        // Unload chunks that are far from the player
        unloadFarChunks(worldData, playerChunkX, playerChunkY);
    }

    // A set to keep track of the tile coordinates that have been added to the world mesh
    private Set<Tuple<Double, Double>> addedTileCoordinates = new HashSet<>();

    /**
     * Adds tiles to the world mesh, ensuring that tiles are not added multiple times.
     * This method builds the world mesh by iterating over the chunks and adding the relevant tiles.
     */
    private void addWorldMesh() {
        WorldEntity map = WorldEntity.getInstance();
        if (map == null) return;

        MapMeshComponent mapMesh = map.getComponent(MapMeshComponent.class);
        List<Point> meshRow = new ArrayList<>();
        Map<Tuple<Integer, Integer>, Chunk> worldChunks = map.getComponent(WorldDataComponent.class).getMapData().getWorld();

        // Iterate over each chunk and its tiles to add them to the mesh
        worldChunks.values().forEach(chunk -> {
            for (List<TileEntity> tileEntities : chunk.getChunk()) {
                for (TileEntity tileEntity : tileEntities) {
                    if (tileEntity.getComponent(HitBoxComponent.class) != null) {
                        continue;
                    }

                    // Get the tile's coordinates and ensure it hasn't been added already
                    double tileX = tileEntity.getComponent(CentralMassComponent.class).getCentralX();
                    double tileY = tileEntity.getComponent(CentralMassComponent.class).getCentralY();
                    Tuple<Double, Double> tileCoordinates = new Tuple<>(tileX, tileY);
                    if (!addedTileCoordinates.contains(tileCoordinates)) {
                        meshRow.add(new Point(tileX, tileY));
                        addedTileCoordinates.add(tileCoordinates);
                    }
                }

                // If the mesh row is not empty, add it to the map mesh and clear the row for the next iteration
                if (!meshRow.isEmpty()) {
                    mapMesh.addRow(new ArrayList<>(meshRow));
                    meshRow.clear();
                }
            }
        });
    }

    /**
     * Unloads chunks that are too far from the player. These chunks are saved for later use.
     * This method ensures that only the chunks close to the player are kept active in memory.
     *
     * @param worldData The world data component that holds all the chunks.
     * @param playerChunkX The player's current chunk X position.
     * @param playerChunkY The player's current chunk Y position.
     */
    private void unloadFarChunks(World worldData, int playerChunkX, int playerChunkY) {
        Iterator<Map.Entry<Tuple<Integer, Integer>, Chunk>> iterator = worldData.getWorld().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Tuple<Integer, Integer>, Chunk> entry = iterator.next();
            int chunkX = entry.getKey().first();
            int chunkY = entry.getKey().second();

            // If the chunk is too far from the player, save it and remove it from the active world
            if (Math.abs(chunkX - playerChunkX) > Config.loadDistance || Math.abs(chunkY - playerChunkY) > Config.loadDistance) {
                worldData.getSavedChunks().put(entry.getKey(), entry.getValue());
                iterator.remove();
            }
        }
    }

    /**
     * Loads or generates a chunk based on its position in the world.
     * If the chunk already exists, it is added to the world. Otherwise, it is generated and added.
     *
     * @param worldData The world data component that manages the chunks.
     * @param chunkX The X coordinate of the chunk.
     * @param chunkY The Y coordinate of the chunk.
     */
    private void loadOrGenerateChunk(World worldData, int chunkX, int chunkY) {
        Tuple<Integer, Integer> chunkKey = new Tuple<>(chunkX, chunkY);

        // If the chunk is already saved, load it from the saved chunks
        if (worldData.getSavedChunks().containsKey(chunkKey)) {
            worldData.addChunk(chunkX, chunkY, worldData.getSavedChunks().get(chunkKey));
        } else {
            // Otherwise, generate a new chunk and add it to the world
            Chunk newChunk = WorldGenerator.generateChunk(chunkX, chunkY, Config.chunkWidth, Config.chunkHeight);
            worldData.getSavedChunks().put(chunkKey, newChunk);
            worldData.addChunk(chunkX, chunkY, newChunk);
        }

        addWorldMesh(); // Update the world mesh after loading the chunk
    }
}
