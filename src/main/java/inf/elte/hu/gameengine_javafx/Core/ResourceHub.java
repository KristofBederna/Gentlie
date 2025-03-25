package inf.elte.hu.gameengine_javafx.Core;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@code ResourceHub} class manages different types of resources within the game engine.
 * It allows for adding, retrieving, and removing {@code ResourceManager} instances, which are responsible
 * for managing specific types of resources. The {@code ResourceHub} is a singleton that ensures centralized
 * access to all resources within the game engine.
 */
public class ResourceHub {
    private static ResourceHub instance;
    private final Map<Class<?>, ResourceManager<?>> resourceManagers;

    /**
     * Private constructor for initializing the {@code ResourceHub}.
     */
    private ResourceHub() {
        resourceManagers = new HashMap<>();
    }

    /**
     * Returns the singleton instance of the {@code ResourceHub}.
     *
     * @return the {@code ResourceHub} instance
     */
    public static synchronized ResourceHub getInstance() {
        if (instance == null) {
            instance = new ResourceHub();
        }
        return instance;
    }

    public static void resetInstance() {
        instance = null;
    }

    /**
     * Adds a {@code ResourceManager} to the {@code ResourceHub}.
     *
     * @param classType       the class type of the resource managed by the {@code ResourceManager}
     * @param resourceManager the {@code ResourceManager} instance to add
     * @param <T>             the type of resource managed by the {@code ResourceManager}
     */
    public <T> void addResourceManager(Class<T> classType, ResourceManager<T> resourceManager) {
        resourceManagers.put(classType, resourceManager);
    }

    /**
     * Retrieves the {@code ResourceManager} for a given resource type.
     *
     * @param classType the class type of the resource managed by the {@code ResourceManager}
     * @param <T>       the type of resource managed by the {@code ResourceManager}
     * @return the {@code ResourceManager} for the specified resource type, or {@code null} if not found
     */
    @SuppressWarnings("unchecked")
    public <T> ResourceManager<T> getResourceManager(Class<T> classType) {
        return (ResourceManager<T>) resourceManagers.get(classType);
    }

    /**
     * Removes the {@code ResourceManager} for a given resource type.
     *
     * @param type the class type of the resource managed by the {@code ResourceManager}
     */
    public void removeResourceManager(Class<?> type) {
        resourceManagers.remove(type);
    }

    /**
     * Unloads all resources by calling {@code unloadAll} on all registered {@code ResourceManager} instances.
     */
    public void unloadAll() {
        resourceManagers.values().forEach(ResourceManager::unloadAll);
    }

    /**
     * Returns a map of all registered {@code ResourceManager} instances, with the class type as the key.
     *
     * @return a map of all {@code ResourceManager} instances
     */
    public Map<Class<?>, ResourceManager<?>> getAllResourceManagers() {
        return resourceManagers;
    }

    /**
     * Clears all resources and removes all registered {@code ResourceManager} instances.
     */
    public void clearResources() {
        resourceManagers.clear();
    }
}
