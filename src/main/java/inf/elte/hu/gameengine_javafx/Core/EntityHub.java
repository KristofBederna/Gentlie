package inf.elte.hu.gameengine_javafx.Core;

import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.DimensionComponent;
import inf.elte.hu.gameengine_javafx.Components.RenderingComponents.ImageComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Entities.CameraEntity;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The {@code EntityHub} class is a singleton responsible for managing entities and their associated
 * entity managers in the game engine.
 * <p>
 * It provides methods for adding, removing, and retrieving entities and entity managers, as well as
 * filtering entities based on specific components or types. It also provides functionality for
 * determining which entities are visible inside the camera's viewport.
 * </p>
 */
public class EntityHub {
    private static EntityHub instance;
    private final Map<Class<?>, EntityManager<?>> entityManagers;
    final Map<Integer, Entity> entities = new HashMap<>();
    private final Map<Class<? extends Component>, Set<Integer>> componentCache = new ConcurrentHashMap<>();

    public static void resetInstance() {
        instance = null;
    }

    public Map<Class<? extends Component>, Set<Integer>> getComponentCache() {
        return componentCache;
    }

    /**
     * Private constructor for initializing the entity hub.
     */
    private EntityHub() {
        entityManagers = new ConcurrentHashMap<>();
    }

    /**
     * Returns the singleton instance of the {@code EntityHub}.
     *
     * @return the singleton instance of {@code EntityHub}
     */
    public static synchronized EntityHub getInstance() {
        if (instance == null) {
            instance = new EntityHub();
        }
        return instance;
    }

    /**
     * Adds an entity manager to the hub for managing entities of a specific type.
     *
     * @param type          the type of entities managed by the entity manager
     * @param entityManager the entity manager to be added
     * @param <T>           the type of entities managed by the entity manager
     */
    public <T extends Entity> void addEntityManager(Class<T> type, EntityManager<T> entityManager) {
        entityManagers.put(type, entityManager);
        refreshEntitiesList();
    }

    /**
     * Retrieves the entity manager for a specific entity type.
     *
     * @param type the type of entities managed by the entity manager
     * @param <T>  the type of entities managed by the entity manager
     * @return the entity manager for the specified type, or {@code null} if not found
     */
    @SuppressWarnings("unchecked")
    public <T extends Entity> EntityManager<T> getEntityManager(Class<T> type) {
        return (EntityManager<T>) entityManagers.get(type);
    }

    /**
     * Removes an entity manager from the hub for a specific entity type.
     *
     * @param type the type of entities whose entity manager is to be removed
     */
    public void removeEntityManager(Class<?> type) {
        entityManagers.remove(type);
    }

    /**
     * Removes all entity managers from the hub.
     */
    public void removeAllEntityManagers() {
        entityManagers.clear();
    }

    /**
     * Unloads all entities managed by the entity managers.
     */
    public void unloadAll() {
        entities.clear();
        entityManagers.clear();
        componentCache.clear();
    }

    /**
     * Returns a map of all entity managers in the hub.
     *
     * @return a map of entity managers
     */
    public Map<Class<?>, EntityManager<?>> getAllEntityManagers() {
        return entityManagers;
    }

    /**
     * Returns a list of all entities currently managed by the hub.
     *
     * @return a list of all entities
     */
    public List<Entity> getAllEntities() {
        return entities.values().stream().toList();
    }

    /**
     * Refreshes the list of entities by retrieving entities from all entity managers.
     */
    public void refreshEntitiesList() {
        synchronized (entities) {
            entities.clear();
            for (EntityManager<?> entityManager : entityManagers.values()) {
                for(Entity entity : entityManager.getEntities().values()) {
                    entities.put(entity.getId(), entity);
                }
            }
        }
    }

    /**
     * Returns a list of entities that are inside the viewport of the specified camera entity.
     *
     * @param cameraEntity the camera entity used to determine the visible area
     * @return a list of entities inside the camera's viewport
     */
    public List<Entity> getEntitiesInsideViewport(CameraEntity cameraEntity) {
        if (cameraEntity == null) return null;

        List<Entity> visibleEntities = new ArrayList<>();
        synchronized (entities) {
            for (Entity entity : entities.values()) {
                PositionComponent position = entity.getComponent(PositionComponent.class);
                if (position == null) continue;

                if (entity.getComponent(ImageComponent.class) == null) {
                    continue;
                }
                if (cameraEntity.isPositionInsideViewport(
                        position.getGlobalX(),
                        position.getGlobalY(),
                        entity.getComponent(DimensionComponent.class).getWidth(),
                        entity.getComponent(DimensionComponent.class).getHeight())) {
                    visibleEntities.add(entity);
                }
            }
        }
        return visibleEntities;
    }

    /**
     * Returns a list of entities that have a specific component.
     *
     * @param type the type of component to check for
     * @return a list of entities that have the specified component
     */
    public List<Entity> getEntitiesWithComponent(Class<? extends Component> type) {
        Set<Integer> entityIds = componentCache.get(type);

        if (entityIds == null || entityIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Entity> entitiesWithComponent = new ArrayList<>(entityIds.size());
        for (Integer id : entityIds) {
            entitiesWithComponent.add(entities.get(id));
        }

        return entitiesWithComponent;
    }



    /**
     * Returns a list of entities that are of a specific type.
     *
     * @param type the type of entities to filter
     * @return a list of entities of the specified type
     */
    public List<Entity> getEntitiesWithType(Class<? extends Entity> type) {
        List<Entity> entitiesWithType = new ArrayList<>();
        synchronized (entities) {
            for (Entity entity : entities.values()) {
                if (type.isInstance(entity)) {
                    entitiesWithType.add(entity);
                }
            }
        }
        return entitiesWithType;
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity.getId());
        for(Set<Integer> componentIds : componentCache.values()) {
            if (componentIds.contains(entity.getId())) {
                componentIds.removeIf(entityId -> entityId == entity.getId());
            }
        }
        getEntityManager(entity.getClass()).unload(entity.getId());
    }
}
