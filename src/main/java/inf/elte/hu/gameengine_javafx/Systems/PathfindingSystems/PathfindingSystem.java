package inf.elte.hu.gameengine_javafx.Systems.PathfindingSystems;

import inf.elte.hu.gameengine_javafx.Components.PathfindingComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.AccelerationComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.VelocityComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.CentralMassComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Misc.Pathfinding;
import inf.elte.hu.gameengine_javafx.Misc.Time;

/**
 * The PathfindingSystem is responsible for controlling the movement of entities
 * based on their pathfinding data. It processes entities that have a PathfindingComponent
 * and moves them towards their target location by following the computed path.
 */
public class PathfindingSystem extends GameSystem {

    /**
     * Starts the pathfinding system and sets it to active.
     */
    @Override
    public void start() {
        this.active = true;
    }

    /**
     * Updates the pathfinding for entities that have a PathfindingComponent.
     * It computes the path for entities, moves them towards their target, and adjusts
     * their movement based on the path data.
     */
    @Override
    public void update() {
        // Get all entities that have a PathfindingComponent
        var pathfinderEntities = EntityHub.getInstance().getEntitiesWithComponent(PathfindingComponent.class);

        // Iterate through all entities with PathfindingComponent
        for (var entity : pathfinderEntities) {
            // Skip null entities
            if (entity == null) {
                continue;
            }

            // Retrieve the PathfindingComponent of the current entity
            PathfindingComponent pathfindingComponent = entity.getComponent(PathfindingComponent.class);
            Point start = pathfindingComponent.getStart();
            Point end = pathfindingComponent.getEnd();

            // Skip if the start or end points are null
            if (start == null || end == null) continue;

            // If the path has not been calculated yet, compute the path
            if (pathfindingComponent.getPath() == null) {
                pathfindingComponent.setPath(Pathfinding.selectPath(entity));
            } else if (!pathfindingComponent.getPath().isEmpty()) {
                // If the path exists, get the first node (target) on the path
                Point node = pathfindingComponent.getPath().getFirst();

                // Retrieve the current position of the entity
                Point position = new Point(
                        entity.getComponent(CentralMassComponent.class).getCentralX(),
                        entity.getComponent(CentralMassComponent.class).getCentralY()
                );

                // Calculate the distance to the next node
                double deltaX = node.getX() - position.getX();
                double deltaY = node.getY() - position.getY();
                double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

                // If the distance is greater than a threshold, apply movement
                if (distance > 0.01) {
                    double normDx = deltaX / distance;
                    double normDy = deltaY / distance;

                    // Apply acceleration to move towards the target node
                    double speed = 4 * Time.getInstance().getDeltaTime();
                    AccelerationComponent accel = entity.getComponent(AccelerationComponent.class);
                    accel.getAcceleration().setDx(normDx * speed);
                    accel.getAcceleration().setDy(normDy * speed);
                }

                // If the entity reaches the target node, remove it from the path
                if (position.compareCoordinates(node)) {
                    pathfindingComponent.getPath().removeFirst();
                }
            }

            // If the path is completed, stop the movement and reset components
            if (pathfindingComponent.getPath() != null && pathfindingComponent.getPath().isEmpty()) {
                pathfindingComponent.resetPathing(entity);
                AccelerationComponent accel = entity.getComponent(AccelerationComponent.class);
                VelocityComponent velocity = entity.getComponent(VelocityComponent.class);

                accel.stopMovement();
                velocity.stopMovement();
            }
        }
    }
}
