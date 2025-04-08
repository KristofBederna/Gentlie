package inf.elte.hu.gameengine_javafx.Systems;

import inf.elte.hu.gameengine_javafx.Components.PathfindingComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.AccelerationComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.VelocityComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.CentralMassComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.StateComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Misc.Pathfinding;
import inf.elte.hu.gameengine_javafx.Misc.Time;

public class PathfindingSystem extends GameSystem {

    @Override
    public void start() {
        this.active = true;
    }

    @Override
    protected void update() {
        var pathfinderEntities = EntityHub.getInstance().getEntitiesWithComponent(PathfindingComponent.class);

        for (var entity : pathfinderEntities) {
            if (entity == null) {
                continue;
            }
            PathfindingComponent pathfindingComponent = entity.getComponent(PathfindingComponent.class);
            Point start = pathfindingComponent.getStart();
            Point end = pathfindingComponent.getEnd();

            if (start == null || end == null) continue;

            if (pathfindingComponent.getPath() == null) {
                pathfindingComponent.setPath(Pathfinding.selectPath(entity));
            } else if (!pathfindingComponent.getPath().isEmpty()) {
                Point node = pathfindingComponent.getPath().getFirst();
                Point position = new Point(
                        entity.getComponent(CentralMassComponent.class).getCentralX(),
                        entity.getComponent(CentralMassComponent.class).getCentralY()
                );

                double deltaX = node.getX() - position.getX();
                double deltaY = node.getY() - position.getY();

                double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
                if (distance > 0.01) {
                    double normDx = deltaX / distance;
                    double normDy = deltaY / distance;

                    double speed = 4 * Time.getInstance().getDeltaTime();
                    AccelerationComponent accel = entity.getComponent(AccelerationComponent.class);
                    accel.getAcceleration().setDx(normDx * speed);
                    accel.getAcceleration().setDy(normDy * speed);

                    StateComponent state = entity.getComponent(StateComponent.class);
                    if (Math.abs(normDx) > Math.abs(normDy)) {
                        state.setCurrentState(normDx > 0 ? "right" : "left");
                    } else {
                        state.setCurrentState(normDy > 0 ? "down" : "up");
                    }
                }

                if (position.compareCoordinates(node)) {
                    pathfindingComponent.getPath().removeFirst();
                    if (pathfindingComponent.getPath().isEmpty()) {
                        entity.getComponent(StateComponent.class).setCurrentState("idle");
                    }
                }
            }

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
