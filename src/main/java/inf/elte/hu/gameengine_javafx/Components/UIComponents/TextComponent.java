package inf.elte.hu.gameengine_javafx.Components.UIComponents;

import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class TextComponent extends UIComponent<StackPane> {

    public TextComponent(double x, double y, double width, double height, String textContent) {
        super(x, y, width, height);

        Text text = new Text(textContent);
        StackPane textWrapper = new StackPane(text);
        textWrapper.setMinWidth(width);
        textWrapper.setMinHeight(height);
        textWrapper.setLayoutX(x);
        textWrapper.setLayoutY(y);

        this.uiElement = textWrapper;
    }

    @Override
    public String getStatus() {
        return "";
    }
}
