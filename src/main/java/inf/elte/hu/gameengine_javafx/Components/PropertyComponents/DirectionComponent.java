package inf.elte.hu.gameengine_javafx.Components.PropertyComponents;

import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;
import inf.elte.hu.gameengine_javafx.Misc.Direction;

public class DirectionComponent extends Component {
    private Direction direction;

    public DirectionComponent(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }
}
