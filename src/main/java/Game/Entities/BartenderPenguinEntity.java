package Game.Entities;

import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.CentralMassComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.DimensionComponent;
import inf.elte.hu.gameengine_javafx.Components.RenderingComponents.ImageComponent;
import inf.elte.hu.gameengine_javafx.Components.RenderingComponents.ZIndexComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;

public class BartenderPenguinEntity extends Entity {
    public BartenderPenguinEntity(double x, double y, String path, double width, double height) {
        this.getComponent(PositionComponent.class).setLocalPosition(x, y, this);
        this.addComponent(new ImageComponent(path, width, height));
        this.addComponent(new DimensionComponent(width, height));
        this.addComponent(new ZIndexComponent(3));
        this.addComponent(new CentralMassComponent(x + width / 2, y + height / 2));

        addToManager();
    }
}
