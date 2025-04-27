package inf.elte.hu.gameengine_javafx.Components.PropertyComponents;

import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;

public class CentralMassComponent extends Component {
    private Point central;

    public CentralMassComponent(double centralX, double centralY) {
        this.central = new Point(centralX, centralY);
    }

    public double getCentralX() {
        return central.getX();
    }

    public double getCentralY() {
        return central.getY();
    }

    public void setCentralX(double centralX) {
        this.central.setX(centralX);
    }

    public void setCentralY(double centralY) {
        this.central.setY(centralY);
    }

    public void setCentral(Point central) {
        this.central = central;
    }

    public Point getCentral() {
        return central;
    }
}
