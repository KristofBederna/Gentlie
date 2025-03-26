package Game.Entities;

import inf.elte.hu.gameengine_javafx.Components.ShapeComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Rectangle;
import inf.elte.hu.gameengine_javafx.Misc.Config;

public class WaterEntity extends Entity {
    public WaterEntity() {
        addComponent(new ShapeComponent<>(new Rectangle(new Point(0, 4*Config.tileSize-0.75*Config.tileSize), Config.tileSize*Config.chunkWidth, Config.tileSize*Config.chunkHeight)));
        addToManager();
    }
}
