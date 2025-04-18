package inf.elte.hu.gameengine_javafx.Misc.InputHandlers;

import inf.elte.hu.gameengine_javafx.Misc.Layers.GameCanvas;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * Singleton class for handling keyboard input events.
 * <br>
 * This class tracks pressed and released keys and provides methods to check their status.
 */
public class KeyboardInputHandler {
    private static KeyboardInputHandler instance = null;
    private final Set<KeyCode> pressedKeys = new HashSet<>();
    private final Set<KeyCode> releasedKeys = new HashSet<>();

    /**
     * Private constructor that initializes key event handlers for the game scene.
     */
    private KeyboardInputHandler() {
        Scene scene = GameCanvas.getInstance().getScene();
        scene.setOnKeyPressed(this::keyPressed);
        scene.setOnKeyReleased(this::keyReleased);
    }

    private KeyboardInputHandler(boolean test) {

    }

    /**
     * Returns the singleton instance of the {@code KeyboardInputHandler}.
     * If no instance exists, a new one is created.
     *
     * @return The singleton instance of {@code KeyboardInputHandler}.
     */
    public static KeyboardInputHandler getInstance() {
        if (instance == null) {
            instance = new KeyboardInputHandler();
        }
        return instance;
    }

    public static KeyboardInputHandler getInstance(boolean test) {
        if (instance == null) {
            instance = new KeyboardInputHandler(test);
        }
        return instance;
    }

    /**
     * Handles key press events, adding the key to the pressed keys set.
     *
     * @param event The {@code KeyEvent} triggered by a key press.
     */
    private void keyPressed(KeyEvent event) {
        KeyCode key = event.getCode();

        if (!pressedKeys.contains(key)) {
            pressedKeys.add(key);
            releasedKeys.remove(key);
        }
    }

    /**
     * Handles key release events, moving the key from pressed to released keys set.
     *
     * @param event The {@code KeyEvent} triggered by a key release.
     */
    private void keyReleased(KeyEvent event) {
        KeyCode key = event.getCode();
        pressedKeys.remove(key);
        releasedKeys.add(key);
    }

    /**
     * Checks if a specific key is currently pressed.
     *
     * @param keyCode The key to check.
     * @return {@code true} if the key is currently pressed, {@code false} otherwise.
     */
    public boolean isKeyPressed(KeyCode keyCode) {
        return pressedKeys.contains(keyCode);
    }

    /**
     * Checks if a specific key was released since the last check.
     * <br>
     * If the key was released, it is removed from the released keys set.
     *
     * @param keyCode The key to check.
     * @return {@code true} if the key was released, {@code false} otherwise.
     */
    public boolean isKeyReleased(KeyCode keyCode) {
        boolean wasReleased = releasedKeys.contains(keyCode);
        if (wasReleased) {
            releasedKeys.remove(keyCode);
        }
        return wasReleased;
    }

    /**
     * Returns a set of currently pressed keys.
     *
     * @return A {@code Set} containing the currently pressed keys.
     */
    public Set<KeyCode> getPressedKeys() {
        return new HashSet<>(pressedKeys);
    }
}
