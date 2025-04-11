package inf.elte.hu.gameengine_javafx.Misc.MapClasses;

import inf.elte.hu.gameengine_javafx.Components.FilePathComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.CentralMassComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.MapMeshComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.TileSetComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.WorldDataComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.WorldDimensionComponent;
import inf.elte.hu.gameengine_javafx.Entities.TileEntity;
import inf.elte.hu.gameengine_javafx.Entities.WorldEntity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Misc.Config;
import inf.elte.hu.gameengine_javafx.Misc.Tuple;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MapLoader {
    public static void loadMap() {
        ArrayList<ArrayList<Integer>> data = new ArrayList<>();
        int width, height;

        WorldEntity map = WorldEntity.getInstance();
        if (map == null) {
            return;
        }

        String filePath = map.getComponent(FilePathComponent.class).getFilePath();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getInputStream(filePath)))) {
            String line;
            String[] dimensions = reader.readLine().split(" ");
            width = Integer.parseInt(dimensions[0]);
            height = Integer.parseInt(dimensions[1]);
            map.getComponent(WorldDimensionComponent.class).setWorldHeight(height);
            map.getComponent(WorldDimensionComponent.class).setWorldWidth(width);

            while ((line = reader.readLine()) != null) {
                String[] values = line.split(" ");
                ArrayList<Integer> row = new ArrayList<>();
                for (String value : values) {
                    row.add(Integer.parseInt(value));
                }
                data.add(row);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading map file", e);
        }

        // Divide into chunks
        int chunkWidth = Math.floorDiv(width, Config.chunkWidth);
        int chunkHeight = Math.floorDiv(height, Config.chunkHeight);
        for (int i = 0; i < chunkWidth; i++) {
            for (int j = 0; j < chunkHeight; j++) {
                Tuple<Integer, Integer> coordinates = new Tuple<>(i, j);
                Chunk chunkTiles = new Chunk();
                for (int y = j * Config.chunkHeight; y < height; y++) {
                    List<TileEntity> chunkRow = new ArrayList<>();
                    List<Point> meshRow = new ArrayList<>();
                    for (int x = i * Config.chunkWidth; x < width; x++) {
                        int value = data.get(y).get(x);
                        String name = map.getComponent(TileSetComponent.class).getTileLoader().getTilePath(value);
                        if (name == null) name = String.valueOf(value);
                        TileEntity tile;
                        if (!Config.wallTiles.contains(value)) {
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
     * Tries to open an InputStream from classpath or directly from filesystem.
     */
    private static InputStream getInputStream(String filePath) throws IOException {
        InputStream stream = MapLoader.class.getResourceAsStream(filePath);
        if (stream != null) {
            return stream;
        }

        File file = new File(filePath);
        if (file.exists()) {
            return new FileInputStream(file);
        }

        throw new FileNotFoundException("Map file not found in resources or filesystem: " + filePath);
    }
}
