package inf.elte.hu.gameengine_javafx.Components.PhysicsComponents;

import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;
import inf.elte.hu.gameengine_javafx.Maths.Vector;

public class VelocityComponent extends Component {
    Vector velocity;
    double maxVelocity;

    public VelocityComponent() {
        velocity = new Vector(0, 0);
    }

    public VelocityComponent(double maxVelocity) {
        this.velocity = new Vector(0, 0);
        this.maxVelocity = maxVelocity;
    }

    public VelocityComponent(Vector velocity, double maxVelocity) {
        this.velocity = velocity;
        this.maxVelocity = maxVelocity;
    }

    public Vector getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector velocity) {
        this.velocity = velocity;
    }

    public void setVelocity(double x, double y) {
        double clampedX = Math.max(-maxVelocity, Math.min(x, maxVelocity));
        double clampedY = Math.max(-maxVelocity, Math.min(y, maxVelocity));
        this.velocity = new Vector(clampedX, clampedY);
    }


    public double getMaxVelocity() {
        return maxVelocity;
    }

    public void setMaxVelocity(double maxVelocity) {
        this.maxVelocity = maxVelocity;
    }

    public void stopMovement() {
        velocity = new Vector(0, 0);
    }
}
