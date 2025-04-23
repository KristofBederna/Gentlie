package inf.elte.hu.gameengine_javafx.Components;

import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;
import inf.elte.hu.gameengine_javafx.Misc.Tuple;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

import java.util.HashMap;
import java.util.Map;

public class InteractiveComponent extends Component {
    private final Map<KeyCode, Tuple<Runnable, Runnable>> keyInputMapping;
    private final Map<MouseButton, Tuple<Runnable, Runnable>> mouseInputMapping;
    private Runnable scrollAction;
    private Runnable mouseMoveAction;
    private final Map<Tuple<KeyCode, MouseButton>, Tuple<Long, Long>> lastTimeCalled;

    public InteractiveComponent() {
        this.keyInputMapping = new HashMap<>();
        this.mouseInputMapping = new HashMap<>();
        this.lastTimeCalled = new HashMap<>();
    }

    public void mapInput(KeyCode keyCode, long millis, Runnable action) {
        keyInputMapping.put(keyCode, new Tuple<>(action, null));
        lastTimeCalled.put(new Tuple<>(keyCode, null), new Tuple<>(System.currentTimeMillis(), millis));
    }

    public void mapInput(KeyCode keyCode, long millis, Runnable action, Runnable counterAction) {
        keyInputMapping.put(keyCode, new Tuple<>(action, counterAction));
        lastTimeCalled.put(new Tuple<>(keyCode, null), new Tuple<>(System.currentTimeMillis(), millis));
    }

    public void mapInput(MouseButton button, long millis, Runnable action) {
        mouseInputMapping.put(button, new Tuple<>(action, null));
        lastTimeCalled.put(new Tuple<>(null, button), new Tuple<>(System.currentTimeMillis(), millis));
    }

    public void mapInput(MouseButton button, long millis, Runnable action, Runnable counterAction) {
        mouseInputMapping.put(button, new Tuple<>(action, counterAction));
        lastTimeCalled.put(new Tuple<>(null, button), new Tuple<>(System.currentTimeMillis(), millis));
    }

    public void setMouseMoveAction(Runnable action) {
        this.mouseMoveAction = action;
    }

    public Map<KeyCode, Tuple<Runnable, Runnable>> getKeyInputMapping() {
        return keyInputMapping;
    }

    public Map<MouseButton, Tuple<Runnable, Runnable>> getMouseInputMapping() {
        return mouseInputMapping;
    }

    public Map<Tuple<KeyCode, MouseButton>, Tuple<Long, Long>> getLastTimeCalled() {
        return lastTimeCalled;
    }

    public Tuple<Long, Long> getLastTimeCalled(Tuple<KeyCode, MouseButton> button) {
        return lastTimeCalled.get(button);
    }

    public Runnable getMouseMoveAction() {
        return mouseMoveAction;
    }

    public void setScrollAction(Runnable action) {
        this.scrollAction = action;
    }

    public Runnable getScrollAction() {
        return scrollAction;
    }


    @Override
    public String getStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("---").append(this.getClass().getSimpleName()).append("---\n");

        for (Map.Entry<KeyCode, Tuple<Runnable, Runnable>> entry : keyInputMapping.entrySet()) {
            sb.append("Key: ").append(entry.getKey())
                    .append(", Action: ").append(entry.getValue().first())
                    .append(", CounterAction: ").append(entry.getValue().second())
                    .append("\n");
        }

        for (Map.Entry<MouseButton, Tuple<Runnable, Runnable>> entry : mouseInputMapping.entrySet()) {
            sb.append("Mouse Button: ").append(entry.getKey())
                    .append(", Action: ").append(entry.getValue().first())
                    .append(", CounterAction: ").append(entry.getValue().second())
                    .append("\n");
        }

        if (mouseMoveAction != null) {
            sb.append("Mouse Move Action: ").append(mouseMoveAction).append("\n");
        }

        return sb.toString();
    }

    public void clearMappings() {
        keyInputMapping.clear();
        mouseInputMapping.clear();
        scrollAction = null;
        mouseMoveAction = null;
    }
}
