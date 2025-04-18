package inf.elte.hu.gameengine_javafx.Systems.ResourceSystems;

import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.HitBoxComponents.HitBoxComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.CentralMassComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.DimensionComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.MapMeshComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.WorldDataComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.WorldDimensionComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Entities.CameraEntity;
import inf.elte.hu.gameengine_javafx.Entities.TileEntity;
import inf.elte.hu.gameengine_javafx.Entities.WorldEntity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Misc.Configs.MapConfig;
import inf.elte.hu.gameengine_javafx.Misc.MapClasses.Chunk;
import inf.elte.hu.gameengine_javafx.Misc.MapClasses.WorldGenerator;
import inf.elte.hu.gameengine_javafx.Misc.Tuple;
import inf.elte.hu.gameengine_javafx.Misc.Walker;

import java.util.*;

/**
 * The DynamicWorldLoaderSystem is responsible for dynamically loading and unloading chunks of the world
 * based on the player's position in the game world.
 */
public class DynamicWorldLoaderSystem extends GameSystem {
    private int width;
    private int height;

    /**
     * Constructor to initialize the system with the specified world dimensions.
     *
     * @param width  The width of the world in chunks.
     * @param height The height of the world in chunks.
     */
    public DynamicWorldLoaderSystem(int width, int height) {
        this.width = width;
        this.height = height;

        WorldEntity.getInstance().getComponent(WorldDimensionComponent.class).setWorldWidth(width * MapConfig.chunkWidth);
        WorldEntity.getInstance().getComponent(WorldDimensionComponent.class).setWorldHeight(height * MapConfig.chunkHeight);
    }

    /**
     * Starts the world loading process by loading the full world initially.
     */
    @Override
    public void start() {
        this.active = true;
        WorldEntity map = WorldEntity.getInstance();
        if (map == null) return;
        loadFullWorld();
        runWalkerAlgorithm();
        addWorldMesh();
    }

    public void runWalkerAlgorithm() {
        Random random = new Random();

        // Start with one walker at a random position
        int startX = random.nextInt(MapConfig.chunkWidth * width);
        int startY = random.nextInt(MapConfig.chunkHeight * height);
        Walker walker = new Walker(startX, startY, WorldEntity.getInstance(), new ArrayList<>());

        walker.walk();
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
     * Loads the entire world initially, chunk by chunk.
     */
    private void loadFullWorld() {
        for (int cy = 0; cy < width; cy++) {
            for (int cx = 0; cx < height; cx++) {
                loadOrGenerateChunk(cx, cy);
            }
        }
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
                        loadOrGenerateChunk(chunkX, chunkY);
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
    private void loadOrGenerateChunk(int chunkX, int chunkY) {
        Tuple<Integer, Integer> chunkKey = new Tuple<>(chunkX, chunkY);

        if (WorldEntity.getInstance().getComponent(WorldDataComponent.class).getMapData().getSavedChunks().containsKey(chunkKey)) {
            WorldEntity.getInstance().getComponent(WorldDataComponent.class).getMapData().addChunk(chunkX, chunkY, WorldEntity.getInstance().getComponent(WorldDataComponent.class).getMapData().getSavedChunks().get(chunkKey));
        } else {
            Chunk newChunk = WorldGenerator.generateChunk(chunkX, chunkY, MapConfig.chunkWidth, MapConfig.chunkHeight);
            WorldEntity.getInstance().getComponent(WorldDataComponent.class).getMapData().getSavedChunks().put(chunkKey, newChunk);
            WorldEntity.getInstance().getComponent(WorldDataComponent.class).getMapData().getWorld().put(chunkKey, newChunk);
            addBoundaryWalls(WorldEntity.getInstance().getComponent(WorldDataComponent.class).getMapData().getWorld().get(chunkKey), chunkX, chunkY);
        }
    }

    /**
     * Adds a mesh for the world, creating a list of points for all tiles in the world.
     */
    private void addWorldMesh() {
        WorldEntity map = WorldEntity.getInstance();
        if (map == null) return;

        MapMeshComponent mapMesh = map.getComponent(MapMeshComponent.class);
        if (!mapMesh.getMapCoordinates().isEmpty()) {
            mapMesh.getMapCoordinates().clear();
        }

        int worldWidth = width * MapConfig.chunkWidth;
        int worldHeight = height * MapConfig.chunkHeight;

        for (int row = 0; row < worldHeight; row++) {
            List<Point> meshRow = new ArrayList<>();
            for (int col = 0; col < worldWidth; col++) {
                TileEntity entity = map.getComponent(WorldDataComponent.class).getMapData().getElementAt(new Point(col * MapConfig.scaledTileSize + MapConfig.scaledTileSize / 2, row * MapConfig.scaledTileSize + MapConfig.scaledTileSize / 2));
                if (entity.getComponent(HitBoxComponent.class) == null) {
                    meshRow.add(entity.getComponent(CentralMassComponent.class).getCentral());
                } else {
                    meshRow.add(null);
                }
            }

            mapMesh.addRow(meshRow);
        }
    }


    /**
     * Adds boundary walls to a chunk, determining wall types based on chunk and tile positions.
     *
     * @param chunk  The chunk to which boundary walls should be added.
     * @param chunkX The X coordinate of the chunk.
     * @param chunkY The Y coordinate of the chunk.
     */
    private void addBoundaryWalls(Chunk chunk, int chunkX, int chunkY) {
        for (int x = 0; x < MapConfig.chunkWidth; x++) {
            for (int y = 0; y < MapConfig.chunkHeight; y++) {
                if (x == 0 && y == 0 && chunkX == 0 && chunkY == 0) {
                    chunk.setElement(x, y, MapConfig.topLeftWallCode);
                } else if (x == MapConfig.chunkWidth - 1 && y == 0 && chunkX == 0 && chunkY == height - 1) {
                    chunk.setElement(x, y, MapConfig.bottomLeftWallCode);
                } else if (x == 0 && y == MapConfig.chunkHeight - 1 && chunkX == width - 1 && chunkY == 0) {
                    chunk.setElement(x, y, MapConfig.topRightWallCode);
                } else if (x == MapConfig.chunkWidth - 1 && y == MapConfig.chunkHeight - 1 && chunkX == width - 1 && chunkY == height - 1) {
                    chunk.setElement(x, y, MapConfig.bottomRightWallCode);
                } else if (x == 0 && chunkY == 0) {
                    chunk.setElement(x, y, MapConfig.topWallCode);
                } else if (x == MapConfig.chunkWidth - 1 && chunkY == height - 1) {
                    chunk.setElement(x, y, MapConfig.bottomWallCode);
                } else if (y == 0 && chunkX == 0) {
                    chunk.setElement(x, y, MapConfig.leftWallCode);
                } else if (y == MapConfig.chunkHeight - 1 && chunkX == width - 1) {
                    chunk.setElement(x, y, MapConfig.rightWallCode);
                } else {
                    chunk.setElement(x, y, MapConfig.walkableTileCode);
                }
            }
        }
    }
}
