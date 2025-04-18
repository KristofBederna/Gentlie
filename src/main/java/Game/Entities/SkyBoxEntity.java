package Game.Entities;

import Game.Components.DaytimeComponent;
import Game.Misc.DayTimeData;
import inf.elte.hu.gameengine_javafx.Components.ShapeComponent;
import inf.elte.hu.gameengine_javafx.Components.TimeComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Rectangle;
import inf.elte.hu.gameengine_javafx.Misc.Configs.MapConfig;

public class SkyBoxEntity extends Entity {
    public SkyBoxEntity() {
        addComponent(new ShapeComponent<>(new Rectangle(new Point(0, 0), MapConfig.scaledTileSize * MapConfig.chunkWidth, MapConfig.scaledTileSize * MapConfig.chunkHeight)));
        addComponent(new DaytimeComponent(DayTimeData.lastDayTime));
        addComponent(new TimeComponent(2 * 60000)); //1 time period = 2 minutes, 1 day = 4 minutes

        addToManager();
    }
}
