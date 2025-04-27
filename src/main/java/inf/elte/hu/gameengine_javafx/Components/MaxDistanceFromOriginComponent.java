package inf.elte.hu.gameengine_javafx.Components;

import inf.elte.hu.gameengine_javafx.Components.Default.ParentComponent;
import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;

public class MaxDistanceFromOriginComponent extends Component {
    private double maxDistance;

    public MaxDistanceFromOriginComponent(double maxDistance) {
        this.maxDistance = maxDistance;
    }

    public double getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(double maxDistance) {
        this.maxDistance = maxDistance;
    }

    public boolean isOverMaxDistance(Entity entity) {
        if (entity == null) {
            return false;
        }

        ParentComponent parent = entity.getComponent(ParentComponent.class);
        PositionComponent position = entity.getComponent(PositionComponent.class);
        if (position == null || parent == null || parent.getParent() == null) {
            throw new RuntimeException("Entity has no parent or position");
        }

        PositionComponent parentPosition = parent.getParent().getComponent(PositionComponent.class);
        if (parentPosition == null) {
            return false;
        }

        double x1 = position.getLocalX();
        double y1 = position.getLocalY();

        double distance = Math.sqrt(Math.pow(x1, 2) + Math.pow(y1, 2));

        return distance > maxDistance;
    }
}
