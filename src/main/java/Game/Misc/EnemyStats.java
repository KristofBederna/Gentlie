package Game.Misc;

public class EnemyStats {
    public static double health = 100;
    public static double meleeDamage = 14;
    public static double rangedDamage = 34;
    public static double meleeResistance = 0;
    public static double rangedResistance = 0;

    public static final double BASELINE_Health = 100;
    public static final double BASELINE_MeleeDamage = 14;
    public static final double BASELINE_RangedDamage = 34;
    public static final double BASELINE_MeleeResistance = 0;
    public static final double BASELINE_RangedResistance = 0;

    public static void resetToBaseline() {
        health = BASELINE_Health;
        meleeDamage = BASELINE_MeleeDamage;
        rangedDamage = BASELINE_RangedDamage;
        meleeResistance = BASELINE_MeleeResistance;
        rangedResistance = BASELINE_RangedResistance;
    }
}