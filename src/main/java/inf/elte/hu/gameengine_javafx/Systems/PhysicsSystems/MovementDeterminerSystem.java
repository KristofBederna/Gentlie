package inf.elte.hu.gameengine_javafx.Systems.PhysicsSystems;

import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.VelocityComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.StateComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Maths.Vector;

public class MovementDeterminerSystem extends GameSystem {
    @Override
    public void start() {
        this.active = true;
    }

    @Override
    protected void update() {
        var entities = EntityHub.getInstance().getEntitiesWithComponent(VelocityComponent.class);
        entities.retainAll(EntityHub.getInstance().getEntitiesWithComponent(StateComponent.class));
        for (var entity : entities) {
            VelocityComponent velocityComponent = entity.getComponent(VelocityComponent.class);
            Vector velocity = velocityComponent.getVelocity();
            StateComponent stateComponent = entity.getComponent(StateComponent.class);
            switch ((int) Math.signum(velocity.getDx())) {
                case -1:
                    stateComponent.setCurrentState("left");
                    break;
                case 1:
                    stateComponent.setCurrentState("right");
                    break;
                case 0:
                    stateComponent.setCurrentState("idle");
                    break;
            }
        }
    }
}
