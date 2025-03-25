package inf.elte.hu.gameengine_javafx.Maths.Geometry;

import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Entities.CameraEntity;
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
        gc.setStroke(color);
        gc.setLineWidth(2);

        CameraEntity cameraEntity = CameraEntity.getInstance();

        PositionComponent playerPos = cameraEntity.getOwner().getComponent(PositionComponent.class);

        double renderAX = points.get(0).getX() - playerPos.getGlobalX();
        double renderAY = points.get(0).getY() - playerPos.getGlobalY();
        double renderBX = points.get(1).getX() - playerPos.getGlobalX();
        double renderBY = points.get(1).getY() - playerPos.getGlobalY();
        double renderCX = points.get(2).getX() - playerPos.getGlobalX();
        double renderCY = points.get(2).getY() - playerPos.getGlobalY();

        gc.strokeLine(renderAX, renderAY, renderBX, renderBY);
        gc.strokeLine(renderBX, renderBY, renderCX, renderCY);
        gc.strokeLine(renderCX, renderCY, renderAX, renderAY);
    }

    public void renderFill(GraphicsContext gc, Color color) {
        CameraEntity cameraEntity = CameraEntity.getInstance();
        PositionComponent playerPos = cameraEntity.getOwner().getComponent(PositionComponent.class);

        double renderAX = points.get(0).getX() - playerPos.getGlobalX();
        double renderAY = points.get(0).getY() - playerPos.getGlobalY();
        double renderBX = points.get(1).getX() - playerPos.getGlobalX();
        double renderBY = points.get(1).getY() - playerPos.getGlobalY();
        double renderCX = points.get(2).getX() - playerPos.getGlobalX();
        double renderCY = points.get(2).getY() - playerPos.getGlobalY();

        gc.setFill(color);
        double[] xPoints = {renderAX, renderBX, renderCX};
        double[] yPoints = {renderAY, renderBY, renderCY};
        gc.fillPolygon(xPoints, yPoints, 3);

        gc.setStroke(color);
        gc.setLineWidth(2);

        gc.strokeLine(renderAX, renderAY, renderBX, renderBY);
        gc.strokeLine(renderBX, renderBY, renderCX, renderCY);
        gc.strokeLine(renderCX, renderCY, renderAX, renderAY);
    }

}
