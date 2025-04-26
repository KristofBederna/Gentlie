package Game.Entities;

import Game.Components.ImageViewComponent;
import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Entities.UIEntities.UIEntity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;

public class ImageUIEntity extends UIEntity<ImageViewComponent> {
    public ImageUIEntity(String path, double x, double y, double width, double height) {
        getComponent(PositionComponent.class).setLocal(new Point(x, y), this);
        this.uiComponent = new ImageViewComponent(x, y, width, height, path);
        addComponent(uiComponent);

        addToManager();
        this.addToUI();
    }
}
