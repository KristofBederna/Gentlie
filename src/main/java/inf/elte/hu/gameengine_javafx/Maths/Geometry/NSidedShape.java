package inf.elte.hu.gameengine_javafx.Maths.Geometry;


import inf.elte.hu.gameengine_javafx.Entities.CameraEntity;
import inf.elte.hu.gameengine_javafx.Misc.Configs.DisplayConfig;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class NSidedShape extends Shape {
    private Point center;
    private double radius;
    private int segments;
    private double rotation = 0;

    public NSidedShape(Point center, double radius, int segments) {
        this.center = center;
        this.radius = radius;
        this.segments = segments;
        this.points = new ArrayList<>(List.of(center));
        generateApproximation();
    }

    public NSidedShape(NSidedShape hitBox) {
        this.center = hitBox.center;
        this.radius = hitBox.radius;
        this.segments = hitBox.segments;
        this.rotation = hitBox.rotation;
        this.points = new ArrayList<>(hitBox.points);
    }

    public NSidedShape(Point center, double sideLength, int segments, int rotation) {
        this.center = center;
        this.radius = sideLength / 2;
        this.segments = segments;
        this.points = new ArrayList<>();
        generateApproximation();
        this.rotation = rotation;
        rotate(rotation);
    }


    private void generateApproximation() {
        points.clear();

        for (int i = 0; i < this.segments; i++) {
            createPoint(i);
        }
        updateEdges();
        rotate(rotation);
    }

    private void createPoint(int i) {
        double angle = 2 * Math.PI * i / this.segments;
        double x = center.getX() + radius * Math.cos(angle);
        double y = center.getY() + radius * Math.sin(angle);
        points.add(new Point(x, y));
    }

    public void rotate(double degrees) {
        double angle = Math.toRadians(degrees);

        for (int i = 0; i < points.size(); i++) {
            rotatePoint(i, angle);
        }
        updateEdges();
    }

    private void rotatePoint(int i, double angle) {
        Point p = points.get(i);

        double x = p.getX() - center.getX();
        double y = p.getY() - center.getY();

        double rotatedX = x * Math.cos(angle) - y * Math.sin(angle);
        double rotatedY = x * Math.sin(angle) + y * Math.cos(angle);

        points.set(i, new Point(rotatedX + center.getX(), rotatedY + center.getY()));
    }


    public void updateEdges() {
        this.edges = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            Point p1 = points.get(i);
            Point p2 = points.get((i + 1) % points.size());
            edges.add(new Edge(p1, p2));
        }
    }


    public void render(GraphicsContext gc, Color color) {
        gc.setStroke(color);
        gc.setLineWidth(2);

        renderShape(gc);
    }

    public void render(GraphicsContext gc, Color color, double strokeWidth) {
        gc.setStroke(color);
        gc.setLineWidth(strokeWidth);

        renderShape(gc);
    }

    private void renderShape(GraphicsContext gc) {
        updateEdgesForShape();

        Point prev = points.getLast();
        for (Point p : points) {
            double x1 = CameraEntity.getRenderX(prev.getX());
            double y1 = CameraEntity.getRenderY(prev.getY());
            double x2 = CameraEntity.getRenderX(p.getX());
            double y2 = CameraEntity.getRenderY(p.getY());

            x1 *= DisplayConfig.relativeWidthRatio;
            y1 *= DisplayConfig.relativeHeightRatio;
            x2 *= DisplayConfig.relativeWidthRatio;
            y2 *= DisplayConfig.relativeHeightRatio;

            gc.strokeLine(x1, y1, x2, y2);
            prev = p;
        }
    }


    public void renderFill(GraphicsContext gc, Color color) {
        updateEdgesForShape();

        double[] xPoints = new double[points.size()];
        double[] yPoints = new double[points.size()];

        for (int i = 0; i < points.size(); i++) {
            xPoints[i] = CameraEntity.getRenderX(points.get(i).getX()) * DisplayConfig.relativeWidthRatio;
            yPoints[i] = CameraEntity.getRenderY(points.get(i).getY()) * DisplayConfig.relativeHeightRatio;
        }

        gc.setFill(color);
        gc.fillPolygon(xPoints, yPoints, points.size());
    }

    private void updateEdgesForShape() {
        if (points.isEmpty()) {
            generateApproximation();
            updateEdges();
        }
    }

    public void renderFillWithStroke(GraphicsContext gc, Color fillColor, Color strokeColor, double outerStrokeWidth) {
        updateEdgesForShape();

        double[] xPoints = new double[points.size()];
        double[] yPoints = new double[points.size()];

        for (int i = 0; i < points.size(); i++) {
            xPoints[i] = CameraEntity.getRenderX(points.get(i).getX()) * DisplayConfig.relativeWidthRatio;
            yPoints[i] = CameraEntity.getRenderY(points.get(i).getY()) * DisplayConfig.relativeHeightRatio;
        }

        gc.setFill(fillColor);
        gc.fillPolygon(xPoints, yPoints, points.size());

        gc.setStroke(strokeColor);
        gc.setLineWidth(outerStrokeWidth);
        gc.strokePolygon(xPoints, yPoints, points.size());
    }




    public void moveTo(Point newPoint) {
        center = newPoint;
        generateApproximation();
        updateEdges();
    }

    public void translate(double x, double y) {
        center.translate(x, y);
        generateApproximation();
        updateEdges();
    }

    public Point getCenter() {
        return center;
    }
}

