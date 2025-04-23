package Game.Systems;

import Game.Misc.DayTimeData;
import Game.Misc.ShopItemPrices;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;

import java.util.Random;

public class ShopPriceUpdateSystem extends GameSystem {
    private final Random rand = new Random();

    @Override
    public void start() {
        this.active = true;
        int fullDaysPassed = (DayTimeData.periodsPassed / 2) - (DayTimeData.lastUpdatedPeriod / 2);
        System.out.println("Full Days Passed: " + fullDaysPassed);

        if (fullDaysPassed > 0) {
            for (int i = 0; i < fullDaysPassed; i++) {
                runPriceUpdateCycle();
            }
            DayTimeData.lastUpdatedPeriod = DayTimeData.periodsPassed;
        }
    }

    @Override
    protected void update() {

    }

    private void runPriceUpdateCycle() {
        System.out.println("updating prices");
        int[] prices = {
                ShopItemPrices.Health,
                ShopItemPrices.MeleeDamage,
                ShopItemPrices.RangedDamage,
                ShopItemPrices.MeleeSpeed,
                ShopItemPrices.RangedSpeed,
                ShopItemPrices.MeleeResistance,
                ShopItemPrices.RangedResistance
        };

        int[] baselines = {
                ShopItemPrices.BASELINE_Health,
                ShopItemPrices.BASELINE_MeleeDamage,
                ShopItemPrices.BASELINE_RangedDamage,
                ShopItemPrices.BASELINE_MeleeSpeed,
                ShopItemPrices.BASELINE_RangedSpeed,
                ShopItemPrices.BASELINE_MeleeResistance,
                ShopItemPrices.BASELINE_RangedResistance
        };

        boolean[] isSpiked = new boolean[prices.length];
        double total = 0;
        int count = 0;

        for (int i = 0; i < prices.length; i++) {
            if (prices[i] < baselines[i] * 5) {
                total += prices[i];
                count++;
            } else {
                isSpiked[i] = true;
            }
        }

        double avg = (count > 0) ? total / count : 100;

        for (int i = 0; i < prices.length; i++) {
            int newPrice;
            if (isSpiked[i]) {
                newPrice = (int) (avg * rand.nextDouble(0.95, 1.05));
            } else if (rand.nextDouble() <= 0.05) {
                newPrice = (int) (prices[i] * rand.nextDouble(8.5, 10.0));
            } else if (rand.nextBoolean()) {
                newPrice = (int) (prices[i] * rand.nextDouble(0.8, 1.2));
                newPrice = Math.max(50, newPrice);
            } else {
                newPrice = prices[i];
            }

            prices[i] = newPrice;
        }

        ShopItemPrices.Health = prices[0];
        ShopItemPrices.MeleeDamage = prices[1];
        ShopItemPrices.RangedDamage = prices[2];
        ShopItemPrices.MeleeSpeed = prices[3];
        ShopItemPrices.RangedSpeed = prices[4];
        ShopItemPrices.MeleeResistance = prices[5];
        ShopItemPrices.RangedResistance = prices[6];
    }
}
