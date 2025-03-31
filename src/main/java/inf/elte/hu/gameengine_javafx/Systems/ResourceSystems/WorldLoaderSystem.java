package inf.elte.hu.gameengine_javafx.Systems.ResourceSystems;

import inf.elte.hu.gameengine_javafx.Components.FilePathComponent;
import inf.elte.hu.gameengine_javafx.Components.HitBoxComponents.HitBoxComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.CentralMassComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.DimensionComponent;
import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.RenderingComponents.ImageComponent;
import inf.elte.hu.gameengine_javafx.Components.TileValueComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.MapMeshComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.TileSetComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.WorldDataComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.WorldDimensionComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Core.EntityManager;
import inf.elte.hu.gameengine_javafx.Entities.CameraEntity;
import inf.elte.hu.gameengine_javafx.Entities.TileEntity;
import inf.elte.hu.gameengine_javafx.Entities.WorldEntity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Misc.Config;
import inf.elte.hu.gameengine_javafx.Misc.MapClasses.Chunk;
import inf.elte.hu.gameengine_javafx.Misc.MapClasses.MapSaver;
import inf.elte.hu.gameengine_javafx.Misc.MapClasses.World;
import inf.elte.hu.gameengine_javafx.Misc.Tuple;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
        ArrayList<ArrayList<Integer>> data = new ArrayList<>();
        int width, height;

        // Get the instance of the world entity
        WorldEntity map = WorldEntity.getInstance();
        if (map == null) {
            return;
        }

        // Read map data from file
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(MapSaver.class.getResourceAsStream(map.getComponent(FilePathComponent.class).getFilePath()))))) {
            String line;
            String[] dimensions = reader.readLine().split(" ");
            width = Integer.parseInt(dimensions[0]);
            height = Integer.parseInt(dimensions[1]);
            WorldEntity.getInstance().getComponent(WorldDimensionComponent.class).setWorldHeight(height);
            WorldEntity.getInstance().getComponent(WorldDimensionComponent.class).setWorldWidth(width);
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(" ");
                ArrayList<Integer> row = new ArrayList<>();
                for (String value : values) {
                    row.add(Integer.parseInt(value));
                }
                data.add(row);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading file", e);
        }

        // Divide map into chunks
        int chunkWidth = Math.floorDiv(width, Config.chunkWidth);
        int chunkHeight = Math.floorDiv(height, Config.chunkHeight);
        for (int i = 0; i < chunkWidth; i++) {
            for (int j = 0; j < chunkHeight; j++) {
                Tuple<Integer, Integer> coordinates = new Tuple(i, j);
                Chunk chunkTiles = new Chunk();
                for (int y = j * Config.chunkHeight; y < height; y++) {
                    List<TileEntity> chunkRow = new ArrayList<>();
                    List<Point> meshRow = new ArrayList<>();
                    for (int x = i * Config.chunkWidth; x < width; x++) {
                        int value = data.get(y).get(x);
                        String name = map.getComponent(TileSetComponent.class).getTileLoader().getTilePath(value);
                        TileEntity tile;
                        if (name == null) {
                            name = String.valueOf(value);
                        }
                        if (!Config.wallTiles.contains(value)) {
                            // Special tile handling
                            tile = new TileEntity(value, x * Config.scaledTileSize, y * Config.scaledTileSize, "/assets/tiles/" + name + ".png", Config.scaledTileSize, Config.scaledTileSize);
                            meshRow.add(new Point(tile.getComponent(CentralMassComponent.class).getCentralX(), tile.getComponent(CentralMassComponent.class).getCentralY()));
                        } else {
                            tile = new TileEntity(value, x * Config.scaledTileSize, y * Config.scaledTileSize, "/assets/tiles/" + name + ".png", Config.scaledTileSize, Config.scaledTileSize, true);
                            meshRow.add(null);
                        }
                        chunkRow.add(tile);
                    }
                    chunkTiles.getChunk().add(chunkRow);
                    map.getComponent(MapMeshComponent.class).addRow(meshRow);
                }
                map.getComponent(WorldDataComponent.class).getMapData().getWorld().putIfAbsent(coordinates, chunkTiles);
            }
        }
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
