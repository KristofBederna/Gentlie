package inf.elte.hu.gameengine_javafx.Components.UIComponents;

import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;
import inf.elte.hu.gameengine_javafx.Misc.Configs.DisplayConfig;
import javafx.scene.Node;
import javafx.scene.layout.Region;

public abstract class UIComponent<T extends Region> extends Component {
    protected T uiElement;
    protected double x, y, width, height;

    public UIComponent(double x, double y, double width, double height) {
        this.x = x * DisplayConfig.relativeWidthRatio;
        this.y = y * DisplayConfig.relativeHeightRatio;
        this.width = width * DisplayConfig.relativeWidthRatio;
        this.height = height * DisplayConfig.relativeHeightRatio;
    }

    public void setPosition(double x, double y) {
        this.x = x * DisplayConfig.relativeWidthRatio;
        this.y = y * DisplayConfig.relativeHeightRatio;
        uiElement.setLayoutX(x * DisplayConfig.relativeWidthRatio);
        uiElement.setLayoutY(y * DisplayConfig.relativeHeightRatio);
    }

    public T getUIElement() {
        return uiElement;
    }

    public void setSize(double width, double height) {
        this.width = width * DisplayConfig.relativeWidthRatio;
        this.height = height * DisplayConfig.relativeHeightRatio;
        uiElement.setPrefSize(width * DisplayConfig.relativeWidthRatio, height * DisplayConfig.relativeHeightRatio);
    }

    public Node getNode() {
        return uiElement;
    }
}
