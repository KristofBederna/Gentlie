package inf.elte.hu.gameengine_javafx.Misc.MapClasses;

import inf.elte.hu.gameengine_javafx.Components.TileValueComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.WorldDataComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.WorldDimensionComponent;
import inf.elte.hu.gameengine_javafx.Entities.WorldEntity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Misc.Configs.MapConfig;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class MapSaver {

    public static void saveMap(WorldEntity map, String path) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            int width = getWidth(map);
            int height = getHeight(map);

            writeDimensions(writer, width, height);
            writeMapData(writer, map, width, height);
        }
    }

    private static int getWidth(WorldEntity map) {
        return (int) map.getComponent(WorldDimensionComponent.class).getWorldWidth();
    }

    private static int getHeight(WorldEntity map) {
        return (int) map.getComponent(WorldDimensionComponent.class).getWorldHeight();
    }

    private static void writeDimensions(BufferedWriter writer, int width, int height) throws IOException {
        writer.write(width + " " + height);
        writer.newLine();
    }

    private static void writeMapData(BufferedWriter writer, WorldEntity map, int width, int height) throws IOException {
        for (int x = 0; x < height; x++) {
            StringBuilder row = new StringBuilder();
            for (int y = 0; y < width; y++) {
                int tileValue = getTileValueAt(map, x, y);
                row.append(tileValue).append(" ");
            }
            writer.write(row.toString().trim());
            if (x < height - 1) {
                writer.newLine();
            }
        }
    }

    private static int getTileValueAt(WorldEntity map, int x, int y) {
        Point point = new Point(
                y * MapConfig.scaledTileSize + MapConfig.scaledTileSize / 2,
                x * MapConfig.scaledTileSize + MapConfig.scaledTileSize / 2
        );

        return map.getComponent(WorldDataComponent.class)
                .getMapData()
                .getElementAt(point)
                .getComponent(TileValueComponent.class)
                .getTileValue();
    }
}
