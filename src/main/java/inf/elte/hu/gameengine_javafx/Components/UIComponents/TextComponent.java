package inf.elte.hu.gameengine_javafx.Components.UIComponents;

import inf.elte.hu.gameengine_javafx.Misc.Layers.uiRoot;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class TextComponent extends UIComponent<StackPane> {

    public TextComponent(double x, double y, double width, double height, String textContent) {
        super(x - uiRoot.getInstance().getLayoutX(), y - uiRoot.getInstance().getLayoutY(), width, height);

        Text text = new Text(textContent);
        StackPane textWrapper = new StackPane(text);
        textWrapper.setMinWidth(this.width);
        textWrapper.setMinHeight(this.height);
        textWrapper.setLayoutX(this.x);
        textWrapper.setLayoutY(this.y);

        this.uiElement = textWrapper;
    }

    @Override
    public String getStatus() {
        return "";
    }
}
