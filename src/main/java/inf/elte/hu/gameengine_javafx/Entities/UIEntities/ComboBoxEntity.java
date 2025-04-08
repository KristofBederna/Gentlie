package inf.elte.hu.gameengine_javafx.Entities.UIEntities;

import inf.elte.hu.gameengine_javafx.Components.UIComponents.ComboBoxComponent;

public class ComboBoxEntity<T> extends UIEntity<ComboBoxComponent<T>> {
    public ComboBoxEntity(ComboBoxComponent<T> component) {
        this.uiComponent = component;
        addComponent(uiComponent);

        addToManager();
        this.addToUI();
    }
}
