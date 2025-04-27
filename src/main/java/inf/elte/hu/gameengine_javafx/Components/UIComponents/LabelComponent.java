package inf.elte.hu.gameengine_javafx.Components.UIComponents;

import javafx.scene.control.Label;

public class LabelComponent extends UIComponent<Label> {

    public LabelComponent(String text, double x, double y, double width, double height) {
        super(x, y, width, height);
        this.uiElement = new Label(text);
        uiElement.setLayoutX(x);
        uiElement.setLayoutY(y);
    }

    public void setText(String text) {
        uiElement.setText(text);
    }
}
