package Game.Systems;

import Game.Misc.EnemyStats;
import Game.Misc.PlayerStats;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;

/**
 * System responsible for handling the buffering of polar bear attributes,
 * including updating resistance values based on player kills and adjusting bear health.
 */
public class PolarBearBufferSystem extends GameSystem {

    /**
     * Starts the system by calculating the updated resistance values for polar bears
     * based on player kills and resets the player's kill counts.
     */
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
            if (determineBearsHealth()) break;
        }

        PlayerStats.rangedKills = 0;
        PlayerStats.meleeKills = 0;
    }

    /**
     * Determines whether the polar bear's health has been depleted after applying simulated damage over time.
     *
     * @return {@code True} if the bear's health is fully depleted, {@code False} otherwise
     */
    private boolean determineBearsHealth() {
        int periods = 0;
        double health = EnemyStats.health;

        periods = simulateDPS(health, periods);

        if (periods > 2) {
            return true;
        }

        EnemyStats.health += (PlayerStats.meleeDamage * (1 - EnemyStats.meleeResistance) +
                PlayerStats.rangedDamage * (1 - EnemyStats.rangedResistance)) / 2;
        return false;
    }

    /**
     * Simulates damage per second (DPS) for the polar bear, reducing its health based on player damage
     * and resistance over multiple periods.
     * @param health the initial health of the polar bear
     * @param periods the current number of periods during the simulation
     * @return the number of periods it takes for the bear's health to deplete
     */
    private int simulateDPS(double health, int periods) {
        while (health > 0) {
            health -= PlayerStats.meleeDamage * (1 - EnemyStats.meleeResistance) * (1000.0 / PlayerStats.meleeCooldown);
            health -= PlayerStats.rangedDamage * (1 - EnemyStats.rangedResistance) * (1000.0 / PlayerStats.rangedCooldown);
            periods++;
        }
        return periods;
    }

    /**
     * Updates the system, but no specific behavior is implemented for the update method.
     */
    @Override
    protected void update() {

    }
}
