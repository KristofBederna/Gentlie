package Game.Misc;

import Game.Misc.Enums.Daytime;

public class DayTimeData {
    public static long lastUpdate = System.currentTimeMillis();
    public static Daytime lastDayTime = Daytime.DAY;

    public static int periodsPassed = 0;
}
