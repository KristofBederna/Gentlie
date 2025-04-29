package Game.Systems;

import Game.Components.AttackBoxComponent;
import Game.Components.AttackTimeOutComponent;
import Game.Entities.PolarBearEntity;
import inf.elte.hu.gameengine_javafx.Components.PathfindingComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.VelocityComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.CentralMassComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.MapMeshComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Entities.PlayerEntity;
import inf.elte.hu.gameengine_javafx.Entities.WorldEntity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Misc.Configs.MapConfig;

import java.util.List;
import java.util.Random;

/**
 * System responsible for moving polar bear entities and handling their aggro behavior towards the player.
 */
public class PolarBearMoverSystem extends GameSystem {

    /**
     * Initializes the system and sets it to active.
     */
    @Override
    public void start() {
        this.active = true;
    }

    /**
     * Updates the positions of polar bears and handles their aggro behavior.
     * Finds a target goal for each bear based on the distance to the player or random map positions.
     */
    @Override
    protected void update() {
        Random random = new Random();
        var entities = EntityHub.getInstance().getEntitiesWithType(PolarBearEntity.class);
        PlayerEntity player = (PlayerEntity) EntityHub.getInstance().getEntitiesWithType(PlayerEntity.class).getFirst();
        CentralMassComponent playerCentral = player.getComponent(CentralMassComponent.class);
        for (var entity : entities) {
            if (entity == null) {
                return;
            }
            processEntity(entity, random, playerCentral);
        }
    }

    /**
     * Processes an individual polar bear entity, determining its next goal based on its aggro state or random position.
     *
     * @param entity        the polar bear entity to process
     * @param random        a random number generator for choosing random points
     * @param playerCentral the central point of the player entity
     */
    private void processEntity(Entity entity, Random random, CentralMassComponent playerCentral) {
        PathfindingComponent pathfinding = entity.getComponent(PathfindingComponent.class);
        CentralMassComponent entityCentral = entity.getComponent(CentralMassComponent.class);

        var mapMesh = WorldEntity.getInstance().getComponent(MapMeshComponent.class).getMapCoordinates();

        Point end = mapMesh.get(random.nextInt(1, 31)).get(random.nextInt(1, 31));

        if (playerCentral.getCentral().distanceTo(entityCentral.getCentral()) < 800) {
            end = getAggroGoal(entity, playerCentral, mapMesh, pathfinding);
            if (end == null) return;
        } else {
            resetFromAggroState(entity);
        }

        pathfinding.setEnd(end);
    }

    /**
     * Resets the polar bear's state after losing aggro with the player, including removing attack components and reducing velocity.
     * @param entity the polar bear entity to reset
     */
    private void resetFromAggroState(Entity entity) {
        entity.getComponent(VelocityComponent.class).setMaxVelocity(0.5);
        entity.removeComponentsByType(AttackTimeOutComponent.class);
        entity.removeComponentsByType(AttackBoxComponent.class);
    }

    /**
     * Gets the goal point for the polar bear when it is in an aggro state with the player.
     * @param entity the polar bear entity
     * @param playerCentral the central point of the player entity
     * @param mapMesh the map mesh of the world
     * @param pathfinding the pathfinding component of the bear entity
     * @return the goal point for the polar bear to move towards
     */
    private Point getAggroGoal(Entity entity, CentralMassComponent playerCentral, List<List<Point>> mapMesh, PathfindingComponent pathfinding) {
        Point end;
        end = mapMesh.get(Math.floorDiv((int) playerCentral.getCentralY(), (int) MapConfig.scaledTileSize)).get(Math.floorDiv((int) playerCentral.getCentralX(), (int) MapConfig.scaledTileSize));
        if (end == null) {
            return null;
        }
        if (!end.compareCoordinates(pathfinding.getEnd())) {
            pathfinding.resetPathing(entity);
        }
        entity.getComponent(VelocityComponent.class).setMaxVelocity(1.8);
        return end;
    }
}
