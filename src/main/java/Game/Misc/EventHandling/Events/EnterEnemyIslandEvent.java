package Game.Misc.EventHandling.Events;

import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.Event;

public class EnterEnemyIslandEvent implements Event {
    Point spawn;
    public EnterEnemyIslandEvent(Point spawn) {
        this.spawn = spawn;
    }

    public Point getSpawn() {
        return spawn;
    }
}
