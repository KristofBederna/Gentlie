package inf.elte.hu.gameengine_javafx.Entities.UIEntities;

import inf.elte.hu.gameengine_javafx.Components.UIComponents.LabelComponent;
import inf.elte.hu.gameengine_javafx.Misc.Layers.uiRoot;

public class LabelEntity extends UIEntity<LabelComponent> {
    public LabelEntity(String text, double x, double y, double width, double height) {
        this.uiComponent = new LabelComponent(text, x - uiRoot.getInstance().getLayoutX(), y - uiRoot.getInstance().getLayoutY(), width, height);
        addComponent(uiComponent);

        addToManager();
        this.addToUI();
    }
}
