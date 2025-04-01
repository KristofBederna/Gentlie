package inf.elte.hu.gameengine_javafx.Maths.Geometry;

import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Entities.CameraEntity;
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
        CameraEntity cameraEntity = CameraEntity.getInstance();

        gc.setStroke(color);
        double x = this.getX() - cameraEntity.getComponent(PositionComponent.class).getGlobalX();
        double y = this.getY() - cameraEntity.getComponent(PositionComponent.class).getGlobalY();

        gc.strokeOval(x - radius, y - radius, radius * 2, radius * 2);
    }

    public void renderFill(GraphicsContext gc, double radius, Color color) {
        CameraEntity cameraEntity = CameraEntity.getInstance();

        gc.setFill(color);
        double x = this.getX() - cameraEntity.getComponent(PositionComponent.class).getGlobalX();
        double y = this.getY() - cameraEntity.getComponent(PositionComponent.class).getGlobalY();

        gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);
    }

    public boolean compareCoordinates(Point other) {
        return Math.abs(this.getX() - other.getX()) < 10 && Math.abs(this.getY() - other.getY()) < 10;
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
