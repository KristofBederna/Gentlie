package inf.elte.hu.gameengine_javafx.Maths.Geometry;

import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Entities.CameraEntity;
import inf.elte.hu.gameengine_javafx.Misc.Config;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;

public class Triangle extends Shape {

    public Triangle(Point a, Point b, Point c) {
        this.points = List.of(a, b, c);
        updateEdges();
    }

    public Triangle(Triangle hitBox) {
        this.points = List.of(hitBox.getA(), hitBox.getB(), hitBox.getC());
        updateEdges();
    }

    public void updateEdges() {
        edges = List.of(
                new Edge(points.get(0), points.get(1)), // ab
                new Edge(points.get(1), points.get(2)), // bc
                new Edge(points.get(2), points.get(0))  // ca
        );
    }

    public Point getA() {
        return points.get(0);
    }

    public void setA(Point a) {
        points.set(0, a);
        updateEdges();
    }

    public Point getB() {
        return points.get(1);
    }

    public void setB(Point b) {
        points.set(1, b);
        updateEdges();
    }

    public Point getC() {
        return points.get(2);
    }

    public void setC(Point c) {
        points.set(2, c);
        updateEdges();
    }

    public void translate(double x, double y) {
        for (Point p : points) {
            p.translate(x, y);
        }
        updateEdges();
    }

    public void moveTo(Point newPoint) {
        double deltaX = newPoint.getX() - points.get(0).getX();
        double deltaY = newPoint.getY() - points.get(0).getY();

        for (Point p : points) {
            p.setX(p.getX() + deltaX);
            p.setY(p.getY() + deltaY);
        }
        updateEdges();
    }

    public void render(GraphicsContext gc, Color color) {
        double[] x = new double[3];
        double[] y = new double[3];

        applyCameraOffset(x, y);

        // Apply scaling
        for (int i = 0; i < 3; i++) {
            x[i] *= Config.relativeWidthRatio;
            y[i] *= Config.relativeHeightRatio;
        }

        gc.setStroke(color);
        gc.setLineWidth(2);
        gc.strokePolygon(x, y, 3); // Draws all sides of the triangle
    }

    public void renderFill(GraphicsContext gc, Color color) {
        double[] x = new double[3];
        double[] y = new double[3];

        applyCameraOffset(x, y);

        // Apply scaling
        for (int i = 0; i < 3; i++) {
            x[i] *= Config.relativeWidthRatio;
            y[i] *= Config.relativeHeightRatio;
        }

        gc.setFill(color);
        gc.fillPolygon(x, y, 3); // Fills the triangle

        gc.setStroke(color);
        gc.setLineWidth(2);
        gc.strokePolygon(x, y, 3); // Draws all sides of the triangle
    }

    private void applyCameraOffset(double[] x, double[] y) {
        CameraEntity camera = CameraEntity.getInstance();
        PositionComponent camPos = camera.getComponent(PositionComponent.class);

        for (int i = 0; i < 3; i++) {
            x[i] = points.get(i).getX() - camPos.getGlobalX();
            y[i] = points.get(i).getY() - camPos.getGlobalY();
        }
    }
}
