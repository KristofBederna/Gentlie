package Game.Systems;

import Game.Entities.PenguinEntity;
import Game.Entities.PolarBearEntity;
import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
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
import inf.elte.hu.gameengine_javafx.Misc.Config;
import inf.elte.hu.gameengine_javafx.Misc.Pathfinding;

import javax.swing.text.Position;
import java.util.Random;

public class PolarBearMoverSystem extends GameSystem {
    @Override
    public void start() {
        this.active = true;
    }

    @Override
    protected void update() {
        Random random = new Random();
        var entities = EntityHub.getInstance().getEntitiesWithType(PolarBearEntity.class);
        PlayerEntity player = (PlayerEntity) EntityHub.getInstance().getEntitiesWithType(PlayerEntity.class).getFirst();
        CentralMassComponent playerCentral = player.getComponent(CentralMassComponent.class);
        for (var entity : entities) {
            PathfindingComponent pathfinding = entity.getComponent(PathfindingComponent.class);
            CentralMassComponent entityCentral = entity.getComponent(CentralMassComponent.class);

            var mapMesh = WorldEntity.getInstance().getComponent(MapMeshComponent.class).getMapCoordinates();

            Point end = mapMesh.get(random.nextInt(1, 31)).get(random.nextInt(1, 31));

            if (playerCentral.getCentral().distanceTo(entityCentral.getCentral()) < 600) {
                end = mapMesh.get(Math.floorDiv((int) playerCentral.getCentralY(), (int) Config.scaledTileSize)).get(Math.floorDiv((int) playerCentral.getCentralX(), (int) Config.scaledTileSize));
                if (end == null) {
                    continue;
                }
                if (!end.compareCoordinates(pathfinding.getEnd())) {
                    pathfinding.resetPathing(entity);
                }
                entity.getComponent(VelocityComponent.class).setMaxVelocity(2);
            } else {
                entity.getComponent(VelocityComponent.class).setMaxVelocity(0.5);
            }

            pathfinding.setEnd(end);
        }
    }
}
