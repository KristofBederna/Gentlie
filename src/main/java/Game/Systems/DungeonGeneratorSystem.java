package Game.Systems;

import Game.Entities.ChestEntity;
import Game.Entities.PathfindingEntity;
import Game.Entities.PolarBearEntity;
import Game.Entities.PolarBearSpawner;
import Game.Misc.PlayerStats;
import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.FilePathComponent;
import inf.elte.hu.gameengine_javafx.Components.HitBoxComponents.HitBoxComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.CentralMassComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.DimensionComponent;
import inf.elte.hu.gameengine_javafx.Components.TileValueComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.MapMeshComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.WorldDataComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.WorldDimensionComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Entities.CameraEntity;
import inf.elte.hu.gameengine_javafx.Entities.PlayerEntity;
import inf.elte.hu.gameengine_javafx.Entities.TileEntity;
import inf.elte.hu.gameengine_javafx.Entities.WorldEntity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Misc.Configs.MapConfig;
import inf.elte.hu.gameengine_javafx.Misc.MapClasses.*;
import inf.elte.hu.gameengine_javafx.Misc.Pathfinding;
import inf.elte.hu.gameengine_javafx.Misc.Tuple;
import inf.elte.hu.gameengine_javafx.Misc.Walker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * The DynamicWorldLoaderSystem is responsible for dynamically loading and unloading chunks of the world
 * based on the player's position in the game world.
 */
public class DungeonGeneratorSystem extends GameSystem {
    private int width;
    private int height;

