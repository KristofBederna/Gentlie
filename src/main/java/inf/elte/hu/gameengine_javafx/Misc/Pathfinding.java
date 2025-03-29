package inf.elte.hu.gameengine_javafx.Misc;

import inf.elte.hu.gameengine_javafx.Components.PathfindingComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;

import java.util.*;

public class Pathfinding {
    /**
     * Selects a path from the start point to the end point using the A* algorithm.
     * The algorithm calculates the shortest path considering the neighbors of each point.
     *
     * @param entity The entity for which the pathfinding is being calculated.
     * @return A list of points representing the path from start to end.
     */
    public static List<Point> selectPath(Entity entity) {
        Point start = entity.getComponent(PathfindingComponent.class).getStart();
        Point end = entity.getComponent(PathfindingComponent.class).getEnd();
        List<Point> path = new ArrayList<>();
        Map<Point, Point> cameFrom = new HashMap<>();
        PriorityQueue<Point> openSet = new PriorityQueue<>(Comparator.comparingDouble(p -> p.distanceTo(end)));
        Set<Point> closedSet = new HashSet<>();
        Map<Point, Double> gScore = new HashMap<>();

        openSet.add(start);
        gScore.put(start, 0.0);

        while (!openSet.isEmpty()) {
            Point current = openSet.poll();
            if (current.compareCoordinates(end)) {
                return reconstructPath(cameFrom, current);
            }

            closedSet.add(current);
            List<Point> neighbours = entity.getComponent(PathfindingComponent.class).getNeighbours(current);

            for (Point neighbor : neighbours) {
                if (closedSet.contains(neighbor)) continue;

                double tentativeGScore = gScore.getOrDefault(current, Double.MAX_VALUE) + current.distanceTo(neighbor);

                if (!gScore.containsKey(neighbor) || tentativeGScore < gScore.get(neighbor)) {
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentativeGScore);
                    if (!openSet.contains(neighbor)) openSet.add(neighbor);
                }
            }
        }
        return path;
    }

    /**
     * Reconstructs the path from the start to the end based on the `cameFrom` map.
     *
     * @param cameFrom The map that tracks the best previous point for each point.
     * @param current The current point from which the path is being reconstructed.
     * @return A list of points representing the reconstructed path from start to end.
     */
    private static List<Point> reconstructPath(Map<Point, Point> cameFrom, Point current) {
        List<Point> path = new LinkedList<>();
        while (cameFrom.containsKey(current)) {
            path.add(0, current);
            current = cameFrom.get(current);
        }
        return path;
    }
}
