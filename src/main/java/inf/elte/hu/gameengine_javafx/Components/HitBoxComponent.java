package inf.elte.hu.gameengine_javafx.Components;

import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.ComplexShape;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;

import java.util.List;

public class HitBoxComponent extends Component {
    private ComplexShape hitBox;

    public HitBoxComponent(List<Point> points) {
        this.hitBox = new ComplexShape(points);
    }

    public HitBoxComponent(ComplexShape hitBox) {
        this.hitBox = new ComplexShape(hitBox);
    }

    public ComplexShape getHitBox() {
        return hitBox;
    }

    public void setHitBox(ComplexShape hitBox) {
        this.hitBox = hitBox;
    }
}