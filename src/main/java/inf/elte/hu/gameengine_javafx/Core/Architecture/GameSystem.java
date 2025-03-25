package inf.elte.hu.gameengine_javafx.Core.Architecture;

import inf.elte.hu.gameengine_javafx.Core.SystemHub;

/**
 * Base class for every GameSystem.
 * <br>
 * GameSystems are responsible for calculating data.
 */
public abstract class GameSystem {
    //Status booleans.
    protected boolean active = false;
    protected boolean aborting = false;

    /**
     * While the GameSystem is active their update cycle gets executed every frame the run method is called.
     */
    public void run() {
        if (active) {
            update();
        }
    }

    /**
     * A function that only gets executed once on the initiation of the GameSystem.
     */
    public abstract void start();

    /**
     * A function that gets executed on every frame that the GameSystem's run method is called, while the GameSystem is active.
     */
    protected abstract void update();

    /**
     * Sets the status booleans for GameSystem aborting.
     */
    public void abort() {
        active = false;
        aborting = true;
    }

    /**
     * @return The priority of the GameSystem. Priority meaning in which order the GameSystems are executed.
     */
    public Integer getPriority() {
        return SystemHub.getInstance().getAllSystemsInPriorityOrder().indexOf(this);
    }

    public boolean getIsActive() {
        return active;
    }

    public boolean getIsAborting() {
        return aborting;
    }
}
