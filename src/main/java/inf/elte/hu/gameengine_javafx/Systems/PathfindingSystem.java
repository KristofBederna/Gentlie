package inf.elte.hu.gameengine_javafx.Systems;

import inf.elte.hu.gameengine_javafx.Components.PathfindingComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.AccelerationComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.CentralMassComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.VelocityComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.StateComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Maths.Vector;
import inf.elte.hu.gameengine_javafx.Misc.Time;

import java.util.*;

/**
 * The PathfindingSystem is responsible for calculating and managing the movement path of entities
 * using pathfinding algorithms. It updates the movement of entities with a `PathfindingComponent`
 * based on their current position and target destination.
 */
public class PathfindingSystem extends GameSystem {

    /**
     * Starts the pathfinding system, setting it as active.
     */
    @Override
    public void start() {
        this.active = true;
    }

    /**
     * Updates the pathfinding system. For each entity with a `PathfindingComponent`, it calculates
     * the entity's movement path and updates the entity's position towards its target.
     */
    @Override
    protected void update() {
        var pathfinderEntities = EntityHub.getInstance().getEntitiesWithComponent(PathfindingComponent.class);

        for (var entity : pathfinderEntities) {
            PathfindingComponent pathfindingComponent = entity.getComponent(PathfindingComponent.class);
            Point start = pathfindingComponent.getStart();
            Point end = pathfindingComponent.getEnd();

            if (start == null || end == null) {
                continue;
            }

            if (pathfindingComponent.getPath() == null) {
                pathfindingComponent.setPath(selectPath(start, end, entity));
            } else {
                if (!pathfindingComponent.getPath().isEmpty()) {
                    Point node = pathfindingComponent.getPath().getFirst();
                    Point position = new Point(entity.getComponent(CentralMassComponent.class).getCentralX(),
                            entity.getComponent(CentralMassComponent.class).getCentralY());

                    double deltaX = node.getX() - position.getX();
                    double deltaY = node.getY() - position.getY();

                    StateComponent state = entity.getComponent(StateComponent.class);
                    String previousState = state.getCurrentState();

                    if (Math.abs(deltaX) >= Math.abs(deltaY)) {
                        if (deltaX > 0) { // Moving right
                            counterUp(entity);
                            counterDown(entity);
                            state.setCurrentState("right");
                            moveRight(entity);
                            if ("left".equals(previousState)) counterLeft(entity);
                        } else { // Moving left
                            state.setCurrentState("left");
                            moveLeft(entity);
                            if ("right".equals(previousState)) counterRight(entity);
                        }
                    } else {
                        counterLeft(entity);
                        counterRight(entity);
                        if (deltaY > 0) { // Moving down
                            state.setCurrentState("down");
                            moveDown(entity);
                            if ("up".equals(previousState)) counterUp(entity);
                        } else { // Moving up
                            state.setCurrentState("up");
                            moveUp(entity);
                            if ("down".equals(previousState)) counterDown(entity);
                        }
                    }

                    if (position.compareCoordinates(node)) {
                        pathfindingComponent.getPath().removeFirst();
                        switch (state.getCurrentState()) {
                            case "right":
                                counterRight(entity);
                                break;
                            case "left":
                                counterLeft(entity);
                                break;
                            case "down":
                                counterDown(entity);
                                break;
                            case "up":
                                counterUp(entity);
                                break;
                        }
                        if (pathfindingComponent.getPath().isEmpty()) {
                            entity.getComponent(StateComponent.class).setCurrentState("idle");
                        }
                    }
                }

                if (pathfindingComponent.getPath().isEmpty()) {
                    entity.getComponent(VelocityComponent.class).stopMovement();
                }
            }
        }
    }

    private void moveUp(Entity e) {
        double dy = -4 * Time.getInstance().getDeltaTime();
        e.getComponent(AccelerationComponent.class).getAcceleration().setDy(dy);
    }

    private void moveDown(Entity e) {
        double dy = 4 * Time.getInstance().getDeltaTime();
        e.getComponent(AccelerationComponent.class).getAcceleration().setDy(dy);
    }

    private void moveLeft(Entity e) {
        double dx = -4 * Time.getInstance().getDeltaTime();
        e.getComponent(AccelerationComponent.class).getAcceleration().setDx(dx);
    }

    private void moveRight(Entity e) {
        double dx = 4 * Time.getInstance().getDeltaTime();
        e.getComponent(AccelerationComponent.class).getAcceleration().setDx(dx);
    }

    private void counterUp(Entity e) {
        e.getComponent(AccelerationComponent.class).getAcceleration().setDy(0);
    }

    private void counterDown(Entity e) {
        e.getComponent(AccelerationComponent.class).getAcceleration().setDy(0);
    }

    private void counterRight(Entity e) {
        e.getComponent(AccelerationComponent.class).getAcceleration().setDx(0);
    }

    private void counterLeft(Entity e) {
        e.getComponent(AccelerationComponent.class).getAcceleration().setDx(0);
    }

    /**
     * Selects a path from the start point to the end point using the A* algorithm.
     * The algorithm calculates the shortest path considering the neighbors of each point.
     *
     * @param start The start point of the pathfinding.
     * @param end The end point of the pathfinding.
     * @param entity The entity for which the pathfinding is being calculated.
     * @return A list of points representing the path from start to end.
     */
    private List<Point> selectPath(Point start, Point end, Entity entity) {
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
    private List<Point> reconstructPath(Map<Point, Point> cameFrom, Point current) {
        List<Point> path = new LinkedList<>();
        while (cameFrom.containsKey(current)) {
            path.add(0, current);
            current = cameFrom.get(current);
        }
        return path;
    }
}
