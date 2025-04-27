package inf.elte.hu.gameengine_javafx.Misc.Configs;

/**
 * Config class holding global static variables needed for physics and mathematical calculations.
 */
public class PhysicsConfig {
    public static double EPSILON = 1e-9;

    public static double defaultDrag = 0.001;
    public static double defaultFriction = 0.001;
    public static double defaultMass = 1.0;
    public static final double fixedDeltaTime = 1.0 / 60.0; // 60 FPS physics
}
