package Game.Entities;

import Game.Components.DaytimeComponent;
import Game.Misc.Enums.Daytime;
import inf.elte.hu.gameengine_javafx.Components.RenderingComponents.ZIndexComponent;
import inf.elte.hu.gameengine_javafx.Components.ShapeComponent;
import inf.elte.hu.gameengine_javafx.Components.TimeComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Rectangle;
import inf.elte.hu.gameengine_javafx.Misc.Config;

public class SkyBoxEntity extends Entity {
    public SkyBoxEntity() {
        addComponent(new ShapeComponent<>(new Rectangle(new Point(0, 0), Config.scaledTileSize *Config.chunkWidth, Config.scaledTileSize *Config.chunkHeight)));
        addComponent(new DaytimeComponent(Daytime.DAY));
        addComponent(new TimeComponent(100000));

        addToManager();
    }
}
