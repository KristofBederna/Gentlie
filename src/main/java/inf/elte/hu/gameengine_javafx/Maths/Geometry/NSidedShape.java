package inf.elte.hu.gameengine_javafx.Maths.Geometry;

import inf.elte.hu.gameengine_javafx.Entities.CameraEntity;
import inf.elte.hu.gameengine_javafx.Misc.Configs.DisplayConfig;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a regular polygon (N-sided shape).
 * Provides functionality for rendering, rotating, and moving the shape.
 */
public class NSidedShape extends Shape {
    private Point center;
    private final double radius;
    private final int segments;
    private double rotation = 0;

    /**
     * Constructs an NSidedShape with a given center, radius, and number of segments.
     *
     * @param center   the center point of the shape
     * @param radius   the distance from center to each vertex
     * @param segments the number of sides/vertices
     */
    public NSidedShape(Point center, double radius, int segments) {
        this.center = center;
        this.radius = radius;
        this.segments = segments;
        this.points = new ArrayList<>(List.of(center));
        generateApproximation();
    }

    /**
     * Constructs a copy of another NSidedShape.
     *
     * @param hitBox the shape to copy
     */
    public NSidedShape(NSidedShape hitBox) {
        this.center = hitBox.center;
        this.radius = hitBox.radius;
        this.segments = hitBox.segments;
        this.rotation = hitBox.rotation;
        this.points = new ArrayList<>(hitBox.points);
    }

    /**
     * Constructs an NSidedShape with a given center, side length, number of segments, and initial rotation.
     *
     * @param center the center point of the shape
     * @param sideLength the length of each side
     * @param segments the number of sides/vertices
     * @param rotation initial rotation in degrees
     */
    public NSidedShape(Point center, double sideLength, int segments, int rotation) {
        this.center = center;
        this.radius = sideLength / 2;
        this.segments = segments;
        this.points = new ArrayList<>();
        generateApproximation();
        this.rotation = rotation;
        rotate(rotation);
    }

    /**
     * Generates the points approximating the N-sided shape based on the current parameters.
     */
    private void generateApproximation() {
        points.clear();
        for (int i = 0; i < this.segments; i++) {
            createPoint(i);
        }
        updateEdges();
        rotate(rotation);
    }

    /**
     * Creates a single point at the given segment index.
     *
     * @param i the segment index
     */
    private void createPoint(int i) {
        double angle = 2 * Math.PI * i / this.segments;
        double x = center.getX() + radius * Math.cos(angle);
        double y = center.getY() + radius * Math.sin(angle);
        points.add(new Point(x, y));
    }

    /**
     * Rotates the shape by the specified number of degrees.
     *
     * @param degrees the degrees to rotate
     */
    public void rotate(double degrees) {
        double angle = Math.toRadians(degrees);
        for (int i = 0; i < points.size(); i++) {
            rotatePoint(i, angle);
        }
        updateEdges();
    }

    /**
     * Rotates a single point around the center.
     *
     * @param i the index of the point
     * @param angle the angle in radians
     */
    private void rotatePoint(int i, double angle) {
        Point p = points.get(i);

        double x = p.getX() - center.getX();
        double y = p.getY() - center.getY();

        double rotatedX = x * Math.cos(angle) - y * Math.sin(angle);
        double rotatedY = x * Math.sin(angle) + y * Math.cos(angle);

        points.set(i, new Point(rotatedX + center.getX(), rotatedY + center.getY()));
    }

    /**
     * Updates the edges of the shape based on the current points.
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
     * Renders the shape outline with a specified color.
     *
     * @param gc the GraphicsContext to draw on
     * @param color the stroke color
     */
    public void render(GraphicsContext gc, Color color) {
        gc.setStroke(color);
        gc.setLineWidth(2);
        renderShape(gc);
    }

    /**
     * Renders the shape outline with a specified color and stroke width.
     *
     * @param gc the GraphicsContext to draw on
     * @param color the stroke color
     * @param strokeWidth the width of the stroke
     */
    public void render(GraphicsContext gc, Color color, double strokeWidth) {
        gc.setStroke(color);
        gc.setLineWidth(strokeWidth);
        renderShape(gc);
    }

    /**
     * Renders the shape's outline.
     *
     * @param gc the GraphicsContext to draw on
     */
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

    /**
     * Renders the filled shape with a specified color.
     *
     * @param gc the GraphicsContext to draw on
     * @param color the fill color
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
     * Ensures edges are updated before rendering if necessary.
     */
    private void updateEdgesForShape() {
        if (points.isEmpty()) {
            generateApproximation();
            updateEdges();
        }
    }

    /**
     * Renders the shape with a fill and an outer stroke.
     *
     * @param gc the GraphicsContext to draw on
     * @param fillColor the fill color
     * @param strokeColor the stroke color
     * @param outerStrokeWidth the width of the outer stroke
     */
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

    /**
     * Moves the shape to a new center point.
     *
     * @param newPoint the new center point
     */
    public void moveTo(Point newPoint) {
        center = newPoint;
        generateApproximation();
        updateEdges();
    }

    /**
     * Translates the shape by a specified amount along the x and y axes.
     *
     * @param x amount to translate along the x-axis
     * @param y amount to translate along the y-axis
     */
    public void translate(double x, double y) {
        center.translate(x, y);
        generateApproximation();
        updateEdges();
    }

    /**
     * Gets the center point of the shape.
     *
     * @return the center point
     */
    public Point getCenter() {
        return center;
    }
}