    /**
     * Constructor to initialize the system with the specified world dimensions.
     *
     * @param width  The width of the world in chunks.
     * @param height The height of the world in chunks.
     */
    public DungeonGeneratorSystem(int width, int height) {
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
        String saveDir = PlayerStats.currentSave;
        File lastMap = new File(saveDir, "lastMapGenerated.txt");
        if (lastMap.exists()) {
            map.getComponent(WorldDataComponent.class).clear();
            map.addComponent(new FilePathComponent(lastMap.getPath()));
            File dungeonEntityPositions = new File(saveDir, "dungeonEntities.txt");
            if (dungeonEntityPositions.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(dungeonEntityPositions.getPath()))) {
                    String line;

                    // First read
                    br.readLine();
                    while ((line = br.readLine()) != null && !Objects.equals(line, "Chests")) {
                        String[] split = line.split(" ");
                        new PolarBearSpawner(Double.parseDouble(split[0]) + MapConfig.scaledTileSize * 0.75 / 2, Double.parseDouble(split[1]) + MapConfig.scaledTileSize * 0.75 / 2);
                    }

                    // Skip the "Chests" line
                    if (line != null && Objects.equals(line, "Chests")) {
                        // Now process chests
                        for (String chestLine = br.readLine(); chestLine != null; chestLine = br.readLine()) {
                            String[] split = chestLine.split(" ");
                            new ChestEntity(Double.parseDouble(split[0]), Double.parseDouble(split[1]), MapConfig.scaledTileSize * 0.7, MapConfig.scaledTileSize * 0.7);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
            MapLoader.loadMap();
            return;
        }
        double walkablePercent;
        do {
            resetEntities();
            loadFullWorld();
            runWalkerAlgorithm();
            addWorldMesh();
            applyConditioning();
            addWorldMesh();
            removeIsolated();
            double walkable = 0;
            for (int i = 0; i < map.getComponent(MapMeshComponent.class).getMapCoordinates().size(); i++) {
                for (int j = 0; j < map.getComponent(MapMeshComponent.class).getMapCoordinates().size(); j++) {
                    if (map.getComponent(MapMeshComponent.class).getMapCoordinate(i, j) != null) {
                        walkable++;
                    }
                }
            }
            walkablePercent = walkable / (MapConfig.chunkWidth * width * MapConfig.chunkHeight * height);
        }
        while (walkablePercent < 0.63);
        try {
            MapSaver.saveMap(map, PlayerStats.currentSave + "/lastMapGenerated.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void resetEntities() {
        for (Entity entity : EntityHub.getInstance().getEntitiesWithType(PolarBearSpawner.class)) {
            EntityHub.getInstance().removeEntity(entity);
        }
        for (Entity entity : EntityHub.getInstance().getEntitiesWithType(PolarBearEntity.class)) {
            EntityHub.getInstance().removeEntity(entity);
        }
        for (Entity entity : EntityHub.getInstance().getEntitiesWithType(ChestEntity.class)) {
            EntityHub.getInstance().removeEntity(entity);
        }
        EntityHub.getInstance().removeEntityManager(PolarBearSpawner.class);
        EntityHub.getInstance().removeEntityManager(PolarBearEntity.class);
        EntityHub.getInstance().removeEntityManager(ChestEntity.class);
    }

    private void removeIsolated() {
        Random random = new Random();
        double luckFactor = 0;
        double spawnFactor = 0;
        for (List<Point> row : WorldEntity.getInstance().getComponent(MapMeshComponent.class).getMapCoordinates()) {
            for (Point p : row) {
                if (p == null) continue;
                List<Point> path = Pathfinding.selectPath(new PathfindingEntity(EntityHub.getInstance().getEntitiesWithType(PlayerEntity.class).getFirst().getComponent(CentralMassComponent.class).getCentral(), p.getCoordinates()));
                if (path.isEmpty()) {
                    row.set(row.indexOf(p), null);
                    WorldEntity.getInstance().getComponent(WorldDataComponent.class).getMapData().setElementAt(p, 1);
                }
                luckFactor = placeChests(p, path, random, luckFactor);
                spawnFactor = placeEnemySpawns(p, path, random, spawnFactor);
            }
        }
    }

    private double placeEnemySpawns(Point p, List<Point> path, Random random, double spawnFactor) {
        if (path.size() > 8) {
            double chance = random.nextDouble(0, 1);
            if (chance + spawnFactor > 0.5) {
                WorldDataComponent worldData = WorldEntity.getInstance().getComponent(WorldDataComponent.class);
                World mapData = worldData.getMapData();

                boolean left = mapData.getElementAt(new Point(p.getX() - MapConfig.scaledTileSize, p.getY()))
                        .getComponent(TileValueComponent.class).getTileValue() == 4;
                boolean right = mapData.getElementAt(new Point(p.getX() + MapConfig.scaledTileSize, p.getY()))
                        .getComponent(TileValueComponent.class).getTileValue() == 4;
                boolean up = mapData.getElementAt(new Point(p.getX(), p.getY() - MapConfig.scaledTileSize))
                        .getComponent(TileValueComponent.class).getTileValue() == 4;
                boolean down = mapData.getElementAt(new Point(p.getX(), p.getY() + MapConfig.scaledTileSize))
                        .getComponent(TileValueComponent.class).getTileValue() == 4;

                int count = (left ? 1 : 0) + (right ? 1 : 0) + (up ? 1 : 0) + (down ? 1 : 0);

                if (count >= 3) {
                    new PolarBearSpawner(p.getX(), p.getY());
                    spawnFactor = -1.0;
                    return spawnFactor;
                }

                spawnFactor += 0.01;
            } else {
                spawnFactor += 0.01;
            }
        }
        return spawnFactor;
    }

    private double placeChests(Point p, List<Point> path, Random random, double luckFactor) {
        if (path.size() > 10) {
            double chance = random.nextDouble(0, 1);
            if (chance + luckFactor > 0.95) {
                WorldDataComponent worldData = WorldEntity.getInstance().getComponent(WorldDataComponent.class);
                World mapData = worldData.getMapData();

                boolean left = mapData.getElementAt(new Point(p.getX() - MapConfig.scaledTileSize, p.getY()))
                        .getComponent(TileValueComponent.class).getTileValue() == 1;
                boolean right = mapData.getElementAt(new Point(p.getX() + MapConfig.scaledTileSize, p.getY()))
                        .getComponent(TileValueComponent.class).getTileValue() == 1;
                boolean up = mapData.getElementAt(new Point(p.getX(), p.getY() - MapConfig.scaledTileSize))
                        .getComponent(TileValueComponent.class).getTileValue() == 1;
                boolean down = mapData.getElementAt(new Point(p.getX(), p.getY() + MapConfig.scaledTileSize))
                        .getComponent(TileValueComponent.class).getTileValue() == 1;

                int count = (left ? 1 : 0) + (right ? 1 : 0) + (up ? 1 : 0) + (down ? 1 : 0);

                boolean isOpposingBlocked = (up && down && !left && !right) || (left && right && !up && !down);

                if (count >= 2 && !isOpposingBlocked) {
                    new ChestEntity(p.getX(), p.getY(), MapConfig.scaledTileSize * 0.7, MapConfig.scaledTileSize * 0.7);
                    luckFactor = -1;
                    return luckFactor;
                }

                luckFactor += 0.01;
            } else {
                luckFactor += 0.01;
            }
        }
        return luckFactor;
    }

    private void applyConditioning() {
        WorldEntity.getInstance().getComponent(WorldDataComponent.class).getMapData().getWorld().entrySet().forEach(entry -> {
            Tuple<Integer, Integer> chunkKey = entry.getKey();
            Chunk chunk = entry.getValue();

            chunk.getChunk().forEach(tiles -> {
                tiles.forEach(tile -> {
                    PositionComponent pos = tile.getComponent(PositionComponent.class);
                    //Spawn location
                    if (chunkKey.first() == 0 && chunkKey.second() == 0
                            && pos.getGlobal().getX() <= 3 * MapConfig.scaledTileSize
                            && pos.getGlobal().getY() <= 3 * MapConfig.scaledTileSize
                            && pos.getGlobal().getY() >= MapConfig.scaledTileSize) {
                        if (pos.getGlobal().getX() < MapConfig.scaledTileSize) {
                            tile.changeValues(0);
                            return;
                        }
                        tile.changeValues(4);
                    }
                });
            });
            WorldEntity.getInstance().getComponent(WorldDataComponent.class).getMapData().getSavedChunks().put(chunkKey, chunk);
        });
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
                if (chunkX >= 0 && chunkX < width && chunkY >= 0 && chunkY < height) {
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
        }
        addBoundaryWalls(WorldEntity.getInstance().getComponent(WorldDataComponent.class).getMapData().getWorld().get(chunkKey), chunkX, chunkY);
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

        int worldWidth = width * MapConfig.chunkWidth;  // Total world width in tiles
        int worldHeight = height * MapConfig.chunkHeight;  // Total world height in tiles

        // Iterate over each row of the world
        for (int row = 0; row < worldHeight; row++) {
            List<Point> meshRow = new ArrayList<>();
            for (int col = 0; col < worldWidth; col++) {
                TileEntity entity = map.getComponent(WorldDataComponent.class).getMapData().getElementAt(new Point(col * MapConfig.scaledTileSize + (double) MapConfig.scaledTileSize / 2, row * MapConfig.scaledTileSize + (double) MapConfig.scaledTileSize / 2));
                if (entity.getComponent(HitBoxComponent.class) == null) {
                    meshRow.add(entity.getComponent(CentralMassComponent.class).getCentral());
                } else {
                    meshRow.add(null);
                }
            }

            // Add the full row to the map mesh
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
                    chunk.setElement(x, y, 1); // topLeftWall
                } else if (x == MapConfig.chunkWidth - 1 && y == 0 && chunkX == 0 && chunkY == height - 1) {
                    chunk.setElement(x, y, 1); // BottomLeftWall
                } else if (x == 0 && y == MapConfig.chunkHeight - 1 && chunkX == width - 1 && chunkY == 0) {
                    chunk.setElement(x, y, 1); // topRightWall
                } else if (x == MapConfig.chunkWidth - 1 && y == MapConfig.chunkHeight - 1 && chunkX == width - 1 && chunkY == height - 1) {
                    chunk.setElement(x, y, 1); // bottomRightWall
                } else if (x == 0 && chunkY == 0) {
                    chunk.setElement(x, y, 1); // topWall
                } else if (x == MapConfig.chunkWidth - 1 && chunkY == height - 1) {
                    chunk.setElement(x, y, 1); // bottomWall
                } else if (y == 0 && chunkX == 0) {
                    chunk.setElement(x, y, 1); // leftWall
                } else if (y == MapConfig.chunkHeight - 1 && chunkX == width - 1) {
                    chunk.setElement(x, y, 1); // rightWall
                } else {
                    chunk.setElement(x, y, 4); // walkable tile
                }
            }
        }
    }
}
