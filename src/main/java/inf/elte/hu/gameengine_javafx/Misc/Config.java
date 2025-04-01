package inf.elte.hu.gameengine_javafx.Misc;

import java.util.ArrayList;
import java.util.List;

public class Config {
    private static final int tileSize = 100;
    private static double tileScale = 1.0;
    public static double scaledTileSize = tileSize * tileScale;
    public static double gameCanvasWidth = 1920;
    public static double gameCanvasHeight = 1080;
    public static String windowTitle = "Game Engine";
    public static double EPSILON = 1e-9;
    public static int chunkWidth = 16;
    public static int chunkHeight = 16;
    public static int loadDistance = 2;
    public static double drag = 0.001;
    public static double friction = 0.001;
    public static boolean renderDebugMode = false;
    public static boolean fullScreenMode = false;
    public static List<Integer> wallTiles = new ArrayList<>(List.of(0, 1, 3));
    public static double dynamicMapWalkability = 0.63;

    public static void setTileScale(double newTileScale) {
        tileScale = newTileScale;
        scaledTileSize = tileSize * tileScale;
    }

    public static double getTileScale() {
        return tileScale;
    }
}
