package Game.Systems;

import Game.Components.DaytimeComponent;
import Game.Entities.SkyBoxEntity;
import Game.Misc.Enums.Daytime;
import inf.elte.hu.gameengine_javafx.Components.TimeComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;

public class DayNightCycleSystem extends GameSystem {
    @Override
    public void start() {
        this.active = true;
    }

    @Override
    protected void update() {
        SkyBoxEntity skyBox = (SkyBoxEntity) EntityHub.getInstance().getEntitiesWithComponent(DaytimeComponent.class).getFirst();

        DaytimeComponent daytime = skyBox.getComponent(DaytimeComponent.class);
        TimeComponent time = skyBox.getComponent(TimeComponent.class);

        if (System.currentTimeMillis() >= time.getLastOccurrence() + time.getTimeBetweenOccurrences()) {
            switch (daytime.getDaytime()) {
                case DAY:
                    daytime.setDaytime(Daytime.NIGHT);
                    break;
                case NIGHT:
                    daytime.setDaytime(Daytime.DAY);
                    break;
            }
            time.setLastOccurrence();
        }
    }
}
