package Game.Systems;

import Game.Misc.EnemyStats;
import Game.Misc.PlayerStats;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;

public class PolarBearBufferSystem extends GameSystem {
    @Override
    public void start() {
        this.active = true;

        final double SCALING_FACTOR = 0.05;
        final double MAX_RESISTANCE = 0.5;

        double newRangedResistance = EnemyStats.rangedResistance + SCALING_FACTOR * Math.log1p(PlayerStats.rangedKills);
        newRangedResistance = Math.min(newRangedResistance, MAX_RESISTANCE);

        if (newRangedResistance < EnemyStats.rangedResistance) {
            newRangedResistance = EnemyStats.rangedResistance;
        }

        EnemyStats.rangedResistance = newRangedResistance;

        double newMeleeResistance = EnemyStats.meleeResistance + SCALING_FACTOR * Math.log1p(PlayerStats.meleeKills);
        newMeleeResistance = Math.min(newMeleeResistance, MAX_RESISTANCE);

        if (newMeleeResistance < EnemyStats.meleeResistance) {
            newMeleeResistance = EnemyStats.meleeResistance;
        }

        EnemyStats.meleeResistance = newMeleeResistance;

        PlayerStats.totalMeleeKills += PlayerStats.meleeKills;
        PlayerStats.totalRangedKills += PlayerStats.rangedKills;

        PlayerStats.rangedKills = 0;
        PlayerStats.meleeKills = 0;
    }


    @Override
    protected void update() {

    }
}
