package Game.Systems;

import Game.Misc.EnemyStats;
import Game.Misc.PlayerStats;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;

public class PolarBearBufferSystem extends GameSystem {
    @Override
    public void start() {
        this.active = true;

        final double SCALING_FACTOR = 0.03;
        final double MAX_RESISTANCE = 0.85;

        double newRangedResistance = EnemyStats.rangedResistance + SCALING_FACTOR * Math.log(PlayerStats.rangedKills);
        newRangedResistance = Math.min(newRangedResistance, MAX_RESISTANCE);

        if (newRangedResistance < EnemyStats.rangedResistance) {
            newRangedResistance = EnemyStats.rangedResistance;
        }

        EnemyStats.rangedResistance = newRangedResistance;

        double newMeleeResistance = EnemyStats.meleeResistance + SCALING_FACTOR * Math.log(PlayerStats.meleeKills);
        newMeleeResistance = Math.min(newMeleeResistance, MAX_RESISTANCE);

        if (newMeleeResistance < EnemyStats.meleeResistance) {
            newMeleeResistance = EnemyStats.meleeResistance;
        }

        EnemyStats.meleeResistance = newMeleeResistance;

        PlayerStats.totalMeleeKills += PlayerStats.meleeKills;
        PlayerStats.totalRangedKills += PlayerStats.rangedKills;

        while (true) {
            int periods = 0;
            double health = EnemyStats.health;

            while (health > 0) {
                health -= PlayerStats.meleeDamage * (1 - EnemyStats.meleeResistance) * (1000.0 / PlayerStats.meleeCooldown);
                health -= PlayerStats.rangedDamage * (1 - EnemyStats.rangedResistance) * (1000.0 / PlayerStats.rangedCooldown);
                periods++;
            }

            if (periods > 2) {
                break;
            }

            EnemyStats.health += (PlayerStats.meleeDamage * (1 - EnemyStats.meleeResistance) +
                    PlayerStats.rangedDamage * (1 - EnemyStats.rangedResistance)) / 2;
        }


        PlayerStats.rangedKills = 0;
        PlayerStats.meleeKills = 0;
    }


    @Override
    protected void update() {

    }
}
