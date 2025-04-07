package Game.Components;

import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.ComplexShape;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;

import java.util.List;

public class AttackBoxComponent extends Component {
    private ComplexShape attackBox;

    public AttackBoxComponent(List<Point> points) {
        this.attackBox = new ComplexShape(points);
    }

    public AttackBoxComponent(ComplexShape attackBox) {
        this.attackBox = new ComplexShape(attackBox);
    }

    public ComplexShape getAttackBox() {
        return attackBox;
    }

    public void setAttackBox(ComplexShape hitBox) {
        this.attackBox = hitBox;
    }

    @Override
    public String getStatus() {
        return "";
    }
}
