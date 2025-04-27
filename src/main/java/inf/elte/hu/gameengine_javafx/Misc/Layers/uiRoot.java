package inf.elte.hu.gameengine_javafx.Misc.Layers;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

/**
 * A singleton class extending {@link Pane} that represents the root of the UI layer in the game.
 * <p>
 * The {@code uiRoot} class is responsible for managing the user interface elements
 * in the game. It provides functionality to clear all children from the UI layer
 * and is designed to be used as a singleton.
 * </p>
 */
public class uiRoot extends Pane {
    private static uiRoot instance;

    /**
     * Private constructor to prevent external instantiation. Initializes the UI root.
     */
    private uiRoot() {
        super();
    }

    /**
     * Returns the singleton instance of {@code uiRoot}.
     * If the instance has not been created yet, it will be created and returned.
     *
     * @return the singleton instance of {@code uiRoot}
     */
    public static uiRoot getInstance() {
        if (instance == null) {
            instance = new uiRoot();
        }
        return instance;
    }

    /**
     * Clears all children from the UI root. This method is executed asynchronously
     * on the JavaFX application thread using {@link Platform#runLater(Runnable)}.
     */
    public void unloadAll() {
        Platform.runLater(() -> this.getChildren().clear());
    }

    /**
     * Clears a Node from the UI root.This method is executed asynchronously
     * on the JavaFX application thread using {@link Platform#runLater(Runnable)}.
     *
     * @param element the Node to be removed.
     */
    public void unload(Node element) {
        Platform.runLater(() -> this.getChildren().remove(element));
    }
}
