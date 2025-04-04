package inf.elte.hu.gameengine_javafx.Maths.Geometry;


import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Entities.CameraEntity;
import inf.elte.hu.gameengine_javafx.Misc.Config;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class ComplexShape extends Shape {
    public ComplexShape() {
        this.points = new ArrayList<>();
        updateEdges();
    }

    public ComplexShape(List<Point> points) {
        this.points = new ArrayList<>();
        this.points.addAll(points);
        updateEdges();
    }

    public ComplexShape(ComplexShape hitBox) {
        this.points = new ArrayList<>();
        this.points.addAll(hitBox.points);
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
        CameraEntity cameraEntity = CameraEntity.getInstance();

        gc.setStroke(color);
        gc.setLineWidth(2);

        if (points.isEmpty()) {
            updateEdges();
        }

        Point prev = points.getLast();
        for (Point p : points) {
            double x1 = prev.getX() - cameraEntity.getComponent(PositionComponent.class).getGlobalX();
            double y1 = prev.getY() - cameraEntity.getComponent(PositionComponent.class).getGlobalY();
            double x2 = p.getX() - cameraEntity.getComponent(PositionComponent.class).getGlobalX();
            double y2 = p.getY() - cameraEntity.getComponent(PositionComponent.class).getGlobalY();

            gc.strokeLine(x1* Config.relativeWidthRatio, y1*Config.relativeHeightRatio, x2*Config.relativeWidthRatio, y2*Config.relativeHeightRatio);
            prev = p;
        }
    }

    public void renderFill(GraphicsContext gc, Color color) {
        CameraEntity cameraEntity = CameraEntity.getInstance();

        if (points.isEmpty()) {
            updateEdges();
        }

        double[] xPoints = new double[points.size()];
        double[] yPoints = new double[points.size()];

        double cameraX = cameraEntity.getComponent(PositionComponent.class).getGlobalX();
        double cameraY = cameraEntity.getComponent(PositionComponent.class).getGlobalY();

        for (int i = 0; i < points.size(); i++) {
            xPoints[i] = (points.get(i).getX() - cameraX)*Config.relativeWidthRatio;
            yPoints[i] = (points.get(i).getY() - cameraY)*Config.relativeHeightRatio;
        }

        gc.setFill(color);
        gc.fillPolygon(xPoints, yPoints, points.size());
    }

    public void renderFillWithStroke(GraphicsContext gc, Color color, double outerStrokeWidth) {
        CameraEntity cameraEntity = CameraEntity.getInstance();

        if (points.isEmpty()) {
            updateEdges();
        }

        double[] xPoints = new double[points.size()];
        double[] yPoints = new double[points.size()];

        double cameraX = cameraEntity.getComponent(PositionComponent.class).getGlobalX();
        double cameraY = cameraEntity.getComponent(PositionComponent.class).getGlobalY();

        for (int i = 0; i < points.size(); i++) {
            xPoints[i] = (points.get(i).getX() - cameraX)* Config.relativeWidthRatio;
            yPoints[i] = (points.get(i).getY() - cameraY)* Config.relativeHeightRatio;
        }
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(outerStrokeWidth);

        for (int i = 0; i < points.size(); i++) {
            xPoints[i] += outerStrokeWidth / 2;
            yPoints[i] += outerStrokeWidth / 2;
        }
        gc.strokePolygon(xPoints, yPoints, points.size());

        gc.setFill(color);
        gc.fillPolygon(xPoints, yPoints, points.size());
    }


    public void moveTo(Point newPoint) {
        double dx = newPoint.getX() - points.getFirst().getX();
        double dy = newPoint.getY() - points.getFirst().getY();

        for (Point p : points) {
            p.setX(p.getX() + dx);
            p.setY(p.getY() + dy);
        }
        updateEdges();
    }

    public void translate(double x, double y) {
        for (Point p : points) {
            p.translate(x, y);
        }
        updateEdges();
    }

    public void addShape(Shape shape) {
        points.addAll(shape.getPoints());
        updateEdges();
    }

    public boolean isPointInside(Point point) {
        int intersectionCount = 0;
        for (Edge edge : edges) {
            Point p1 = edge.getBeginning();
            Point p2 = edge.getEnd();

            if (point.getY() > Math.min(p1.getY(), p2.getY()) && point.getY() <= Math.max(p1.getY(), p2.getY())) {
                double intersectionX = p1.getX() + (point.getY() - p1.getY()) * (p2.getX() - p1.getX()) / (p2.getY() - p1.getY());
                if (point.getX() < intersectionX) {
                    intersectionCount++;
                }
            }
        }
        return intersectionCount % 2 == 1;
    }

}

