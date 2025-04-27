package inf.elte.hu.gameengine_javafx.Maths.Geometry;

import inf.elte.hu.gameengine_javafx.Entities.CameraEntity;
import inf.elte.hu.gameengine_javafx.Misc.Configs.DisplayConfig;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Point {
    private double x, y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void translate(double x, double y) {
        this.x += x;
        this.y += y;
    }

    public double distanceTo(Point other) {
        double dx = this.x - other.getX();
        double dy = this.y - other.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    public void render(GraphicsContext gc, double radius, Color color) {
        gc.setStroke(color);

        double x = CameraEntity.getRenderX(this.getX());
        double y = CameraEntity.getRenderY(this.getY());

        x *= DisplayConfig.relativeWidthRatio;
        y *= DisplayConfig.relativeHeightRatio;
        radius *= Math.min(DisplayConfig.relativeWidthRatio, DisplayConfig.relativeHeightRatio);

        gc.strokeOval(x - radius, y - radius, radius * 2, radius * 2);
    }

    public void renderFill(GraphicsContext gc, double radius, Color color) {
        gc.setFill(color);
        double x = CameraEntity.getRenderX(this.getX());
        double y = CameraEntity.getRenderY(this.getY());

        x *= DisplayConfig.relativeWidthRatio;
        y *= DisplayConfig.relativeHeightRatio;
        radius *= Math.min(DisplayConfig.relativeWidthRatio, DisplayConfig.relativeHeightRatio);

        gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);
    }

    public void renderFillWithStroke(GraphicsContext gc, double radius, Color color, Color strokeColor, double outerStrokeWidth) {
        gc.setFill(color);
        double x = CameraEntity.getRenderX(this.getX());
        double y = CameraEntity.getRenderY(this.getY());

        x *= DisplayConfig.relativeWidthRatio;
        y *= DisplayConfig.relativeHeightRatio;
        radius *= Math.min(DisplayConfig.relativeWidthRatio, DisplayConfig.relativeHeightRatio);

        gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);

        gc.setStroke(strokeColor);
        gc.setLineWidth(outerStrokeWidth);
        gc.strokeOval(x - radius, y - radius, radius * 2, radius * 2);
    }

    /**
     * Compares the coordinates of this point with another, allowing a fixed delta of 10 units.
     *
     * @param other the other point
     * @return true if the points are within 10 units, false otherwise
     */
    public boolean compareCoordinates(Point other) {
        if (other == null) {
            return false;
        }
        return Math.abs(this.getX() - other.getX()) < 10 && Math.abs(this.getY() - other.getY()) < 10;
    }

    /**
     * Compares the coordinates of this point with another, allowing a custom delta.
     *
     * @param other the other point
     * @param delta the maximum allowed difference
     * @return true if the points are within the specified delta, false otherwise
     */
    public boolean compareCoordinates(Point other, double delta) {
        if (other == null) {
            return false;
        }
        return Math.abs(this.getX() - other.getX()) < delta && Math.abs(this.getY() - other.getY()) < delta;
    }

    public void setCoordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setCoordinates(Point other) {
        this.x = other.getX();
        this.y = other.getY();
    }

    public Point getCoordinates() {
        return new Point(x, y);
    }
}
