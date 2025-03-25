package inf.elte.hu.gameengine_javafx.Misc;

/**
 * Abstract base class for the game loop.
 * <br>
 * This class runs the main game loop in a separate thread, handling frame updates and enforcing a frames-per-second (FPS) cap.
 * <br>
 * Subclasses must implement the {@link #update()} method to define the logic that should be executed on every frame.
 */
public abstract class GameLoop extends Thread {
    private boolean running;
    private final Time time;

    /**
     * Constructs a new {@code GameLoop} instance.
     * <br>
     * This initializes the game loop with a default FPS cap of 144.
     */
    public GameLoop() {
        this.running = false;
        this.time = Time.getInstance();
        time.setFPSCap(144);
    }

    /**
     * Starts the game loop.
     * <br>
     * This method starts the loop by invoking the {@link #run()} method in a new thread.
     */
    public void startLoop() {
        running = true;
        this.start();
    }

    /**
     * Stops the game loop.
     * <br>
     * This method halts the loop, effectively ending the thread's execution.
     */
    public void stopLoop() {
        running = false;
    }

    /**
     * The main loop execution method.
     * <br>
     * The loop continues running as long as {@link #running} is {@code true}, updating time, invoking the {@link #update()} method,
     * and ensuring the FPS cap is adhered to.
     */
    @Override
    public void run() {
        while (running) {
            long frameStartTime = System.nanoTime();
            time.update();

            update();

            if (time.isFPSCapEnabled()) {
                long frameDuration = System.nanoTime() - frameStartTime;
                long sleepTime = (1_000_000_000L / time.getFPSCap()) - frameDuration;

                if (sleepTime > 0) {
                    try {
                        Thread.sleep(sleepTime / 1_000_000, (int) (sleepTime % 1_000_000));
                    } catch (InterruptedException e) {

                    }
                } else {
                    Thread.yield();
                }
            }
        }
    }

    /**
     * Abstract method to be implemented by subclasses to define the frame update logic.
     * <br>
     * This method is called every frame after time updates, and it contains the game's primary logic to execute during each frame.
     */
    public abstract void update();

    public void setRunning(boolean b) {
        running = b;
    }
}
