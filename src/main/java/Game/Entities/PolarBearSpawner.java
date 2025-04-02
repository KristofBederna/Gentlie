package Game.Entities;

import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Misc.Config;

public class PolarBearSpawner extends Entity {
    private boolean spawned = false;

    public PolarBearSpawner(double x, double y) {
        this.getComponent(PositionComponent.class).setGlobal(new Point(x, y));

        addToManager();
    }

    public void spawn() {
        if (spawned) {
            return;
        }
        Point p = this.getComponent(PositionComponent.class).getGlobal();
        new PolarBearEntity(p.getX()- Config.scaledTileSize*0.75/2, p.getY()-Config.scaledTileSize*0.75/2, "idle", "/assets/images/PolarBears/Polar_Bear_Down_Idle.png", Config.scaledTileSize*0.75, Config.scaledTileSize*0.75);
        spawned = true;
    }
}
