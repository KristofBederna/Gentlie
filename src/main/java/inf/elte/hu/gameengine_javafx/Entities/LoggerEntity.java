package inf.elte.hu.gameengine_javafx.Entities;

import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import javafx.scene.Node;
import javafx.scene.control.TextArea;

public class LoggerEntity extends Entity {
    private final TextArea debugInfoTextArea;

    public LoggerEntity() {
        debugInfoTextArea = new TextArea();
        debugInfoTextArea.setEditable(false);
        debugInfoTextArea.setFocusTraversable(false);
        debugInfoTextArea.setMouseTransparent(true);
        debugInfoTextArea.setPrefSize(300, 500);
        debugInfoTextArea.setWrapText(true);
    }

    public Node getTextArea() {
        return debugInfoTextArea;
    }
}
