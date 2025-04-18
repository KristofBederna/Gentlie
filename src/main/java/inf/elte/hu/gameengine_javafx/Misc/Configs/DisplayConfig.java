package inf.elte.hu.gameengine_javafx.Misc.Configs;

import inf.elte.hu.gameengine_javafx.Misc.Tuple;

public class DisplayConfig {
    public static String windowTitle = "Game Engine";
    public static boolean renderDebugMode = false;
    public static boolean fullScreenMode = false;
    public static Tuple<Double, Double> resolution = new Tuple<>(1920.0, 1080.0);
    public static double relativeWidthRatio = resolution.first() / 1920;
    public static double relativeHeightRatio = resolution.second() / 1080;

    public static int fpsCap = 144;

    public static void setRelativeAspectRatio() {
        relativeWidthRatio = resolution.first() / 1920;
        relativeHeightRatio = resolution.second() / 1080;
    }
}
