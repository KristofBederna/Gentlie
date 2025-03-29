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
import inf.elte.hu.gameengine_javafx.Misc.Pathfinding;
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
                pathfindingComponent.setPath(Pathfinding.selectPath(entity));
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
}
