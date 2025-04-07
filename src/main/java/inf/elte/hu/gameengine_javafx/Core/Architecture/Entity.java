package inf.elte.hu.gameengine_javafx.Core.Architecture;

import inf.elte.hu.gameengine_javafx.Components.Default.ParentComponent;
import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.HitBoxComponents.HitBoxComponent;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Core.EntityManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Base class for every Entity.
 * <br>
 * Entities store Components and make the connection between the Components' data and the Systems.
 */
public abstract class Entity {
    //Static variable holding the ID that'll be assigned to the next generated Entity.
    private static int nextId = 0;
    private final int id;
    private final Map<Class<? extends Component>, Component> components = new HashMap<>();

    /**
     * Super constructor for every entity.
     */
    public Entity() {
        this.id = ++nextId;
        this.addComponent(new ParentComponent());
        this.addComponent(new PositionComponent(this));
    }

    /**
     * Puts a Component into the Entity's Component HashMap.
     *
     * @param component
     * @param <T>
     */
    public <T extends Component> void addComponent(T component) {
        components.put(component.getClass(), component);
        EntityHub.getInstance().getComponentCache().computeIfAbsent(component.getClass(), k -> new HashSet<>()).add(this.getId());
    }


    /**
     * @param componentType
     * @param <T>
     * @return The Entity's Component associated with the Class in the parameter.
     */
    public <T extends Component> T getComponent(Class<T> componentType) {
        Component component = components.get(componentType);
        return component != null ? componentType.cast(component) : null;
    }


    /**
     * Removes the Entity's Component associated with the Class in the parameter.
     *
     * @param componentType
     * @param <T>
     */
    public <T extends Component> void removeComponentsByType(Class<T> componentType) {
       if(components.entrySet().removeIf(entry -> componentType.isAssignableFrom(entry.getKey()))) {
           if (EntityHub.getInstance().getComponentCache().get(componentType) != null) {
               EntityHub.getInstance().getComponentCache().get(componentType).removeIf(integer -> integer == this.id);
           }
       }
    }

    /**
     * @return The ID of the Entity.
     */
    public int getId() {
        return id;
    }

    /**
     * Prints every Component's simple name, held by this Entity, onto the Console.
     */
    public void showComponents() {
        for (Component component : components.values()) {
            System.out.println(component.getClass().getSimpleName());
        }
    }

    /**
     * @return A Map of this Entity's Components.
     */
    public Map<Class<? extends Component>, Component> getAllComponents() {
        return components;
    }

    /**
     * Adds this Entity to their respective Entity Manager.
     *
     * @param <T>
     */
    @SuppressWarnings("unchecked")
    public <T extends Entity> void addToManager() {
        Class<T> entityClass = (Class<T>) this.getClass();
        EntityManager<T> manager = EntityHub.getInstance().getEntityManager(entityClass);

        if (manager == null) {
            manager = new EntityManager<>();
            EntityHub.getInstance().addEntityManager(entityClass, manager);
        }

        manager.register((T) this);
    }
}
