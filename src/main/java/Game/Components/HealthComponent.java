package Game.Components;

import Game.Misc.CauseOfDeath;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;

public class HealthComponent extends Component {
    private double health;
    private long changedAt;
    private CauseOfDeath causeOfDeath;

    public HealthComponent(double health) {
        this.health = health;
    }

    public CauseOfDeath getCauseOfDeath() {
        return causeOfDeath;
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health, CauseOfDeath causeOfDeath) {
        if (System.currentTimeMillis() - changedAt > 100) {
            this.health = health;
            changedAt = System.currentTimeMillis();
            this.causeOfDeath = causeOfDeath;
        }
    }

    public void increaseHealth(double amount) {
        if (System.currentTimeMillis() - changedAt > 100) {
            health += amount;
            changedAt = System.currentTimeMillis();
        }
    }

    public void decreaseHealth(double amount, CauseOfDeath causeOfDeath) {
        if (System.currentTimeMillis() - changedAt > 100) {
            health -= amount;
            changedAt = System.currentTimeMillis();
            if (health <= 0) {
                this.causeOfDeath = causeOfDeath;
            }
        }
    }
    public boolean isAlive() {
        return health > 0;
    }
}
