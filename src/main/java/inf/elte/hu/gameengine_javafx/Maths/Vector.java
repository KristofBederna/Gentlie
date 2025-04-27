package inf.elte.hu.gameengine_javafx.Maths;

import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.CentralMassComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;

/**
 * A class representing a 2D vector with horizontal (dx) and vertical (dy) components.
 * <p>
 * The {@code Vector} class provides functionality for manipulating vectors in 2D space.
 * It includes methods for setting and getting the vector's components, moving towards a
 * specific point, and copying a vector.
 * </p>
 */
public class Vector {
    private double dx;
    private double dy;

    /**
     * Constructs a {@code Vector} with the specified horizontal and vertical components.
     *
     * @param dx the horizontal component of the vector
     * @param dy the vertical component of the vector
     */
    public Vector(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * Returns the horizontal component of the vector (dx).
     *
     * @return the horizontal component of the vector
     */
    public double getDx() {
        return dx;
    }

    /**
     * Returns the vertical component of the vector (dy).
     *
     * @return the vertical component of the vector
     */
    public double getDy() {
        return dy;
    }

    /**
     * Returns a new {@code Vector} object that is a copy of this vector.
     *
     * @return a new {@code Vector} with the same components as this vector
     */
    public Vector getVector() {
        return new Vector(dx, dy);
    }

    /**
     * Sets the horizontal component of the vector (dx).
     *
     * @param dx the new horizontal component of the vector
     */
    public void setDx(double dx) {
        this.dx = dx;
    }

    /**
     * Sets the vertical component of the vector (dy).
     *
     * @param dy the new vertical component of the vector
     */
    public void setDy(double dy) {
        this.dy = dy;
    }

    /**
     * Sets both the horizontal and vertical components of the vector from another vector.
     *
     * @param vector the vector whose components will be used to set this vector's components
     */
    public void setVector(Vector vector) {
        this.dx = vector.getDx();
        this.dy = vector.getDy();
    }

    /**
     * Moves the entity towards the specified point. The vector is updated to represent the direction
     * the entity should move in order to reach the point.
     *
     * @param node   the point the entity is moving towards
     * @param entity the entity that is moving
     */
    public void moveTowards(Point node, Entity entity) {
        double currentX = entity.getComponent(CentralMassComponent.class).getCentralX();
        double currentY = entity.getComponent(CentralMassComponent.class).getCentralY();

        double deltaX = node.getX() - currentX;
        double deltaY = node.getY() - currentY;
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        if (distance > 0) {
            dx = deltaX / distance;
            dy = deltaY / distance;
        }
    }

    /**
     * @return {@code True} if both dx and dy are 0, {@code False} otherwise.
     */
    public boolean isZero() {
        return dx == 0 && dy == 0;
    }

    /**
     * @return The magnitude of the vector.
     */
    public double magnitude() {
        return Math.sqrt(dx * dx + dy * dy);
    }
}
