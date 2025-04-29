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

public class PenguinMoverSystem extends GameSystem {
    @Override
    public void start() {
        this.active = true;
    }

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

    private void processEntity(Entity entity, Random random) {
        PathfindingComponent pathfinding = entity.getComponent(PathfindingComponent.class);
        if (pathfinding != null) {
            Point end = WorldEntity.getInstance().getComponent(MapMeshComponent.class).getMapCoordinates().get(2).get(random.nextInt(3, 10));
            pathfinding.setEnd(end);
        }
    }
}
