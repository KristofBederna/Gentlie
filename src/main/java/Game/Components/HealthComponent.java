package Game.Components;

import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;

public class HealthComponent extends Component {
    private double health;
    private long changedAt;

    public HealthComponent(double health) {
        this.health = health;
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        if (System.currentTimeMillis() - changedAt > 100) {
            this.health = health;
            changedAt = System.currentTimeMillis();
        }
    }

    public void increaseHealth(double amount) {
        if (System.currentTimeMillis() - changedAt > 100) {
            health += amount;
            changedAt = System.currentTimeMillis();
        }
    }

    public void decreaseHealth(double amount) {
        if (System.currentTimeMillis() - changedAt > 100) {
            health -= amount;
            changedAt = System.currentTimeMillis();
        }
    }
    public boolean isAlive() {
        return health > 0;
    }

    @Override
    public String getStatus() {
        return "";
    }
}
