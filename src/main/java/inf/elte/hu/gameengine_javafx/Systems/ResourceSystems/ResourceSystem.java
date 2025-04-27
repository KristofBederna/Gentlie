package inf.elte.hu.gameengine_javafx.Systems.ResourceSystems;

import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.ResourceHub;
import inf.elte.hu.gameengine_javafx.Core.ResourceManager;
import inf.elte.hu.gameengine_javafx.Misc.Configs.ResourceConfig;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;

/**
 * The ResourceSystem is responsible for managing and updating resources within the game engine.
 * It handles the cleaning of resources that have not been accessed for a specified period.
 */
public class ResourceSystem extends GameSystem {

    /**
     * Starts the ResourceSystem by activating it.
     */
    @Override
    public void start() {
        this.active = true;
    }

    /**
     * Updates the ResourceSystem by checking all resource managers and removing resources
     * that have not been accessed in the last ResourceConfig.resourceUnloadThresholdTime milliseconds.
     */
    @Override
    public void update() {
        Map<Class<?>, ResourceManager<?>> resourceManagers = ResourceHub.getInstance().getAllResourceManagers();

        long threshold = System.currentTimeMillis() - ResourceConfig.resourceUnloadThresholdTime;

        List<String> resourcesToRemove = new ArrayList<>();
        for (ResourceManager<?> resourceManager : resourceManagers.values()) {
            resourcesToRemove.clear();
            try {
                for (Map.Entry<String, ?> entry : resourceManager.getResources().entrySet()) {
                    String key = entry.getKey();
                    Long lastAccessed = resourceManager.getLastAccessed(key);

                    if (lastAccessed != null && lastAccessed < threshold) {
                        resourcesToRemove.add(key);
                    }
                }
                for (String key : resourcesToRemove) {
                    resourceManager.unload(key);
                }
            } catch (ConcurrentModificationException e) {
                System.err.println("Concurrent modification detected in ResourceSystem. Skipping this update cycle.");
            }
        }
    }

}
