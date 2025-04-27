package inf.elte.hu.gameengine_javafx.Misc.Configs;

/**
 * Config class holding global static variables needed for resource management and sound.
 */
public class ResourceConfig {
    //Sound
    public static float backgroundMusicVolume = 0.0f;
    public static float masterVolume = 1.0f;
    public static boolean linearVolumeControl = false;

    //Resources
    public static long resourceUnloadThresholdTime = 10000;
}
