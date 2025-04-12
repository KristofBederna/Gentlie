package Game.Misc;

public class PlayerStats {
    public static String currentSave = "";

    public static double health = 100;
    public static double meleeDamage = 53;
    public static double rangedDamage = 34;
    public static double meleeResistance = 0;
    public static double rangedResistance = 0;
    public static int gold = 1000;
    public static int meleeKills = 0;
    public static int rangedKills = 0;
    public static int totalMeleeKills = 0;
    public static int totalRangedKills = 0;
    public static long meleeCooldown = 2000;
    public static long rangedCooldown = 3000;

    public static final double BASELINE_Health = 100;
    public static final double BASELINE_MeleeDamage = 53;
    public static final double BASELINE_RangedDamage = 34;
    public static final double BASELINE_MeleeResistance = 0;
    public static final double BASELINE_RangedResistance = 0;
    public static final int BASELINE_Gold = 1000;
    public static final int BASELINE_MeleeKills = 0;
    public static final int BASELINE_RangedKills = 0;
    public static final int BASELINE_TotalMeleeKills = 0;
    public static final int BASELINE_TotalRangedKills = 0;
    public static final long BASELINE_MeleeCooldown = 2000;
    public static final long BASELINE_RangedCooldown = 3000;

    public static void resetToBaseline() {
        health = BASELINE_Health;
        meleeDamage = BASELINE_MeleeDamage;
        rangedDamage = BASELINE_RangedDamage;
        meleeResistance = BASELINE_MeleeResistance;
        rangedResistance = BASELINE_RangedResistance;
        gold = BASELINE_Gold;
        meleeKills = BASELINE_MeleeKills;
        rangedKills = BASELINE_RangedKills;
        totalMeleeKills = BASELINE_TotalMeleeKills;
        totalRangedKills = BASELINE_TotalRangedKills;
        meleeCooldown = BASELINE_MeleeCooldown;
        rangedCooldown = BASELINE_RangedCooldown;
    }
}