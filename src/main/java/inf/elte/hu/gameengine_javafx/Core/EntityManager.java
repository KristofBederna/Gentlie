package inf.elte.hu.gameengine_javafx.Core;

import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * The {@code EntityManager} class is responsible for managing a collection of entities of a specific type.
 * It allows for registering, retrieving, unloading, and tracking entities within the game engine.
 * It also manages the time of last access for each entity, helping with entity lifecycle management.
 *
 * @param <T> the type of entity managed by this {@code EntityManager}
 */
public class EntityManager<T extends Entity> {
    private final Map<Integer, T> entities;

    /**
     * Creates a new {@code EntityManager} to manage entities.
     */
    public EntityManager() {
        this.entities = new HashMap<>();
    }

    /**
     * Retrieves an entity by its ID.
     *
     * @param id the ID of the entity to retrieve
     * @return the entity associated with the specified ID, or {@code null} if the entity is not registered
     */
    public T get(Integer id) {
        if (entities.containsKey(id)) {
            return entities.get(id);
        } else {
            System.err.println("Entity not registered: " + id);
            return null;
        }
    }

    /**
     * Registers a new entity with the {@code EntityManager}.
     *
     * @param entity the entity to register
     */
    public void register(T entity) {
        this.entities.put(entity.getId(), entity);
        for (Class<? extends Component> componentClass : entity.getAllComponents().keySet()) {
            EntityHub.getInstance().getComponentCache()
                    .computeIfAbsent(componentClass, k -> new HashSet<>() {
                    })
                    .add(entity.getId());
        }
        EntityHub.getInstance().refreshEntitiesList();
    }


    /**
     * Unloads an entity by its ID, removing it from the manager.
     *
     * @param id the ID of the entity to unload
     */
    public void unload(Integer id) {
        entities.remove(id);
        EntityHub.getInstance().refreshEntitiesList();
    }

    /**
     * Unloads all entities managed by the {@code EntityManager}.
     */
    public void unloadAll() {
        entities.clear();
        EntityHub.getInstance().refreshEntitiesList();
    }

    /**
     * Returns a map of all entities managed by the {@code EntityManager}, where the key is the entity's ID.
     *
     * @return a map of all entities, with entity IDs as keys
     */
    public Map<Integer, T> getEntities() {
        return entities;
    }

    /**
     * Registers a list of entities with the {@code EntityManager}.
     *
     * @param list the list of entities to register
     */
    public void registerAll(List<T> list) {
        for (T entity : list) {
            register(entity);
        }
    }
}
