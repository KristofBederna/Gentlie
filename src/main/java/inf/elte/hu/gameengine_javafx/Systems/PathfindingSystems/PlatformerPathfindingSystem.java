package inf.elte.hu.gameengine_javafx.Systems.PathfindingSystems;

import inf.elte.hu.gameengine_javafx.Components.PathfindingComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.VelocityComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.CentralMassComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Misc.Pathfinding;

/**
 * The PlatformerPathfindingSystem is a system that handles the pathfinding logic
 * for left-right moving entities. It moves entities based on calculated paths, ensuring
 * that the entities are guided towards their target.
 */
public class PlatformerPathfindingSystem extends GameSystem {

    /**
     * Starts the platformer pathfinding system by activating it.
     * This method marks the system as active and ready for updates.
     */
    @Override
    public void start() {
        this.active = true;
    }

    /**
     * Updates the pathfinding for entities with a PathfindingComponent.
     * It checks if the path has been calculated and moves the entity
     * towards its target if necessary.
     * <p>
     * If the entity has reached its current target node, the path continues,
     * and if the path is finished, the entity stops moving.
     */
    @Override
    protected void update() {
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
            if (start == null || end == null) {
                continue;
            }

            // If the path has not been calculated yet, compute the path
            if (pathfindingComponent.getPath() == null) {
                pathfindingComponent.setPath(Pathfinding.selectPath(entity));
            } else {
                // If there is a path, process it
                if (!pathfindingComponent.getPath().isEmpty()) {
                    // Get the next node in the path
                    Point node = pathfindingComponent.getPath().getFirst();

                    // Get the current position of the entity
                    Point position = new Point(entity.getComponent(CentralMassComponent.class).getCentralX(),
                            entity.getComponent(CentralMassComponent.class).getCentralY());

                    // If the entity reaches the target node, remove it from the path
                    if (position.compareCoordinates(node)) {
                        pathfindingComponent.getPath().removeFirst();
                    }
                }

                // If the path is empty, stop the entity's movement
                if (pathfindingComponent.getPath().isEmpty()) {
                    entity.getComponent(VelocityComponent.class).stopMovement();
                    pathfindingComponent.resetPathing(entity);
                }
            }
        }
    }
}
