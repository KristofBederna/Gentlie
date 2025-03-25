package inf.elte.hu.gameengine_javafx.Entities.UIEntities;

import inf.elte.hu.gameengine_javafx.Components.UIComponents.ButtonComponent;

public class ButtonEntity extends UIEntity<ButtonComponent> {
    public ButtonEntity(String text, double x, double y, double width, double height, Runnable onClick) {
        this.uiComponent = new ButtonComponent(text, x, y, width, height, onClick);
        addComponent(uiComponent);

        addToManager();
        this.addToUI();
    }
}
