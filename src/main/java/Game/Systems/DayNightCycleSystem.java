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

        handleDayTimeSwitching(periodsPassed, daytime);
        updateDayTimeData(time, timePassedThisPeriod, periodsPassed);
    }

    private void updateDayTimeData(TimeComponent time, long timePassedThisPeriod, int periodsPassed) {
        time.setLastOccurrence(System.currentTimeMillis() - timePassedThisPeriod);
        DayTimeData.periodsPassed += periodsPassed;
    }

    private void handleDayTimeSwitching(int periodsPassed, DaytimeComponent daytime) {
        if (periodsPassed % 2 == 1) {
            if (DayTimeData.lastDayTime == Daytime.DAY) {
                daytime.setDaytime(Daytime.NIGHT);
            } else {
                daytime.setDaytime(Daytime.DAY);
            }
        } else {
            daytime.setDaytime(DayTimeData.lastDayTime);
        }
    }


    @Override
    protected void update() {
        SkyBoxEntity skyBox = (SkyBoxEntity) EntityHub.getInstance().getEntitiesWithComponent(DaytimeComponent.class).getFirst();

        DaytimeComponent daytime = skyBox.getComponent(DaytimeComponent.class);
        TimeComponent time = skyBox.getComponent(TimeComponent.class);

        updateDayTimeComponent(time, daytime);

        DayTimeData.lastUpdate = System.currentTimeMillis();
    }

    private void updateDayTimeComponent(TimeComponent time, DaytimeComponent daytime) {
        if (System.currentTimeMillis() >= time.getLastOccurrence() + time.getTimeBetweenOccurrences()) {
            setNewDayTime(daytime);
            DayTimeData.periodsPassed++;
            time.setLastOccurrence();
        }
    }

    private void setNewDayTime(DaytimeComponent daytime) {
        switch (daytime.getDaytime()) {
            case DAY:
                daytime.setDaytime(Daytime.NIGHT);
                DayTimeData.lastDayTime = Daytime.NIGHT;
                break;
            case NIGHT:
                daytime.setDaytime(Daytime.DAY);
                DayTimeData.lastDayTime = Daytime.DAY;
                break;
        }
    }
}
