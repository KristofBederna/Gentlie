package Game.Entities;

import inf.elte.hu.gameengine_javafx.Components.ShapeComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Rectangle;
import inf.elte.hu.gameengine_javafx.Misc.Config;

public class WaterEntity extends Entity {
    public WaterEntity() {
        addComponent(new ShapeComponent<>(new Rectangle(new Point(0, 4*Config.scaledTileSize -0.75*Config.scaledTileSize), Config.scaledTileSize *Config.chunkWidth, Config.scaledTileSize *Config.chunkHeight)));
        addToManager();
    }
}
