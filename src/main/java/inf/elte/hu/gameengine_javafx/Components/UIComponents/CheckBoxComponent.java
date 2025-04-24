package inf.elte.hu.gameengine_javafx.Components.UIComponents;

import javafx.scene.control.CheckBox;

public class CheckBoxComponent extends UIComponent<CheckBox> {

    public CheckBoxComponent(String text, double x, double y, double width, double height) {
        super(x, y, width, height);
        this.uiElement = new CheckBox(text);
        uiElement.setLayoutX(this.x);
        uiElement.setLayoutY(this.y);
    }

    public boolean isSelected() {
        return uiElement.isSelected();
    }

    public void setSelected(boolean selected) {
        uiElement.setSelected(selected);
    }

    @Override
    public String getStatus() {
        return "";
    }
}
