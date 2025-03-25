package inf.elte.hu.gameengine_javafx.Components.UIComponents;

import javafx.scene.control.Button;

public class ButtonComponent extends UIComponent<Button> {

    public ButtonComponent(String text, double x, double y, double width, double height, Runnable onClick) {
        super(x, y, width, height);
        this.uiElement = new Button(text);
        uiElement.setLayoutX(x);
        uiElement.setLayoutY(y);
        uiElement.setPrefSize(width, height);

        uiElement.setOnAction(event -> onClick.run());
    }

    @Override
    public String getStatus() {
        return "";
    }
}
