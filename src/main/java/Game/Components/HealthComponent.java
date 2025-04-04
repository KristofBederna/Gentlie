package Game.Components;

import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;

public class HealthComponent extends Component {
    private int health;
    public HealthComponent(int health) {
        this.health = health;
    }
    public int getHealth() {
        return health;
    }
    public void setHealth(int health) {
        this.health = health;
    }
    public void increaseHealth() {
        health += 1;
    }
    public void decreaseHealth() {
        health -= 1;
    }
    public boolean isAlive() {
        return health > 0;
    }

    @Override
    public String getStatus() {
        return "";
    }
}
