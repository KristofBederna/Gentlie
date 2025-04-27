package inf.elte.hu.gameengine_javafx.Misc.Layers;

import javafx.scene.layout.StackPane;

/**
 * A singleton class that extends {@link StackPane} to represent a game layer
 * containing the game canvas and UI root.
 * <p>
 * This class is used to manage the rendering layer of the game. It contains the
 * {@link GameCanvas} for drawing the game world and the {@link uiRoot} for managing the user interface.
 * </p>
 */
public class GameLayer extends StackPane {
    private static GameLayer instance;
    private final GameCanvas canvas;
    private final uiRoot UI;

    /**
     * Private constructor to prevent external instantiation. Initializes the game layer
     * with a canvas and a UI root.
     */
    private GameLayer() {
        super();
        canvas = GameCanvas.getInstance();
        UI = uiRoot.getInstance();
    }

    /**
     * Returns the singleton instance of {@code GameLayer}.
     * If the instance has not been created yet, it will be created and returned.
     *
     * @return the singleton instance of {@code GameLayer}
     */
    public static GameLayer getInstance() {
        if (instance == null) {
            instance = new GameLayer();
        }
        return instance;
    }

    /**
     * Returns the {@code GameCanvas} used for rendering the game world.
     *
     * @return the {@code GameCanvas} instance
     */
    public GameCanvas getCanvas() {
        return canvas;
    }

    /**
     * Returns the {@code uiRoot} used for managing the user interface.
     *
     * @return the {@code uiRoot} instance
     */
    public uiRoot getUIRoot() {
        return UI;
    }
}
