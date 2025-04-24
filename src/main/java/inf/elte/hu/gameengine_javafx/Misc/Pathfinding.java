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

        // If the start and end points are very close, return the start point as the path
        if (start.distanceTo(end) < 5) {
            return new ArrayList<>(List.of(start));
        }

        // Initialize pathfinding structures
        Map<Point, Point> cameFrom = new HashMap<>();
        PriorityQueue<Point> openSet = initializeOpenSet(start, end);
        Set<Point> closedSet = new HashSet<>();
        Map<Point, Double> gScore = new HashMap<>();

        gScore.put(start, 0.0);

        // Execute the A* search algorithm
        return executePathfinding(entity, end, cameFrom, openSet, closedSet, gScore);
    }

    /**
     * Initializes the open set (priority queue) for A* search.
     *
     * @param start The starting point for pathfinding.
     * @param end   The end point for pathfinding.
     * @return The initialized open set.
     */
    private static PriorityQueue<Point> initializeOpenSet(Point start, Point end) {
        PriorityQueue<Point> openSet = new PriorityQueue<>(Comparator.comparingDouble(p -> p.distanceTo(end)));
        openSet.add(start);
        return openSet;
    }

    /**
     * Executes the A* algorithm to find the path.
     *
     * @param entity    The entity for which the pathfinding is being calculated.
     * @param end       The target point to reach.
     * @param cameFrom  The map to track the best path.
     * @param openSet   The priority queue to explore points.
     * @param closedSet The set of points that have already been processed.
     * @param gScore    The map tracking the shortest path cost to each point.
     * @return The list of points representing the path.
     */
    private static ArrayList<Point> executePathfinding(Entity entity, Point end, Map<Point, Point> cameFrom, PriorityQueue<Point> openSet, Set<Point> closedSet, Map<Point, Double> gScore) {
        while (!openSet.isEmpty()) {
            Point current = openSet.poll();

            // If the destination is reached, reconstruct and return the path
            if (current.compareCoordinates(end)) {
                return reconstructPath(cameFrom, current);
            }

            closedSet.add(current);

            // Get the neighbors of the current point
            List<Point> neighbors = entity.getComponent(PathfindingComponent.class).getNeighbours(current);

            // Process each neighbor
            for (Point neighbor : neighbors) {
                if (closedSet.contains(neighbor)) continue;

                double tentativeGScore = gScore.getOrDefault(current, Double.MAX_VALUE) + current.distanceTo(neighbor);

                if (!gScore.containsKey(neighbor) || tentativeGScore < gScore.get(neighbor)) {
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentativeGScore);

                    // Add neighbor to open set if not already there
                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }
        return new ArrayList<>();
    }

    /**
     * Reconstructs the path from the start to the end based on the `cameFrom` map.
     *
     * @param cameFrom The map that tracks the best previous point for each point.
     * @param current The current point from which the path is being reconstructed.
     * @return A list of points representing the reconstructed path from start to end.
     */
    private static ArrayList<Point> reconstructPath(Map<Point, Point> cameFrom, Point current) {
        ArrayList<Point> path = new ArrayList<>();
        while (cameFrom.containsKey(current)) {
            path.addFirst(current);
            current = cameFrom.get(current);
        }
        return path;
    }
}
