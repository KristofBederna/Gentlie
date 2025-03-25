package inf.elte.hu.gameengine_javafx.Components.PhysicsComponents;

import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;

public class FrictionComponent extends Component {
    private double friction;

    public FrictionComponent() {
    }

    public FrictionComponent(double friction) {
        this.friction = friction;
    }

    public double getFriction() {
        return friction;
    }

    public void setFriction(double friction) {
        this.friction = friction;
    }

    @Override
    public String getStatus() {
        return "";
    }
}
