package inf.elte.hu.gameengine_javafx.Core;

import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.SceneManagementSystem;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.SoundSystems.BackgroundMusicSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The {@code SystemHub} class is responsible for managing and organizing all the systems in the game engine.
 * It allows adding, removing, retrieving, and ordering systems based on their priorities.
 * It also facilitates the orderly shutdown of systems when the game engine is shutting down.
 */
public class SystemHub {
    private static SystemHub instance;
    private final Map<Class<? extends GameSystem>, Integer> systemPriorities;
    private final Map<Integer, GameSystem> systems;
    private boolean isShuttingDown = false;

    /**
     * Private constructor to ensure that the SystemHub is a singleton.
     */
    private SystemHub() {
        systemPriorities = new ConcurrentHashMap<>();
        systems = new ConcurrentHashMap<>();
    }

    /**
     * Gets the singleton instance of the {@code SystemHub}.
     *
     * @return the singleton instance of the SystemHub
     */
    public static SystemHub getInstance() {
        if (instance == null) {
            instance = new SystemHub();
        }
        return instance;
    }

    /**
     * Resets the instance of the singleton.
     */
    public static void resetInstance() {
        instance = new SystemHub();
    }

    /**
     * Adds a system to the SystemHub with a specified priority.
     * The system will be stored in a TreeMap, where it is ordered by priority.
     *
     * @param systemClass the class type of the system
     * @param system      the system instance to add
     * @param priority    the priority level of the system (lower values are higher priority)
     * @param <T>         the type of the system
     */
    public <T extends GameSystem> void addSystem(Class<T> systemClass, T system, int priority) {
        systemPriorities.put(systemClass, priority);
        systems.put(priority, system);
    }

    /**
     * Removes a system from the {@code SystemHub}.
     *
     * @param systemClass the class type of the system to unload
     * @param <T>         the type of the system
     */
    public <T extends GameSystem> void removeSystem(Class<T> systemClass) {
        Integer priority = systemPriorities.remove(systemClass);
        if (priority != null) {
            systems.remove(priority);
        }
    }

    /**
     * Retrieves a system by its class type.
     *
     * @param systemClass the class type of the system to retrieve
     * @param <T>         the type of the system
     * @return the system instance, or {@code null} if the system is not registered
     */
    public <T extends GameSystem> T getSystem(Class<T> systemClass) {
        Integer priority = systemPriorities.get(systemClass);
        return (priority != null) ? systemClass.cast(systems.get(priority)) : null;
    }

    /**
     * Retrieves all systems in priority order, with the highest priority first.
     *
     * @return a list of all systems ordered by priority
     */
    public List<GameSystem> getAllSystemsInPriorityOrder() {
        return new ArrayList<>(systems.values());
    }

    /**
     * Shuts down all systems in the SystemHub, aborting each one in the reverse priority order,
     * The systems are cleared from the system hub after shutdown.
     */
    public void shutDownSystems() {
        if (isShuttingDown) {
            return;
        }

        isShuttingDown = true;
        try {
            abortIfNotKept();
        } finally {
            isShuttingDown = false;
        }
    }

    /**
     * Aborts every system that is not kept between scenes.
     */
    private void abortIfNotKept() {
        SceneManagementSystem sceneManagementSystem = getSystem(SceneManagementSystem.class);
        BackgroundMusicSystem backgroundMusicSystem = getSystem(BackgroundMusicSystem.class);

        abortReversed(sceneManagementSystem, backgroundMusicSystem);
        resetSystems(backgroundMusicSystem, sceneManagementSystem);
        resetSystemPriorities();
    }

    /**
     * Aborts systems in a reversed order, so that they are shut down in a logical order.
     */
    private void abortReversed(SceneManagementSystem sceneManagementSystem, BackgroundMusicSystem backgroundMusicSystem) {
        for (GameSystem system : getAllSystemsInPriorityOrder().reversed()) {
            if (system == sceneManagementSystem || system == backgroundMusicSystem) {
                continue;
            }
            system.abort();
        }
    }

    /**
     * Resets the {@code systems} map then adds the essential systems back to it.
     */
    private void resetSystems(BackgroundMusicSystem backgroundMusicSystem, SceneManagementSystem sceneManagementSystem) {
        systems.clear();
        systems.put(998, backgroundMusicSystem);
        systems.put(999, sceneManagementSystem);
    }

    /**
     * Resets the {@code systemPriorities} map then adds the essential systems back to it.
     */
    private void resetSystemPriorities() {
        systemPriorities.clear();
        systemPriorities.put(BackgroundMusicSystem.class, 998);
        systemPriorities.put(SceneManagementSystem.class, 999);
    }
}
