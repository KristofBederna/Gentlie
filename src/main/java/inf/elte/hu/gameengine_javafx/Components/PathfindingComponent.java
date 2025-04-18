package inf.elte.hu.gameengine_javafx.Components;

import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.CentralMassComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.MapMeshComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.WorldDimensionComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Entities.WorldEntity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Misc.Configs.MapConfig;

import java.util.ArrayList;
import java.util.List;

public class PathfindingComponent extends Component {
    private Point start;
    private Point end;
    private Point current;
    private List<Point> neighbours;
    private ArrayList<Point> path;

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

    public ArrayList<Point> getPath() {
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

    public void setPath(ArrayList<Point> path) {
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
        start = WorldEntity.getInstance().getComponent(MapMeshComponent.class).getMapCoordinate(Math.floorDiv((int) entity.getComponent(CentralMassComponent.class).getCentralX(), (int) MapConfig.scaledTileSize), Math.floorDiv((int) entity.getComponent(CentralMassComponent.class).getCentralY(), (int) MapConfig.scaledTileSize));
    }

    public List<Point> getNeighbours(Point current) {
        List<Point> neighbours = new ArrayList<>();
        MapMeshComponent mapMesh = WorldEntity.getInstance().getComponent(MapMeshComponent.class);
        WorldDimensionComponent dimensions = WorldEntity.getInstance().getComponent(WorldDimensionComponent.class);

        int currentX = toTileCoordinate(current.getX());
        int currentY = toTileCoordinate(current.getY());

        int worldWidth = (int) dimensions.getWorldWidth();
        int worldHeight = (int) dimensions.getWorldHeight();

        int[][] directions = getAllDirections();

        for (int[] dir : directions) {
            int neighbourX = currentX + dir[0];
            int neighbourY = currentY + dir[1];

            if (!isInBounds(neighbourX, neighbourY, worldWidth, worldHeight)) continue;

            Point neighbour = mapMesh.getMapCoordinate(neighbourX, neighbourY);
            if (neighbour == null) continue;

            if (isDiagonal(dir) && !canMoveDiagonally(mapMesh, currentX, currentY, dir)) continue;

            neighbours.add(neighbour);
        }

        return neighbours;
    }

    private int toTileCoordinate(double value) {
        return (int) Math.floor(value / MapConfig.scaledTileSize);
    }

    private int[][] getAllDirections() {
        return new int[][]{
                {-1, 0}, {1, 0}, {0, -1}, {0, 1},
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
        };
    }

    private boolean isInBounds(int x, int y, int width, int height) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    private boolean isDiagonal(int[] dir) {
        return dir[0] != 0 && dir[1] != 0;
    }

    private boolean canMoveDiagonally(MapMeshComponent mapMesh, int x, int y, int[] dir) {
        return mapMesh.getMapCoordinate(x + dir[0], y) != null &&
                mapMesh.getMapCoordinate(x, y + dir[1]) != null;
    }


    public void setStart(Point start) {
        this.start = start;
    }
}
