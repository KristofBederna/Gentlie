package inf.elte.hu.gameengine_javafx.Components.PhysicsComponents;

import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;

public class DragComponent extends Component {
    private double drag;

    public DragComponent() {
    }

    public DragComponent(double drag) {
        this.drag = drag;
    }

    public double getDrag() {
        return drag;
    }

    public void setDrag(double drag) {
        this.drag = drag;
    }
}
