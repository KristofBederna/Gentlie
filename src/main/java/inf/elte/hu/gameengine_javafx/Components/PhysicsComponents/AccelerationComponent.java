package inf.elte.hu.gameengine_javafx.Components.PhysicsComponents;

import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;
import inf.elte.hu.gameengine_javafx.Maths.Vector;

public class AccelerationComponent extends Component {
    private Vector acceleration;

    public AccelerationComponent(Vector acceleration) {
        this.acceleration = acceleration;
    }

    public AccelerationComponent() {
        acceleration = new Vector(0, 0);
    }

    public Vector getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(Vector acceleration) {
        this.acceleration = acceleration;
    }

    @Override
    public String getStatus() {
        return "";
    }

    public void stopMovement() {
        this.acceleration = new Vector(0, 0);
    }
}
