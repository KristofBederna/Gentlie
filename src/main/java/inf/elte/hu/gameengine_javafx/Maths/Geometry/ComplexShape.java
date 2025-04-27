package inf.elte.hu.gameengine_javafx.Maths.Geometry;

import inf.elte.hu.gameengine_javafx.Entities.CameraEntity;
import inf.elte.hu.gameengine_javafx.Misc.Configs.DisplayConfig;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a complex, polygonal shape composed of multiple points and edges.
 * Provides functionality for rendering, transforming, and detecting point collisions.
 */
public class ComplexShape extends Shape {

    /**
     * Constructs an empty ComplexShape.
     */
    public ComplexShape() {
        this.points = new ArrayList<>();
        updateEdges();
    }

    /**
     * Constructs a ComplexShape from a list of points.
     *
     * @param points List of points defining the shape.
     */
    public ComplexShape(List<Point> points) {
        this.points = new ArrayList<>();
        this.points.addAll(points);
        updateEdges();
    }

    /**
     * Constructs a ComplexShape by copying another ComplexShape.
     *
     * @param hitBox The ComplexShape to copy.
     */
    public ComplexShape(ComplexShape hitBox) {
        this.points = new ArrayList<>();
        this.points.addAll(hitBox.points);
    }

    /**
     * Updates the edges based on the current points of the shape.
     */
    public void updateEdges() {
        this.edges = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            Point p1 = points.get(i);
            Point p2 = points.get((i + 1) % points.size());
            edges.add(new Edge(p1, p2));
        }
    }

    /**
     * Renders the shape's outline on the given GraphicsContext with the specified color.
     *
     * @param gc    The GraphicsContext to draw on.
     * @param color The stroke color.
     */
    public void render(GraphicsContext gc, Color color) {
        gc.setStroke(color);
        gc.setLineWidth(2);
        renderShape(gc);
    }

    /**
     * Renders the shape's outline on the given GraphicsContext with the specified color and stroke width.
     *
     * @param gc          The GraphicsContext to draw on.
     * @param color       The stroke color.
     * @param strokeWidth The width of the stroke.
     */
    public void render(GraphicsContext gc, Color color, double strokeWidth) {
        gc.setStroke(color);
        gc.setLineWidth(strokeWidth);
        renderShape(gc);
    }

    /**
     * Internal helper method to render the shape outline.
     *
     * @param gc The GraphicsContext to draw on.
     */
    private void renderShape(GraphicsContext gc) {
        updateEdgesForShape();

        Point prev = points.getLast();
        for (Point p : points) {
            double x1 = CameraEntity.getRenderX(prev.getX());
            double y1 = CameraEntity.getRenderY(prev.getY());
            double x2 = CameraEntity.getRenderX(p.getX());
            double y2 = CameraEntity.getRenderY(p.getY());

            gc.strokeLine(x1 * DisplayConfig.relativeWidthRatio, y1 * DisplayConfig.relativeHeightRatio,
                    x2 * DisplayConfig.relativeWidthRatio, y2 * DisplayConfig.relativeHeightRatio);
            prev = p;
        }
    }

    /**
     * Renders the shape as a filled polygon with the specified color.
     *
     * @param gc    The GraphicsContext to draw on.
     * @param color The fill color.
     */
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

    /**
     * Ensures that edges are updated if the shape has points.
     */
    private void updateEdgesForShape() {
        if (points.isEmpty()) {
            updateEdges();
        }
    }

    /**
     * Renders the shape as a filled polygon with an outer stroke.
     *
     * @param gc               The GraphicsContext to draw on.
     * @param color            The fill color.
     * @param stroke           The stroke color.
     * @param outerStrokeWidth The width of the outer stroke.
     */
    public void renderFillWithStroke(GraphicsContext gc, Color color, Color stroke, double outerStrokeWidth) {
        updateEdgesForShape();

        double[] xPoints = new double[points.size()];
        double[] yPoints = new double[points.size()];

        for (int i = 0; i < points.size(); i++) {
            xPoints[i] = CameraEntity.getRenderX(points.get(i).getX()) * DisplayConfig.relativeWidthRatio;
            yPoints[i] = CameraEntity.getRenderY(points.get(i).getY()) * DisplayConfig.relativeHeightRatio;
        }

        gc.setStroke(stroke);
        gc.setLineWidth(outerStrokeWidth);

        for (int i = 0; i < points.size(); i++) {
            xPoints[i] += outerStrokeWidth / 2;
            yPoints[i] += outerStrokeWidth / 2;
        }
        gc.strokePolygon(xPoints, yPoints, points.size());

        gc.setFill(color);
        gc.fillPolygon(xPoints, yPoints, points.size());
    }

    /**
     * Moves the entire shape so that its first point matches the given new point.
     *
     * @param newPoint The new position for the first point.
     */
    public void moveTo(Point newPoint) {
        double dx = newPoint.getX() - points.getFirst().getX();
        double dy = newPoint.getY() - points.getFirst().getY();

        for (Point p : points) {
            p.setX(p.getX() + dx);
            p.setY(p.getY() + dy);
        }
        updateEdges();
    }

    /**
     * Translates the shape by the given x and y amounts.
     *
     * @param x The amount to translate along the x-axis.
     * @param y The amount to translate along the y-axis.
     */
    public void translate(double x, double y) {
        for (Point p : points) {
            p.translate(x, y);
        }
        updateEdges();
    }

    /**
     * Checks whether a given point is inside the shape using ray-casting algorithm.
     *
     * @param point The point to test.
     * @return True if the point is inside the shape, false otherwise.
     */
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
