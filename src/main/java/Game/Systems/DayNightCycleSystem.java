package Game.Systems;

import Game.Components.DaytimeComponent;
import Game.Entities.SkyBoxEntity;
import Game.Misc.DayTimeData;
import Game.Misc.Enums.Daytime;
import inf.elte.hu.gameengine_javafx.Components.TimeComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;

/**
 * The DayNightCycleSystem controls the switching between day and night in the game world.
 * It updates the DaytimeComponent of the SkyBoxEntity based on time passed,
 * and keeps track of the day/night state using DayTimeData.
 */
public class DayNightCycleSystem extends GameSystem {

    /**
     * Initializes the system and sets the current daytime state based on the time passed since last update (since it doesn't update on scenes without a skybox).
     */
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

    /**
     * Updates the DayTimeData with the number of periods passed and the corrected last occurrence time.
     *
     * @param time                 the TimeComponent used for tracking periods
     * @param timePassedThisPeriod milliseconds passed in the current incomplete period
     * @param periodsPassed        number of full periods passed since last update
     */
    private void updateDayTimeData(TimeComponent time, long timePassedThisPeriod, int periodsPassed) {
        time.setLastOccurrence(System.currentTimeMillis() - timePassedThisPeriod);
        DayTimeData.periodsPassed += periodsPassed;
    }

    /**
     * Switches the current daytime if the number of periods passed is odd.
     * Keeps the previous state if even.
     *
     * @param periodsPassed number of full periods passed since last update
     * @param daytime       the DaytimeComponent to update
     */
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

    /**
     * Called every frame to check if it's time to change from day to night or vice versa.
     * Updates the DaytimeComponent accordingly.
     */
    @Override
    protected void update() {
        SkyBoxEntity skyBox = (SkyBoxEntity) EntityHub.getInstance()
                .getEntitiesWithComponent(DaytimeComponent.class)
                .getFirst();

        DaytimeComponent daytime = skyBox.getComponent(DaytimeComponent.class);
        TimeComponent time = skyBox.getComponent(TimeComponent.class);

        updateDayTimeComponent(time, daytime);
        DayTimeData.lastUpdate = System.currentTimeMillis();
    }

    /**
     * Checks whether the configured time between changes has passed, and updates daytime if so.
     *
     * @param time    the TimeComponent used for timing day/night cycle
     * @param daytime the DaytimeComponent to update
     */
    private void updateDayTimeComponent(TimeComponent time, DaytimeComponent daytime) {
        if (System.currentTimeMillis() >= time.getLastOccurrence() + time.getTimeBetweenOccurrences()) {
            setNewDayTime(daytime);
            DayTimeData.periodsPassed++;
            time.setLastOccurrence();
        }
    }

    /**
     * Toggles the current daytime between DAY and NIGHT, and updates the DayTimeData#lastDayTime.
     *
     * @param daytime the DaytimeComponent to toggle
     */
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
