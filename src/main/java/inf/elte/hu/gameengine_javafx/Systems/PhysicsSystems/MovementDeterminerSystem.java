package inf.elte.hu.gameengine_javafx.Systems.PhysicsSystems;

import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.VelocityComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.DirectionComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.StateComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Maths.Vector;
import inf.elte.hu.gameengine_javafx.Misc.Direction;

public class MovementDeterminerSystem extends GameSystem {
    @Override
    public void start() {
        this.active = true;
    }

    @Override
    public void update() {
        var entities = EntityHub.getInstance().getEntitiesWithComponent(VelocityComponent.class);
        entities.retainAll(EntityHub.getInstance().getEntitiesWithComponent(StateComponent.class));
        for (var entity : entities) {
            processEntity(entity);
        }
    }

    private void processEntity(Entity entity) {
        if (entity == null) {
            return;
        }
        VelocityComponent velocityComponent = entity.getComponent(VelocityComponent.class);
        Vector velocity = velocityComponent.getVelocity();
        StateComponent stateComponent = entity.getComponent(StateComponent.class);
        DirectionComponent directionComponent = entity.getComponent(DirectionComponent.class);

        determineLeftRightMovement(velocity, stateComponent, directionComponent);
        determineUpDownMovement(velocity, stateComponent, directionComponent);
    }

    private void determineUpDownMovement(Vector velocity, StateComponent stateComponent, DirectionComponent directionComponent) {
        switch ((int) Math.signum(velocity.getDy())) {
            case -1:
                if (Math.abs(velocity.getDx()) < Math.abs(velocity.getDy())) {
                    stateComponent.setCurrentState("up");
                    directionComponent.setDirection(Direction.UP);
                }
                break;
            case 1:
                if (Math.abs(velocity.getDx()) < Math.abs(velocity.getDy())) {
                    stateComponent.setCurrentState("down");
                    directionComponent.setDirection(Direction.DOWN);
                }
                break;
            case 0:
                if (!stateComponent.getCurrentState().equals("idle")) {
                    return;
                }
                stateComponent.setCurrentState("idle");
                directionComponent.setDirection(Direction.ALL);
        }
    }

    private void determineLeftRightMovement(Vector velocity, StateComponent stateComponent, DirectionComponent directionComponent) {
        switch ((int) Math.signum(velocity.getDx())) {
            case -1:
                stateComponent.setCurrentState("left");
                directionComponent.setDirection(Direction.LEFT);
                break;
            case 1:
                stateComponent.setCurrentState("right");
                directionComponent.setDirection(Direction.RIGHT);
                break;
            case 0:
                stateComponent.setCurrentState("idle");
                directionComponent.setDirection(Direction.ALL);
                break;
        }
    }
}
