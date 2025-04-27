package inf.elte.hu.gameengine_javafx.Components.Default;

import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;

public class PositionComponent extends Component {
    private Point local;
    private Point global;

    public PositionComponent(double localX, double localY, Entity entity) {
        this.local = new Point(localX, localY);
        this.global = new Point(localX, localY);
        updateGlobalPosition(entity);
    }

    public PositionComponent(Entity entity) {
        this.local = new Point(0.0, 0.0);
        this.global = new Point(0.0, 0.0);
        updateGlobalPosition(entity);
    }

    public double getLocalX() {
        return this.local.getX();
    }

    public double getLocalY() {
        return this.local.getY();
    }

    public Point getLocal() {
        return local;
    }

    public void setLocal(Point local, Entity entity) {
        this.local = local;
        updateGlobalPosition(entity);
    }

    public double getGlobalX() {
        return this.global.getX();
    }

    public double getGlobalY() {
        return this.global.getY();
    }

    public Point getGlobal() {
        return global;
    }

    public void setGlobal(Point global) {
        this.global = global;
    }

    public void setLocalX(double localX, Entity entity) {
        this.local.setX(localX);
        updateGlobalPosition(entity);
    }

    public void setLocalY(double localY, Entity entity) {
        this.local.setY(localY);
        updateGlobalPosition(entity);
    }

    public void setLocalPosition(double localX, double localY, Entity entity) {
        this.local.setX(localX);
        this.local.setY(localY);
        updateGlobalPosition(entity);
    }

    public void updateGlobalPosition(Entity entity) {
        if (entity != null) {
            ParentComponent parentComponent = entity.getComponent(ParentComponent.class);
            if (parentComponent != null && parentComponent.getParent() != null) {
                PositionComponent parentPosition = parentComponent.getParent().getComponent(PositionComponent.class);
                if (parentPosition != null) {
                    this.setGlobal(new Point(parentPosition.getGlobal().getX() + this.local.getX(), parentPosition.getGlobal().getY() + this.local.getY()));
                    return;
                }
            }
        }
        this.global.setCoordinates(this.local.getCoordinates());
    }
}
