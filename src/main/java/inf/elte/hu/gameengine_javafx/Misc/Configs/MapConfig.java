package inf.elte.hu.gameengine_javafx.Misc.Configs;

import java.util.ArrayList;
import java.util.List;

/**
 * Config class holding global static variables needed for the map, map generation and tiles.
 */
public class MapConfig {
    private static final int tileSize = 100;
    private static double tileScale = 1.0;
    public static double scaledTileSize = tileSize * tileScale;


    public static List<Integer> wallTiles = new ArrayList<>(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8));

    public static int defaultTileCode = 0;
    public static int topLeftWallCode = 3;
    public static int bottomRightWallCode = 2;
    public static int topRightWallCode = 4;
    public static int bottomLeftWallCode = 1;
    public static int bottomWallCode = 8;
    public static int topWallCode = 7;
    public static int leftWallCode = 5;
    public static int rightWallCode = 6;
    public static int walkableTileCode = 9;

    public static int chunkWidth = 16;
    public static int chunkHeight = 16;
    public static int loadDistance = 2;

    public static void setTileScale(double newTileScale) {
        tileScale = newTileScale;
        scaledTileSize = tileSize * tileScale;
    }


    public static double getTileScale() {
        return tileScale;
    }
}
