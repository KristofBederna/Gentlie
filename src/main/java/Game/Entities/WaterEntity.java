package Game.Entities;

import inf.elte.hu.gameengine_javafx.Components.ShapeComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Rectangle;
import inf.elte.hu.gameengine_javafx.Misc.Configs.MapConfig;

public class WaterEntity extends Entity {
    public WaterEntity() {
        addComponent(new ShapeComponent<>(new Rectangle(new Point(0, 4 * MapConfig.scaledTileSize - 0.75 * MapConfig.scaledTileSize), MapConfig.scaledTileSize * MapConfig.chunkWidth, MapConfig.scaledTileSize * MapConfig.chunkHeight)));
        addToManager();
    }
}
