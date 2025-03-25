package inf.elte.hu.gameengine_javafx.Components;

import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.CentralMassComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.MapMeshComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.WorldDimensionComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Entities.WorldEntity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Misc.Config;

import java.util.ArrayList;
import java.util.List;

public class PathfindingComponent extends Component {
    private Point start;
    private Point end;
    private Point current;
    private List<Point> neighbours;
    private List<Point> path;

    public PathfindingComponent(Point start, Point end) {
        this.start = start;
        this.end = end;
    }

    public Point getStart() {
        return start;
    }

    public Point getEnd() {
        return end;
    }

    public List<Point> getPath() {
        return path;
    }

    public List<Point> getNeighbours() {
        return neighbours;
    }

    public void setNeighbours(List<Point> neighbours) {
        this.neighbours = neighbours;
    }

    public Point getCurrent() {
        return current;
    }

    public void setCurrent(Point current) {
        this.current = current;
    }

    public void setPath(List<Point> path) {
        this.path = path;
    }

    public void setDone() {
        this.path = null;
        this.start = null;
        this.end = null;
    }

    @Override
    public String getStatus() {
        return "";
    }

    public void setEnd(Point point) {
        end = point;
    }

    public void resetPathing(Entity entity) {
        path = null;
        neighbours = null;
        start = new Point(entity.getComponent(CentralMassComponent.class).getCentralX(), entity.getComponent(CentralMassComponent.class).getCentralY());
    }

    public List<Point> getNeighbours(Point current) {
        List<Point> neighbours = new ArrayList<>();
        MapMeshComponent mapMeshComponent = WorldEntity.getInstance().getComponent(MapMeshComponent.class);

        int currentX = (int) Math.floor(current.getX() / Config.tileSize);
        int currentY = (int) Math.floor(current.getY() / Config.tileSize);

        int worldWidth = (int) WorldEntity.getInstance().getComponent(WorldDimensionComponent.class).getWorldWidth();
        int worldHeight = (int) WorldEntity.getInstance().getComponent(WorldDimensionComponent.class).getWorldHeight();


        // Define directions
        int[][] directions = {
                {-1, 0}, {1, 0}, {0, -1}, {0, 1}, // Cardinal (Up, Down, Left, Right)
                //{-1, -1}, {-1, 1}, {1, -1}, {1, 1} // Diagonal
        };

        for (int[] dir : directions) {
            int neighbourX = currentX + dir[0];
            int neighbourY = currentY + dir[1];


            // Out of bounds check
            if (neighbourX < 0 || neighbourX >= worldWidth || neighbourY < 0 || neighbourY >= worldHeight) {
                continue;
            }

            // Get the neighbour point
            Point neighbour = mapMeshComponent.getMapCoordinate(neighbourX, neighbourY);

            if (neighbour == null) {
                continue; // Skip invalid points
            }

            boolean isDiagonal = (dir[0] != 0 && dir[1] != 0);
            if (isDiagonal) {
                // Check if at least one adjacent tile is passable
                boolean canMoveDiagonally =
                        (mapMeshComponent.getMapCoordinate(currentX + dir[0], currentY) != null) ||
                                (mapMeshComponent.getMapCoordinate(currentX, currentY + dir[1]) != null);

                if (!canMoveDiagonally) {
                    continue; // Skip this diagonal move
                }
            }

            // Add valid neighbour
            neighbours.add(neighbour);
        }

        return neighbours;
    }

}
