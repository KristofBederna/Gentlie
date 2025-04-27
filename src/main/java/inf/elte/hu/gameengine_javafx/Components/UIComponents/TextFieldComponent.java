package inf.elte.hu.gameengine_javafx.Components.UIComponents;

import javafx.scene.control.TextField;

public class TextFieldComponent extends UIComponent<TextField> {

    public TextFieldComponent(String text, double x, double y, double width, double height) {
        super(x, y, width, height);
        this.uiElement = new TextField();
        this.uiElement.setText(text);
        uiElement.setLayoutX(x);
        uiElement.setLayoutY(y);
        uiElement.setPrefSize(width, height);
    }

    public String getText() {
        return uiElement.getText();
    }

    public void setText(String text) {
        uiElement.setText(text);
    }
}
