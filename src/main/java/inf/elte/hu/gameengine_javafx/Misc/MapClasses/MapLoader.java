package inf.elte.hu.gameengine_javafx.Misc.MapClasses;

import inf.elte.hu.gameengine_javafx.Components.FilePathComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.CentralMassComponent;
import inf.elte.hu.gameengine_javafx.Components.TileValueComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.MapMeshComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.WorldDataComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.WorldDimensionComponent;
import inf.elte.hu.gameengine_javafx.Entities.TileEntity;
import inf.elte.hu.gameengine_javafx.Entities.WorldEntity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Misc.Configs.MapConfig;
import inf.elte.hu.gameengine_javafx.Misc.Tuple;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapLoader {

    public static void loadMap() {
        WorldEntity map = WorldEntity.getInstance();
        if (map == null) return;

        List<List<Integer>> data = new ArrayList<>();
        Tuple<Integer, Integer> dimensions = readMapData(map, data);

        setWorldDimensions(dimensions);
        createChunks(data, dimensions, map);
    }

    private static Tuple<Integer, Integer> readMapData(WorldEntity map, List<List<Integer>> data) {
        String filePath = map.getComponent(FilePathComponent.class).getFilePath();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        Objects.requireNonNull(MapSaver.class.getResourceAsStream(filePath)))
        )) {
            String[] dims = reader.readLine().split(" ");
            int width = Integer.parseInt(dims[0]);
            int height = Integer.parseInt(dims[1]);

            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(" ");
                List<Integer> row = new ArrayList<>();
                for (String value : values) {
                    row.add(Integer.parseInt(value));
                }
                data.add(row);
            }
            return new Tuple<>(width, height);

        } catch (IOException e) {
            throw new RuntimeException("Error reading file", e);
        }
    }

    private static void setWorldDimensions(Tuple<Integer, Integer> dimensions) {
        WorldDimensionComponent dimComp = WorldEntity.getInstance().getComponent(WorldDimensionComponent.class);
        dimComp.setWorldWidth(dimensions.first());
        dimComp.setWorldHeight(dimensions.second());
    }

    private static void createChunks(List<List<Integer>> data, Tuple<Integer, Integer> dimensions, WorldEntity map) {
        int width = dimensions.first();
        int height = dimensions.second();
        int chunkWidth = Math.floorDiv(width, MapConfig.chunkWidth);
        int chunkHeight = Math.floorDiv(height, MapConfig.chunkHeight);

        for (int i = 0; i < chunkWidth; i++) {
            for (int j = 0; j < chunkHeight; j++) {
                createChunkAt(i, j, data, map, width, height);
            }
        }
    }

    private static void createChunkAt(int chunkX, int chunkY, List<List<Integer>> data, WorldEntity map, int width, int height) {
        Tuple<Integer, Integer> coordinates = new Tuple<>(chunkX, chunkY);
        Chunk chunkTiles = new Chunk();

        for (int y = chunkY * MapConfig.chunkHeight; y < Math.min((chunkY + 1) * MapConfig.chunkHeight, height); y++) {
            List<TileEntity> chunkRow = new ArrayList<>();
            List<Point> meshRow = new ArrayList<>();

            for (int x = chunkX * MapConfig.chunkWidth; x < Math.min((chunkX + 1) * MapConfig.chunkWidth, width); x++) {
                TileEntity tile = createTileEntity(data.get(y).get(x), x, y);
                chunkRow.add(tile);

                if (!MapConfig.wallTiles.contains(tile.getComponent(TileValueComponent.class).getTileValue())) {
                    CentralMassComponent cm = tile.getComponent(CentralMassComponent.class);
                    meshRow.add(new Point(cm.getCentralX(), cm.getCentralY()));
                } else {
                    meshRow.add(null);
                }
            }

            chunkTiles.getChunk().add(chunkRow);
            map.getComponent(MapMeshComponent.class).addToRow(y, meshRow);
        }

        map.getComponent(WorldDataComponent.class).getMapData().getWorld().putIfAbsent(coordinates, chunkTiles);
        map.getComponent(WorldDataComponent.class).getMapData().getSavedChunks().putIfAbsent(coordinates, chunkTiles);
    }

    private static TileEntity createTileEntity(int value, int x, int y) {
        String name = TileLoader.getTilePath(value);
        if (name == null) {
            name = String.valueOf(value);
        }

        String path = "/assets/tiles/" + name + ".png";
        double tileX = x * MapConfig.scaledTileSize;
        double tileY = y * MapConfig.scaledTileSize;

        if (MapConfig.wallTiles.contains(value)) {
            return new TileEntity(value, tileX, tileY, path, MapConfig.scaledTileSize, MapConfig.scaledTileSize);
        } else {
            return new TileEntity(value, tileX, tileY, path, MapConfig.scaledTileSize, MapConfig.scaledTileSize);
        }
    }
}
