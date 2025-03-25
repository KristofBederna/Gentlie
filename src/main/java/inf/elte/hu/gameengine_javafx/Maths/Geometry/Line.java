package inf.elte.hu.gameengine_javafx.Maths.Geometry;

import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Entities.CameraEntity;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Line extends Shape {
    public Line(Point start, Point end) {
        points = new ArrayList<>();
        points.add(start);
        points.add(end);
        updateEdges();
    }

    public void updateEdges() {
        if (this.edges == null) {
            this.edges = new ArrayList<>();
        } else {
            this.edges.clear();
        }
        if (points.size() >= 2) {
            Point p1 = points.get(0);
            Point p2 = points.get(1);
            edges.add(new Edge(p1, p2));
        }
    }

    public void render(GraphicsContext gc, Color color, int thickness) {
        CameraEntity cameraEntity = CameraEntity.getInstance();

        gc.setStroke(color);
        gc.setLineWidth(thickness);

        if (points.size() >= 2) {
            Point start = points.get(0);
            Point end = points.get(1);

            double x1 = start.getX() - cameraEntity.getComponent(PositionComponent.class).getGlobalX();
            double y1 = start.getY() - cameraEntity.getComponent(PositionComponent.class).getGlobalY();
            double x2 = end.getX() - cameraEntity.getComponent(PositionComponent.class).getGlobalX();
            double y2 = end.getY() - cameraEntity.getComponent(PositionComponent.class).getGlobalY();

            gc.strokeLine(x1, y1, x2, y2);
        }
    }

    public void moveTo(Point newPoint) {
        if (points.size() >= 2) {
            double dx = newPoint.getX() - points.get(0).getX();
            double dy = newPoint.getY() - points.get(0).getY();

            points.get(0).setX(points.get(0).getX() + dx);
            points.get(0).setY(points.get(0).getY() + dy);
            points.get(1).setX(points.get(1).getX() + dx);
            points.get(1).setY(points.get(1).getY() + dy);

            updateEdges();
        }
    }

    public void translate(double x, double y) {
        for (Point p : points) {
            p.translate(x, y);
        }
        updateEdges();
    }
}
