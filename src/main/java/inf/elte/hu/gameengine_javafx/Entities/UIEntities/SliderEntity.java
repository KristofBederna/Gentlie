package inf.elte.hu.gameengine_javafx.Entities.UIEntities;

import inf.elte.hu.gameengine_javafx.Components.UIComponents.SliderComponent;

public class SliderEntity extends UIEntity<SliderComponent> {
    public SliderEntity(double x, double y, double width, double height, double min, double max, double initialValue) {
        this.uiComponent = new SliderComponent(x, y, width, height, min, max, initialValue);
        addComponent(uiComponent);

        addToManager();
        this.addToUI();
    }
}
