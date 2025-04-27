package inf.elte.hu.gameengine_javafx.Misc;

import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IgnoreCollisions {
    private static final IgnoreCollisions instance = new IgnoreCollisions();

    Map<Class<? extends Entity>, ArrayList<Class<? extends Entity>>> collisionRules = new HashMap<>();


    private IgnoreCollisions() {
    }

    public static IgnoreCollisions getInstance() {
        return instance;
    }

    /**
     * @return The map holding the rules of collisions. Can be used to add or remove rules.
     */
    public Map<Class<? extends Entity>, ArrayList<Class<? extends Entity>>> getCollisionRules() {
        return collisionRules;
    }

    /**
     * Checks whether entity a and entity b can make a collision with each other.
     *
     * @return {@code True} if they can not collide, {@code False} if they can collide.
     */
    public static boolean shouldIgnoreCollision(Entity a, Entity b) {
        IgnoreCollisions ignore = IgnoreCollisions.getInstance();
        Class<Entity> aClass = (Class<Entity>) a.getClass();
        Class<Entity> bClass = (Class<Entity>) b.getClass();

        ArrayList<Class<? extends Entity>> ignoredForA = ignore.getCollisionRules().get(aClass);
        if (ignoredForA != null && ignoredForA.contains(bClass)) {
            return true;
        }

        ArrayList<Class<? extends Entity>> ignoredForB = ignore.getCollisionRules().get(bClass);
        return ignoredForB != null && ignoredForB.contains(aClass);
    }

}
