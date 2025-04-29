package Game.Systems;

import Game.Entities.PenguinEntity;
import inf.elte.hu.gameengine_javafx.Components.PathfindingComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.MapMeshComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Entities.WorldEntity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;

import java.util.Random;

/**
 * System responsible for giving random movement targets to all PenguinEntity instances
 * by assigning a random map coordinate as their pathfinding destination.
 */
public class PenguinMoverSystem extends GameSystem {

    /**
     * Activates the system.
     */
    @Override
    public void start() {
        this.active = true;
    }

    /**
     * Iterates over all PenguinEntity instances and assigns each a new random destination point
     * using the pathfinding system. The destination is chosen from predefined map coordinates.
     */
    @Override
    protected void update() {
        Random random = new Random();
        var entities = EntityHub.getInstance().getEntitiesWithType(PenguinEntity.class);
        for (var entity : entities) {
            if (entity == null) {
                return;
            }
            processEntity(entity, random);
        }
    }

    /**
     * Assigns a random target point to the entity's PathfindingComponent from the map coordinates.
     *
     * @param entity the penguin entity to process
     * @param random the random number generator used to select the destination
     */
    private void processEntity(Entity entity, Random random) {
        PathfindingComponent pathfinding = entity.getComponent(PathfindingComponent.class);
        if (pathfinding != null) {
            Point end = WorldEntity.getInstance()
                    .getComponent(MapMeshComponent.class)
                    .getMapCoordinates()
                    .get(2)
                    .get(random.nextInt(3, 10));
            pathfinding.setEnd(end);
        }
    }
}
