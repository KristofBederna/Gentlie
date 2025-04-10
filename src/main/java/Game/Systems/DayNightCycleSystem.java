package Game.Systems;

import Game.Components.DaytimeComponent;
import Game.Entities.SkyBoxEntity;
import Game.Misc.DayTimeData;
import Game.Misc.Enums.Daytime;
import inf.elte.hu.gameengine_javafx.Components.TimeComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;

public class DayNightCycleSystem extends GameSystem {
    @Override
    public void start() {
        this.active = true;
        SkyBoxEntity skyBox = (SkyBoxEntity) EntityHub.getInstance()
                .getEntitiesWithComponent(DaytimeComponent.class)
                .getFirst();

        DaytimeComponent daytime = skyBox.getComponent(DaytimeComponent.class);
        TimeComponent time = skyBox.getComponent(TimeComponent.class);

        long currentTime = System.currentTimeMillis();
        long difference = currentTime - DayTimeData.lastUpdate;
        int periodsPassed = (int) (difference / time.getTimeBetweenOccurrences());
        long timePassedThisPeriod = difference % time.getTimeBetweenOccurrences();

        if (periodsPassed % 2 == 1) {
            if (DayTimeData.lastDayTime == Daytime.DAY) {
                daytime.setDaytime(Daytime.NIGHT);
            } else {
                daytime.setDaytime(Daytime.DAY);
            }
        } else {
            daytime.setDaytime(DayTimeData.lastDayTime);
        }

        time.setLastOccurrence(System.currentTimeMillis() - timePassedThisPeriod);
        DayTimeData.periodsPassed += periodsPassed;
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
                    DayTimeData.lastDayTime = Daytime.DAY;
                    break;
                case NIGHT:
                    daytime.setDaytime(Daytime.DAY);
                    DayTimeData.lastDayTime = Daytime.NIGHT;
                    break;
            }
            DayTimeData.periodsPassed++;
            time.setLastOccurrence();
        }
        DayTimeData.lastUpdate = System.currentTimeMillis();
    }
}
