package Game.Misc;

public class ShopItemPrices {
    public static int Health = 100;
    public static int MeleeDamage = 100;
    public static int RangedDamage = 100;
    public static int MeleeSpeed = 100;
    public static int RangedSpeed = 100;
    public static int MeleeResistance = 100;
    public static int RangedResistance = 100;

    public static final int BASELINE_Health = 100;
    public static final int BASELINE_MeleeDamage = 100;
    public static final int BASELINE_RangedDamage = 100;
    public static final int BASELINE_MeleeSpeed = 100;
    public static final int BASELINE_RangedSpeed = 100;
    public static final int BASELINE_MeleeResistance = 100;
    public static final int BASELINE_RangedResistance = 100;

    public static void resetToBaseline() {
        Health = BASELINE_Health;
        MeleeDamage = BASELINE_MeleeDamage;
        RangedDamage = BASELINE_RangedDamage;
        MeleeSpeed = BASELINE_MeleeSpeed;
        RangedSpeed = BASELINE_RangedSpeed;
        MeleeResistance = BASELINE_MeleeResistance;
        RangedResistance = BASELINE_RangedResistance;
    }
}
