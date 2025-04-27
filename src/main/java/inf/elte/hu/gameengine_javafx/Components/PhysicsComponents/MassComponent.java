package inf.elte.hu.gameengine_javafx.Components.PhysicsComponents;

import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;

public class MassComponent extends Component {
    private double mass;

    public MassComponent() {
    }

    public MassComponent(double mass) {
        this.mass = mass;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }
}
