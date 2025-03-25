package inf.elte.hu.gameengine_javafx.Entities.UIEntities;

import inf.elte.hu.gameengine_javafx.Components.UIComponents.TextFieldComponent;

public class TextFieldEntity extends UIEntity<TextFieldComponent> {
    public TextFieldEntity(String text, double x, double y, double width, double height) {
        this.uiComponent = new TextFieldComponent(text, x, y, width, height);
        addComponent(uiComponent);

        addToManager();
        this.addToUI();
    }
}
