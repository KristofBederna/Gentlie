package inf.elte.hu.gameengine_javafx.Core;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * The {@code ResourceManager} class is responsible for managing resources of a specific type,
 * such as images, sounds, or other assets, in the game engine. It uses a key-value mapping
 * to load, store, and retrieve resources on demand.
 *
 * @param <T> the type of resources managed by this class
 */
public class ResourceManager<T> {
    private final Map<String, T> resources;
    private final Map<String, Long> lastAccessed;
    private final Function<String, T> loader;

    /**
     * Constructs a new {@code ResourceManager} with a specified loader function.
     * The loader function is used to load a resource by its key when it's not already cached.
     *
     * @param loader a function that loads a resource by its key
     */
    public ResourceManager(Function<String, T> loader) {
        this.loader = loader;
        this.resources = new HashMap<>();
        this.lastAccessed = new HashMap<>();
    }

    /**
     * Retrieves a resource by its key. If the resource is already loaded, it returns it from the cache.
     * If the resource is not loaded, it uses the loader function to load the resource.
     *
     * @param key the key identifying the resource
     * @return the resource if found, or {@code null} if loading the resource failed
     */
    public T get(String key) {
        if (contains(key)) {
            lastAccessed.put(key, System.currentTimeMillis());
            return resources.get(key);
        }

        return loadThenGet(key);
    }

    /**
     * Loads a resource if it hadn't been loaded before, then returns the resource.
     *
     * @param key unique identifier of the resource.
     * @return The loaded resource
     */
    private T loadThenGet(String key) {
        T resource = loader.apply(key);

        if (resource == null) {
            throw new RuntimeException("Resource not found: " + key);
        }

        resources.put(key, resource);
        lastAccessed.put(key, System.currentTimeMillis());
        return resource;
    }

    /**
     * Returns whether a resource manager is containing a certain key.
     *
     * @param key unique identifier of the resource.
     * @return {@code True} if contains, {@code False} if not.
     */
    private boolean contains(String key) {
        if (resources.containsKey(key)) {
            lastAccessed.put(key, System.currentTimeMillis());
            return true;
        }
        return false;
    }

    /**
     * Unloads a resource by its key, removing it from the cache.
     *
     * @param key the key identifying the resource to unload
     */
    public void unload(String key) {
        resources.remove(key);
        lastAccessed.remove(key);
    }

    /**
     * Unloads all resources, clearing the cache of all stored resources.
     */
    public void unloadAll() {
        resources.clear();
        lastAccessed.clear();
    }

    /**
     * Returns a map of all loaded resources.
     *
     * @return a map of all loaded resources, with the key as the map key
     */
    public Map<String, T> getResources() {
        return resources;
    }

    /**
     * Returns the timestamp of when a resource was last accessed.
     *
     * @param key the key identifying the resource
     * @return the timestamp of the last access, or {@code null} if the resource has never been accessed
     */
    public Long getLastAccessed(String key) {
        return lastAccessed.getOrDefault(key, null);
    }
}
