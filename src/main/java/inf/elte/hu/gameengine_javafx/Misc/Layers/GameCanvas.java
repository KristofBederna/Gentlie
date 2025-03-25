package inf.elte.hu.gameengine_javafx.Misc.Layers;

import javafx.scene.canvas.Canvas;

/**
 * A singleton class that extends {@link Canvas} to represent the game's drawing surface.
 * <p>
 * This class provides a single instance of a {@link Canvas} that can be used for rendering the game scene.
 * It ensures that only one instance of {@code GameCanvas} is created throughout the game.
 * </p>
 */
public class GameCanvas extends Canvas {
    private static GameCanvas instance;

    /**
     * Private constructor to prevent external instantiation. Initializes the canvas with the given width and height.
     *
     * @param width  the width of the canvas
     * @param height the height of the canvas
     */
    private GameCanvas(double width, double height) {
        super(width, height);
    }

    /**
     * Creates and returns the singleton instance of {@code GameCanvas}.
     * If the instance has already been created, it returns the existing instance.
     *
     * @param width  the width of the canvas
     * @param height the height of the canvas
     * @return the singleton instance of {@code GameCanvas}
     */
    public static GameCanvas createInstance(double width, double height) {
        if (instance == null) {
            instance = new GameCanvas(width, height);
        }
        return instance;
    }

    /**
     * Returns the singleton instance of {@code GameCanvas}.
     *
     * @return the singleton instance of {@code GameCanvas}
     */
    public static GameCanvas getInstance() {
        return instance;
    }
}
