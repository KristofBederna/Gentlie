package Game.Entities;

import inf.elte.hu.gameengine_javafx.Components.PathfindingComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;

public class PathfindingEntity extends Entity {
    public PathfindingEntity(Point start, Point end) {
        this.addComponent(new PathfindingComponent(start, end));
    }
}
