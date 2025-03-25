package inf.elte.hu.gameengine_javafx.Misc;

/**
 * The {@code Time} class provides methods to manage and track the passage of time in the game.
 * <br>
 * It allows for precise frame time tracking, delta time calculations, and FPS cap management.
 * <br>
 * This class is implemented as a singleton and provides functionality such as scaling time, calculating FPS, and updating delta times.
 */
public class Time {
    private static final Time instance = new Time();

    private long lastFrameTime;
    private double deltaTime;
    private double unscaledDeltaTime;
    private double timeScale = 1.0;
    private long startTime;

    private boolean fpsCapEnabled = false;
    private int fpsCap = 60;
    private long targetFrameTime = 16_666_667L;
    private long lastFPSUpdate;
    private int fps;
    private long frameCount;

    /**
     * Private constructor for the {@code Time} singleton.
     * Initializes the time tracking variables and calculates the first frame time.
     */
    private Time() {
        lastFrameTime = System.nanoTime();
        startTime = lastFrameTime;
        frameCount = 0;
    }

    /**
     * Gets the single instance of the {@code Time} class.
     * <br>
     * This method ensures that only one instance of the {@code Time} class exists throughout the application.
     *
     * @return the single instance of {@code Time}.
     */
    public static Time getInstance() {
        return instance;
    }

    /**
     * Updates the time-related data, such as {@code deltaTime}, {@code unscaledDeltaTime}, and frame count.
     * <br>
     * This method should be called every frame to keep track of the time elapsed since the last frame.
     */
    public void update() {
        long currentTime = System.nanoTime();
        long elapsedTime = currentTime - lastFrameTime;
        lastFrameTime = currentTime;
        frameCount++;

        unscaledDeltaTime = elapsedTime / 1_000_000_000.0;
        deltaTime = unscaledDeltaTime * timeScale;

        if (fpsCapEnabled) {
            applyFPSCap(currentTime);
        }
    }

    /**
     * Applies the FPS cap by sleeping for the appropriate amount of time to ensure the target frame time is met.
     * <br>
     * This helps maintain a stable FPS and prevents the game from running too fast.
     *
     * @param frameStartTime the start time of the current frame.
     */
    private void applyFPSCap(long frameStartTime) {
        long frameDuration = System.nanoTime() - frameStartTime;
        long sleepTime = targetFrameTime - frameDuration;

        if (sleepTime > 0) {
            try {
                Thread.sleep(sleepTime / 1_000_000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sets the FPS cap for the game.
     * <br>
     * This method controls the maximum number of frames per second that the game will run at.
     *
     * @param fps the target frames per second.
     */
    public void setFPSCap(int fps) {
        if (fps > 0) {
            this.fpsCap = fps;
            this.targetFrameTime = 1_000_000_000L / fps;
            this.fpsCapEnabled = true;
        } else {
            this.fpsCapEnabled = false;
        }
    }

    /**
     * Checks if the FPS cap is enabled.
     *
     * @return {@code true} if the FPS cap is enabled, otherwise {@code false}.
     */
    public boolean isFPSCapEnabled() {
        return fpsCapEnabled;
    }

    /**
     * Gets the FPS cap value.
     *
     * @return the FPS cap, or -1 if the FPS cap is not enabled.
     */
    public int getFPSCap() {
        return fpsCapEnabled ? fpsCap : -1;
    }

    /**
     * Gets the delta time since the last frame, adjusted for the time scale.
     * <br>
     * This value represents the amount of time passed between frames, which is useful for frame-rate independent movement and animations.
     *
     * @return the delta time in seconds.
     */
    public double getDeltaTime() {
        return deltaTime;
    }

    /**
     * Gets the unscaled delta time since the last frame.
     * <br>
     * This value represents the raw time elapsed between frames, before applying the time scale.
     *
     * @return the unscaled delta time in seconds.
     */
    public double getUnscaledDeltaTime() {
        return unscaledDeltaTime;
    }

    /**
     * Sets the time scale for the game.
     * <br>
     * A time scale greater than 1 speeds up time, while a time scale less than 1 slows it down.
     *
     * @param scale the time scale.
     */
    public void setTimeScale(double scale) {
        this.timeScale = Math.max(0, scale);
    }

    /**
     * Gets the current time scale.
     *
     * @return the time scale.
     */
    public double getTimeScale() {
        return timeScale;
    }

    /**
     * Gets the total elapsed time since the start of the game.
     *
     * @return the elapsed time in seconds.
     */
    public double getElapsedTime() {
        return (System.nanoTime() - startTime) / 1_000_000_000.0;
    }

    /**
     * Gets the current FPS (frames per second).
     * <br>
     * This value is updated every second and represents the average frame rate over the past second.
     *
     * @return the current FPS.
     */
    public int getFPS() {
        if (System.nanoTime() - lastFPSUpdate >= 1_000_000_000L) {
            fps = (int) frameCount;
            frameCount = 0;
            lastFPSUpdate = System.nanoTime();
        }
        if (fpsCapEnabled && fps > fpsCap) {
            fps = fpsCap;
        }
        return fps;
    }
}
