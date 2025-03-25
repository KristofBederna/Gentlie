package inf.elte.hu.gameengine_javafx.Entities.UIEntities;

import inf.elte.hu.gameengine_javafx.Components.UIComponents.CheckBoxComponent;

public class CheckBoxEntity extends UIEntity<CheckBoxComponent> {
    public CheckBoxEntity(String text, double x, double y, double width, double height) {
        this.uiComponent = new CheckBoxComponent(text, x, y, width, height);
        addComponent(uiComponent);

        addToManager();
        this.addToUI();
    }
}
