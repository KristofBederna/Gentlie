package inf.elte.hu.gameengine_javafx.Components.UIComponents;

import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;
import inf.elte.hu.gameengine_javafx.Entities.UIEntities.UIEntity;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;

import java.util.ArrayList;

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
