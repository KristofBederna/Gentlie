package inf.elte.hu.gameengine_javafx.Misc.Scenes;

import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * Abstract base class for game scenes.
 * <br>
 * This class extends {@link Scene} and provides a framework for initializing and tearing down game-specific scenes.
 */
public abstract class GameScene extends Scene {

    /**
     * Constructs a new {@code GameScene} with the specified parent node, width, and height.
     *
     * @param parent The root node of the scene.
     * @param width  The width of the scene in pixels.
     * @param height The height of the scene in pixels.
     */
    public GameScene(Parent parent, double width, double height) {
        super(parent, width, height);
    }

    /**
     * Abstract method for setting up the scene.
     * <br>
     * This method should be implemented to initialize necessary components when the scene is loaded.
     */
    public abstract void setup();

    /**
     * Abstract method for cleaning up the scene.
     * <br>
     * This method should be implemented to release resources or perform any necessary teardown before the scene is unloaded.
     */
    public abstract void breakdown();
}
