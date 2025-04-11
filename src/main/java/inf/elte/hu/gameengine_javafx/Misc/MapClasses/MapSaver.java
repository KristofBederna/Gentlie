package inf.elte.hu.gameengine_javafx.Misc.MapClasses;

import inf.elte.hu.gameengine_javafx.Components.TileValueComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.WorldDataComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.WorldDimensionComponent;
import inf.elte.hu.gameengine_javafx.Entities.WorldEntity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Misc.Config;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class MapSaver {
    public static void saveMap(WorldEntity map, String path) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            int width = (int) map.getComponent(WorldDimensionComponent.class).getWorldWidth();
            int height = (int) map.getComponent(WorldDimensionComponent.class).getWorldHeight();

            writer.write(width + " " + height);
            writer.newLine();

            for (int x = 0; x < height; x++) {
                for (int y = 0; y < width; y++) {
                    writer.write(
                            map.getComponent(WorldDataComponent.class)
                                    .getMapData()
                                    .getElementAt(new Point(y * Config.scaledTileSize + Config.scaledTileSize / 2,
                                            x * Config.scaledTileSize + Config.scaledTileSize / 2))
                                    .getComponent(TileValueComponent.class)
                                    .getTileValue() + " "
                    );
                }
                if (x < height - 1) {
                    writer.newLine();
                }
            }
        }
    }

}
