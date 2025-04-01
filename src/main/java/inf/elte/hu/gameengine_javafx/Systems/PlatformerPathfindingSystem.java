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
import inf.elte.hu.gameengine_javafx.Misc.Config;
import inf.elte.hu.gameengine_javafx.Misc.Pathfinding;
import inf.elte.hu.gameengine_javafx.Misc.Time;

public class PlatformerPathfindingSystem extends GameSystem {

    @Override
    public void start() {
        this.active = true;
    }

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

                    StateComponent state = entity.getComponent(StateComponent.class);
                    String previousState = state.getCurrentState();

                    if (deltaX > 0) { // Moving right
                        state.setCurrentState("right");
                        moveRight(entity);
                        if ("left".equals(previousState)) counterLeft(entity);
                    } else if (deltaX < 0) { // Moving left
                        state.setCurrentState("left");
                        moveLeft(entity);
                        if ("right".equals(previousState)) counterRight(entity);
                    }

                    if (position.compareCoordinates(node)) {
                        pathfindingComponent.getPath().removeFirst();
                        if (pathfindingComponent.getPath().isEmpty()) {
                            entity.getComponent(StateComponent.class).setCurrentState("idle");
                        }
                    }
                }

                if (pathfindingComponent.getPath().isEmpty()) {
                    entity.getComponent(VelocityComponent.class).stopMovement();
                    pathfindingComponent.resetPathing(entity);
                }
            }
        }
    }

    private void moveLeft(Entity e) {
        double dx = -4 * Time.getInstance().getDeltaTime() * Config.getTileScale();
        e.getComponent(AccelerationComponent.class).getAcceleration().setDx(dx);
    }

    private void moveRight(Entity e) {
        double dx = 4 * Time.getInstance().getDeltaTime() * Config.getTileScale();
        e.getComponent(AccelerationComponent.class).getAcceleration().setDx(dx);
    }

    private void counterRight(Entity e) {
        e.getComponent(AccelerationComponent.class).getAcceleration().setDx(0);
    }

    private void counterLeft(Entity e) {
        e.getComponent(AccelerationComponent.class).getAcceleration().setDx(0);
    }
}
