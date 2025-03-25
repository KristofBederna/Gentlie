package inf.elte.hu.gameengine_javafx.Components.UIComponents;

import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;
import javafx.scene.Node;
import javafx.scene.layout.Region;

public abstract class UIComponent<T extends Region> extends Component {
    protected T uiElement;
    protected double x, y, width, height;

    public UIComponent(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
        uiElement.setLayoutX(x);
        uiElement.setLayoutY(y);
    }

    public T getUIElement() {
        return uiElement;
    }

    public void setSize(double width, double height) {
        this.width = width;
        this.height = height;
        uiElement.setPrefSize(width, height);
    }

    public Node getNode() {
        return uiElement;
    }
}
