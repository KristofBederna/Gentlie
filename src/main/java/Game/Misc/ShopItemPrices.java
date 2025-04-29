package Game.Misc;

public class ShopItemPrices {
    public static int Health = 75;
    public static int MeleeDamage = 100;
    public static int RangedDamage = 100;
    public static int MeleeSpeed = 150;
    public static int RangedSpeed = 175;
    public static int MeleeResistance = 125;
    public static int RangedResistance = 125;

    public static final int BASELINE_Health = 75;
    public static final int BASELINE_MeleeDamage = 100;
    public static final int BASELINE_RangedDamage = 100;
    public static final int BASELINE_MeleeSpeed = 150;
    public static final int BASELINE_RangedSpeed = 175;
    public static final int BASELINE_MeleeResistance = 125;
    public static final int BASELINE_RangedResistance = 125;

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
