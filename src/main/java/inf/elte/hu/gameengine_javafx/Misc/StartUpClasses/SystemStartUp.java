package inf.elte.hu.gameengine_javafx.Misc.StartUpClasses;

import inf.elte.hu.gameengine_javafx.Core.SystemHub;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.SceneManagementSystem;

/**
 * Initializes and manages the startup process of game systems.
 * <br>
 * This class ensures that all necessary game systems are registered and started in the {@link SystemHub}.
 */
public class SystemStartUp {

    /**
     * Constructs a new {@code SystemStartUp} instance.
     * <br>
     * This initializes the {@link SystemHub} and starts up the game systems using the provided startup method.
     *
     * @param startUpMethod A {@link Runnable} that defines the startup logic for the game systems.
     */
    public SystemStartUp(Runnable startUpMethod) {
        SystemHub.getInstance();
        startUpSystems(startUpMethod);
    }

    /**
     * Executes the provided startup method to initialize game systems.
     *
     * @param startUpMethod A {@link Runnable} that contains the startup logic for game systems.
     */
    public void startUpSystems(Runnable startUpMethod) {
        startUpMethod.run();
    }

    /**
     * Starts up the {@link SceneManagementSystem} and registers it in the {@link SystemHub} with the highest priority.
     * <br>
     * The priority value of {@code 9999} ensures that this system is executed last.
     */
    public void startUpSceneManagementSystem() {
        SystemHub.getInstance().addSystem(SceneManagementSystem.class, new SceneManagementSystem(), 9999);
    }
}
