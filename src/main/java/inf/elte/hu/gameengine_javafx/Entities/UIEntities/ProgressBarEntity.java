package inf.elte.hu.gameengine_javafx.Entities.UIEntities;

import inf.elte.hu.gameengine_javafx.Components.UIComponents.ProgressBarComponent;

public class ProgressBarEntity extends UIEntity<ProgressBarComponent> {
    public ProgressBarEntity(double x, double y, double width, double height, double initialValue) {
        this.uiComponent = new ProgressBarComponent(x, y, width, height, initialValue);
        addComponent(uiComponent);

        addToManager();
        this.addToUI();
    }
}
