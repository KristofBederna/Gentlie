package inf.elte.hu.gameengine_javafx.Maths.Geometry;

import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Entities.CameraEntity;
import inf.elte.hu.gameengine_javafx.Misc.Configs.DisplayConfig;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class Triangle extends Shape {

    public Triangle(Point a, Point b, Point c) {
        this.points = new ArrayList<>(List.of(a, b, c));
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
        double deltaX = newPoint.getX() - points.getFirst().getX();
        double deltaY = newPoint.getY() - points.getFirst().getY();

        for (Point p : points) {
            p.setX(p.getX() + deltaX);
            p.setY(p.getY() + deltaY);
        }
        updateEdges();
    }

    public void render(GraphicsContext gc, Color color) {
        double[] x = new double[3];
        double[] y = new double[3];

        CameraEntity camera = CameraEntity.getInstance();
        PositionComponent camPos = camera.getComponent(PositionComponent.class);

        for (int i = 0; i < 3; i++) {
            x[i] = points.get(i).getX() - camPos.getGlobalX();
            y[i] = points.get(i).getY() - camPos.getGlobalY();
        }

        for (int i = 0; i < 3; i++) {
            x[i] *= DisplayConfig.relativeWidthRatio;
            y[i] *= DisplayConfig.relativeHeightRatio;
        }

        gc.setStroke(color);
        gc.setLineWidth(2);
        gc.strokePolygon(x, y, 3);
    }

    public void render(GraphicsContext gc, Color color, double strokeWidth) {
        double[] x = new double[3];
        double[] y = new double[3];

        for (int i = 0; i < 3; i++) {
            x[i] = CameraEntity.getRenderX(points.get(i).getX()) * DisplayConfig.relativeWidthRatio;
            y[i] = CameraEntity.getRenderY(points.get(i).getY()) * DisplayConfig.relativeHeightRatio;
        }

        gc.setStroke(color);
        gc.setLineWidth(strokeWidth);
        gc.strokePolygon(x, y, 3);
    }

    public void renderFill(GraphicsContext gc, Color color) {
        double[] x = new double[3];
        double[] y = new double[3];

        for (int i = 0; i < 3; i++) {
            x[i] = CameraEntity.getRenderX(points.get(i).getX()) * DisplayConfig.relativeWidthRatio;
            y[i] = CameraEntity.getRenderY(points.get(i).getY()) * DisplayConfig.relativeHeightRatio;
        }

        gc.setFill(color);
        gc.fillPolygon(x, y, 3);
    }

    @Override
    public void renderFillWithStroke(GraphicsContext gc, Color color, Color stroke, double outerStrokeWidth) {
        double[] x = new double[3];
        double[] y = new double[3];

        for (int i = 0; i < 3; i++) {
            x[i] = CameraEntity.getRenderX(points.get(i).getX()) * DisplayConfig.relativeWidthRatio;
            y[i] = CameraEntity.getRenderY(points.get(i).getY()) * DisplayConfig.relativeHeightRatio;
        }

        gc.setFill(color);
        gc.fillPolygon(x, y, 3);

        gc.setStroke(stroke);
        gc.setLineWidth(outerStrokeWidth);
        gc.strokePolygon(x, y, 3);
    }
}
