package inf.elte.hu.gameengine_javafx.Misc.InputHandlers;

import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Entities.CameraEntity;
import inf.elte.hu.gameengine_javafx.Misc.Layers.GameCanvas;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Singleton class for handling mouse input events.
 * <br>
 * Tracks mouse button presses, releases, position, and scroll events.
 */
public class MouseInputHandler {
    private static MouseInputHandler instance;
    private final Set<MouseButton> pressedButtons = new HashSet<>();
    private final Set<MouseButton> releasedButtons = new HashSet<>();
    private final Map<MouseButton, Long> lastPressedTime = new HashMap<>();

    private double mouseX, mouseY;
    private double scrollDeltaY;

    /**
     * Private constructor that initializes mouse event handlers for the game scene.
     */
    private MouseInputHandler() {
        Scene scene = GameCanvas.getInstance().getScene();
        scene.setOnMousePressed(this::mousePressed);
        scene.setOnMouseReleased(this::mouseReleased);
        scene.setOnMouseMoved(this::mouseMoved);
        scene.setOnMouseDragged(this::mouseMoved);
        scene.setOnScroll(this::mouseScrolled);
    }

    private MouseInputHandler(boolean test) {

    }

    /**
     * Returns the singleton instance of {@code MouseInputHandler}.
     * If no instance exists, a new one is created.
     *
     * @return The singleton instance of {@code MouseInputHandler}.
     */
    public static MouseInputHandler getInstance() {
        if (instance == null) {
            instance = new MouseInputHandler();
        }
        return instance;
    }

    public static MouseInputHandler getInstance(boolean test) {
        if (instance == null) {
            instance = new MouseInputHandler(test);
        }
        return instance;
    }

    /**
     * Handles mouse button press events, tracking the button and its press time.
     *
     * @param event The {@code MouseEvent} triggered by a mouse button press.
     */
    private void mousePressed(MouseEvent event) {
        long currentTime = System.currentTimeMillis();
        MouseButton button = event.getButton();

        if (!pressedButtons.contains(button)) {
            lastPressedTime.put(button, currentTime);
            pressedButtons.add(button);
            releasedButtons.clear();
        }
    }

    /**
     * Handles mouse button release events, moving the button from pressed to released state.
     *
     * @param event The {@code MouseEvent} triggered by a mouse button release.
     */
    private void mouseReleased(MouseEvent event) {
        MouseButton button = event.getButton();
        pressedButtons.remove(button);
        releasedButtons.add(button);
    }

    /**
     * Updates the mouse position based on the camera's global position.
     *
     * @param event The {@code MouseEvent} triggered by a mouse movement or drag.
     */
    private void mouseMoved(MouseEvent event) {
        CameraEntity camera = CameraEntity.getInstance();

        if (camera == null) {
            return;
        }

        mouseX = event.getX() + camera.getComponent(PositionComponent.class).getGlobalX();
        mouseY = event.getY() + camera.getComponent(PositionComponent.class).getGlobalY();
    }

    /**
     * Handles mouse scroll events and updates the scroll delta value.
     *
     * @param event The {@code ScrollEvent} triggered by a mouse scroll action.
     */
    private void mouseScrolled(ScrollEvent event) {
        scrollDeltaY = event.getDeltaY();
    }

    /**
     * Checks if a specific mouse button is currently pressed.
     *
     * @param button The mouse button to check.
     * @return {@code true} if the button is currently pressed, {@code false} otherwise.
     */
    public boolean isButtonPressed(MouseButton button) {
        return pressedButtons.contains(button);
    }

    /**
     * Checks if a specific mouse button was released since the last check.
     * <br>
     * If the button was released, it is removed from the released buttons set.
     *
     * @param button The mouse button to check.
     * @return {@code true} if the button was released, {@code false} otherwise.
     */
    public boolean isButtonReleased(MouseButton button) {
        boolean wasReleased = releasedButtons.contains(button);
        if (wasReleased) {
            releasedButtons.remove(button);
        }
        return wasReleased;
    }

    /**
     * Returns the current x-coordinate of the mouse relative to the game world.
     *
     * @return The x-coordinate of the mouse.
     */
    public double getMouseX() {
        return mouseX;
    }

    /**
     * Returns the current y-coordinate of the mouse relative to the game world.
     *
     * @return The y-coordinate of the mouse.
     */
    public double getMouseY() {
        return mouseY;
    }

    /**
     * Returns the amount of vertical scrolling since the last scroll event.
     *
     * @return The vertical scroll delta value.
     */
    public double getScrollDeltaY() {
        return scrollDeltaY;
    }

    public void reset() {
        pressedButtons.clear();
        releasedButtons.clear();
        lastPressedTime.clear();
        mouseX = mouseY = scrollDeltaY = 0;
    }
}
