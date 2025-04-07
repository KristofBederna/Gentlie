package Game.Components;

import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.ComplexShape;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;

import java.util.List;

public class AttackBoxComponent extends Component {
    private ComplexShape attackBox;
    private long startTime;
    private long duration;

    public AttackBoxComponent(List<Point> points, long duration) {
        this.attackBox = new ComplexShape(points);
        this.duration = duration;
        startTime = System.currentTimeMillis();
    }

    public AttackBoxComponent(ComplexShape attackBox, long duration) {
        this.attackBox = new ComplexShape(attackBox);
        this.duration = duration;
        startTime = System.currentTimeMillis();
    }

    public ComplexShape getAttackBox() {
        return attackBox;
    }

    public void setAttackBox(ComplexShape hitBox) {
        this.attackBox = hitBox;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getStartTime() {
        return startTime;
    }

    @Override
    public String getStatus() {
        return "";
    }
}
