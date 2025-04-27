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
    public static ArrayList<Point> selectPath(Entity entity) {
        Point start = entity.getComponent(PathfindingComponent.class).getStart();
        Point end = entity.getComponent(PathfindingComponent.class).getEnd();

        if (start.distanceTo(end) < 5) {
            return new ArrayList<>(List.of(start));
        }

        Map<Point, Point> cameFrom = new HashMap<>();
        Map<Point, Double> gScore = new HashMap<>();
        gScore.put(start, 0.0);

        Map<Point, Double> fScore = new HashMap<>();
        fScore.put(start, start.distanceTo(end));

        PriorityQueue<Point> openSet = new PriorityQueue<>(
                Comparator.comparingDouble(p -> fScore.getOrDefault(p, Double.MAX_VALUE))
        );
        Set<Point> openSetContents = new HashSet<>();

        openSet.add(start);
        openSetContents.add(start);

        Set<Point> closedSet = new HashSet<>();

        while (!openSet.isEmpty()) {
            Point current = openSet.poll();
            openSetContents.remove(current);

            if (current.compareCoordinates(end)) {
                return reconstructPath(cameFrom, current, start);
            }

            closedSet.add(current);

            List<Point> neighbors = entity.getComponent(PathfindingComponent.class).getNeighbours(current);
            for (Point neighbor : neighbors) {
                if (closedSet.contains(neighbor)) continue;

                double tentativeG = gScore.getOrDefault(current, Double.MAX_VALUE) + current.distanceTo(neighbor);

                if (tentativeG < gScore.getOrDefault(neighbor, Double.MAX_VALUE)) {
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentativeG);
                    fScore.put(neighbor, tentativeG + neighbor.distanceTo(end));

                    if (!openSetContents.contains(neighbor)) {
                        openSet.add(neighbor);
                        openSetContents.add(neighbor);
                    }
                }
            }
        }

        return new ArrayList<>();
    }

    /**
     * Reconstructs the path from the end to the start based on the `cameFrom` map,
     * and excludes the starting point from the returned path.
     *
     * @param cameFrom Map tracking the best previous point for each point
     * @param current The endpoint (destination)
     * @param start The starting point that should be excluded from the path
     * @return A list of points representing the reconstructed path (excluding start)
     */
    private static ArrayList<Point> reconstructPath(Map<Point, Point> cameFrom, Point current, Point start) {
        ArrayList<Point> path = new ArrayList<>();
        while (cameFrom.containsKey(current)) {
            if (!current.compareCoordinates(start)) {
                path.add(0, current);
            }
            current = cameFrom.get(current);
        }
        return path;
    }
}
