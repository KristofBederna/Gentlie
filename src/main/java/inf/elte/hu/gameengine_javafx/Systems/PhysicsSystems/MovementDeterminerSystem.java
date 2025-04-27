package inf.elte.hu.gameengine_javafx.Systems.PhysicsSystems;

import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.VelocityComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.DirectionComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.StateComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Maths.Vector;
import inf.elte.hu.gameengine_javafx.Misc.Direction;

/**
 * The MovementDeterminerSystem is responsible for determining and updating
 * the movement states of entities based on their velocity components.
 * It assigns the appropriate state (e.g., "left", "right", "up", "down", "idle")
 * and direction (e.g., Direction.LEFT, Direction.RIGHT) to entities based on their movement.
 */
public class MovementDeterminerSystem extends GameSystem {

    /**
     * Starts the movement determination system by activating it.
     * This method marks the system as active and ready for updates.
     */
    @Override
    public void start() {
        this.active = true;
    }

    /**
     * Updates the movement states and directions of entities with both
     * VelocityComponent and StateComponent.
     * It processes each entity and determines if the movement is in the left-right or up-down direction.
     */
    @Override
    public void update() {
        // Get all entities that have both a VelocityComponent and StateComponent
        var entities = EntityHub.getInstance().getEntitiesWithComponent(VelocityComponent.class);
        entities.retainAll(EntityHub.getInstance().getEntitiesWithComponent(StateComponent.class));

        // Iterate through all eligible entities
        for (var entity : entities) {
            // Skip null entities
            if (entity == null) {
                continue;
            }
            // Process movement and states for the entity
            processEntity(entity);
        }
    }

    /**
     * Processes the movement state and direction for a single entity.
     * Determines the left-right and up-down movement based on the entity's velocity.
     *
     * @param entity The entity to process.
     */
    private void processEntity(Entity entity) {
        // Get the required components of the entity
        VelocityComponent velocityComponent = entity.getComponent(VelocityComponent.class);
        Vector velocity = velocityComponent.getVelocity();
        StateComponent stateComponent = entity.getComponent(StateComponent.class);
        DirectionComponent directionComponent = entity.getComponent(DirectionComponent.class);

        // Determine movement in the left-right direction
        determineLeftRightMovement(velocity, stateComponent, directionComponent);

        // Determine movement in the up-down direction
        determineUpDownMovement(velocity, stateComponent, directionComponent);
    }

    /**
     * Determines and updates the up-down movement state and direction based on the velocity.
     *
     * @param velocity           The velocity of the entity.
     * @param stateComponent     The state component of the entity.
     * @param directionComponent The direction component of the entity.
     */
    private void determineUpDownMovement(Vector velocity, StateComponent stateComponent, DirectionComponent directionComponent) {
        switch ((int) Math.signum(velocity.getDy())) {
            case -1:
                // Moving up
                if (Math.abs(velocity.getDx()) < Math.abs(velocity.getDy())) {
                    stateComponent.setCurrentState("up");
                    directionComponent.setDirection(Direction.UP);
                }
                break;
            case 1:
                // Moving down
                if (Math.abs(velocity.getDx()) < Math.abs(velocity.getDy())) {
                    stateComponent.setCurrentState("down");
                    directionComponent.setDirection(Direction.DOWN);
                }
                break;
            case 0:
                // Idle (no vertical movement)
                if (!stateComponent.getCurrentState().equals("idle")) {
                    return;
                }
                stateComponent.setCurrentState("idle");
                directionComponent.setDirection(Direction.ALL);
        }
    }

    /**
     * Determines and updates the left-right movement state and direction based on the velocity.
     *
     * @param velocity The velocity of the entity.
     * @param stateComponent The state component of the entity.
     * @param directionComponent The direction component of the entity.
     */
    private void determineLeftRightMovement(Vector velocity, StateComponent stateComponent, DirectionComponent directionComponent) {
        switch ((int) Math.signum(velocity.getDx())) {
            case -1:
                // Moving left
                stateComponent.setCurrentState("left");
                directionComponent.setDirection(Direction.LEFT);
                break;
            case 1:
                // Moving right
                stateComponent.setCurrentState("right");
                directionComponent.setDirection(Direction.RIGHT);
                break;
            case 0:
                // Idle (no horizontal movement)
                stateComponent.setCurrentState("idle");
                directionComponent.setDirection(Direction.ALL);
                break;
        }
    }
}
