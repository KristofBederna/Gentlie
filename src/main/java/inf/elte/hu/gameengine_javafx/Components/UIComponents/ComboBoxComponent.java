package inf.elte.hu.gameengine_javafx.Components.UIComponents;

import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;

public class ComboBoxComponent<T> extends UIComponent<ComboBox<T>> {

    public ComboBoxComponent(double x, double y, double width, double height, ObservableList<T> items) {
        super(x, y, width, height);
        this.uiElement = new ComboBox<>(items);
        uiElement.setLayoutX(x);
        uiElement.setLayoutY(y);
    }

    @Override
    public String getStatus() {
        return "";
    }
}
