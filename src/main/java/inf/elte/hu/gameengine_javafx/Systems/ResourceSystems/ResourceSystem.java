package inf.elte.hu.gameengine_javafx.Systems.ResourceSystems;

import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.ResourceHub;
import inf.elte.hu.gameengine_javafx.Core.ResourceManager;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The ResourceSystem is responsible for managing and updating resources within the game engine.
 * It handles the cleaning of resources that have not been accessed for a specified period.
 */
public class ResourceSystem extends GameSystem {

    /**
     * Starts the ResourceSystem by activating it.
     * This method is called when the system is initialized.
     */
    @Override
    public void start() {
        this.active = true;
    }

    /**
     * Updates the ResourceSystem by checking all resource managers and removing resources
     * that have not been accessed in the last 1000 milliseconds.
     * This method is called in every game loop iteration.
     */
    @Override
    public void update() {
        Map<Class<?>, ResourceManager<?>> resourceManagers = ResourceHub.getInstance().getAllResourceManagers();

        long threshold = System.currentTimeMillis() - 10000; // Define threshold time of 10000ms

        // Iterate over all resource managers and clean up old resources
        for (ResourceManager<?> resourceManager : resourceManagers.values()) {
            synchronized (resourceManager) {
                // Take a snapshot of the current resources
                Map<String, ?> resourcesSnapshot = new ConcurrentHashMap<>(resourceManager.getResources());
                Iterator<? extends Map.Entry<String, ?>> iterator = resourcesSnapshot.entrySet().iterator();

                // Remove resources that have not been accessed for over the threshold time
                while (iterator.hasNext()) {
                    Map.Entry<String, ?> resourceEntry = iterator.next();
                    String resourceKey = resourceEntry.getKey();
                    Long lastAccessed = resourceManager.getLastAccessed(resourceKey);

                    if (lastAccessed != null && lastAccessed < threshold) {
                        iterator.remove(); // Remove the resource if it hasn't been accessed in time
                    }
                }
            }
        }
    }
}
