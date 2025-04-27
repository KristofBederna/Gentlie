package inf.elte.hu.gameengine_javafx.Systems;

import inf.elte.hu.gameengine_javafx.Components.InteractiveComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Misc.InputHandlers.KeyboardInputHandler;
import inf.elte.hu.gameengine_javafx.Misc.InputHandlers.MouseInputHandler;
import inf.elte.hu.gameengine_javafx.Misc.Tuple;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The InputHandlingSystem processes keyboard and mouse input for interactive entities.
 * It maps keyboard and mouse button presses to actions and counteractions defined in
 * the entities' InteractiveComponent.
 */
public class InputHandlingSystem extends GameSystem {

    /**
     * Starts the input handling system, setting it as active.
     */
    @Override
    public void start() {
        this.active = true;
    }

    /**
     * Updates the input handling system. It processes all entities with an
     * InteractiveComponent to check and handle user input.
     */
    @Override
    public void update() {
        var entitiesSnapshot = EntityHub.getInstance().getEntitiesWithComponent(InteractiveComponent.class);

        if (entitiesSnapshot.isEmpty()) {
            return;
        }

        for (Entity entity : entitiesSnapshot) {
            if (entity == null) {
                continue;
            }
            processEntity(entity);
        }
    }

    /**
     * Processes input for a given entity. This involves handling both keyboard and
     * mouse inputs through their respective handlers.
     *
     * @param entity The entity whose input needs to be processed.
     */
    private void processEntity(Entity entity) {
        if (entity == null) return;
        InteractiveComponent interactive = entity.getComponent(InteractiveComponent.class);

        if (interactive != null) {
            handleKeyboardInput(interactive);
            handleMouseInput(interactive);
        }
    }

    /**
     * Handles keyboard input for a given entity. It processes key press and release events
     * and executes the corresponding actions or counteractions as defined in the entity's
     * InteractiveComponent.
     *
     * @param interactive The InteractiveComponent containing the key mappings and actions.
     */
    private void handleKeyboardInput(InteractiveComponent interactive) {
        KeyboardInputHandler keyboardInputHandler = KeyboardInputHandler.getInstance();
        List<Map.Entry<KeyCode, Tuple<Runnable, Runnable>>> snapshot = new ArrayList<>(interactive.getKeyInputMapping().entrySet());
        Map<Tuple<KeyCode, MouseButton>, Tuple<Long, Long>> lastTimeCalled = interactive.getLastTimeCalled();

        for (Map.Entry<KeyCode, Tuple<Runnable, Runnable>> entry : snapshot) {
            KeyCode keyCode = entry.getKey();
            Runnable action = entry.getValue().first();
            Runnable counterAction = entry.getValue().second();

            if (keyboardInputHandler.isKeyPressed(keyCode)) {
                if (System.currentTimeMillis() > lastTimeCalled.get(new Tuple<>(keyCode, null)).first() + lastTimeCalled.get(new Tuple<>(keyCode, null)).second()) {
                    action.run();
                    lastTimeCalled.put(new Tuple<>(keyCode, null), new Tuple<>(System.currentTimeMillis(), lastTimeCalled.get(new Tuple<>(keyCode, null)).second()));
                }
            }
            if (keyboardInputHandler.isKeyReleased(keyCode) && counterAction != null) {
                counterAction.run();
            }
        }
    }

    /**
     * Handles mouse input for a given entity. It processes mouse button press and release events
     * and invokes the corresponding actions or counteractions defined in the entity's
     * InteractiveComponent.
     *
     * @param interactive The InteractiveComponent containing the mouse button mappings and actions.
     */
    private void handleMouseInput(InteractiveComponent interactive) {
        MouseInputHandler mouseInputHandler = MouseInputHandler.getInstance();
        Map<Tuple<KeyCode, MouseButton>, Tuple<Long, Long>> lastTimeCalled = interactive.getLastTimeCalled();

        for (Map.Entry<MouseButton, Tuple<Runnable, Runnable>> entry : interactive.getMouseInputMapping().entrySet()) {
            MouseButton mouseButton = entry.getKey();
            Runnable action = entry.getValue().first();
            Runnable counterAction = entry.getValue().second();

            if (mouseInputHandler.isButtonPressed(mouseButton)) {
                if (System.currentTimeMillis() > lastTimeCalled.get(new Tuple<>(null, mouseButton)).first() + lastTimeCalled.get(new Tuple<>(null, mouseButton)).second()) {
                    action.run();
                    lastTimeCalled.put(new Tuple<>(null, mouseButton), new Tuple<>(System.currentTimeMillis(), lastTimeCalled.get(new Tuple<>(null, mouseButton)).second()));
                }
            }
            if (mouseInputHandler.isButtonReleased(mouseButton) && counterAction != null) {
                counterAction.run();
            }
        }
    }
}
