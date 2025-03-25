package inf.elte.hu.gameengine_javafx.Components.UIComponents;

import javafx.scene.control.Slider;

public class SliderComponent extends UIComponent<Slider> {

    public SliderComponent(double x, double y, double width, double height, double min, double max, double initialValue) {
        super(x, y, width, height);
        this.uiElement = new Slider(min, max, initialValue);
        uiElement.setLayoutX(x);
        uiElement.setLayoutY(y);
        uiElement.setPrefSize(width, height);
    }

    public double getValue() {
        return uiElement.getValue();
    }

    public void setValue(double value) {
        uiElement.setValue(value);
    }

    @Override
    public String getStatus() {
        return "";
    }
}
