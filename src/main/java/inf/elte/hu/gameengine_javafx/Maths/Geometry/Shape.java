package inf.elte.hu.gameengine_javafx.Maths.Geometry;

import inf.elte.hu.gameengine_javafx.Misc.Configs.PhysicsConfig;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;

/**
 * Abstract geometric shape composed of Points and Edges.
 * Provides methods for geometric operations like intersection detection.
 */
public class Shape {
    protected List<Point> points;
    protected List<Edge> edges;

    public List<Edge> getEdges() {
        return edges;
    }

    public List<Point> getPoints() {
        return points;
    }

    /**
     * Checks if any edge of shape a intersects with any edge of shape b.
     * Uses orientation and on-segment tests from computational geometry.
     * Reference: "Computational Geometry: Algorithms and Applications".
     */
    public static boolean intersect(Shape a, Shape b) {
        for (Edge edge1 : a.getEdges()) {
            for (Edge edge2 : b.getEdges()) {
                if (edgesIntersect(edge1, edge2)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if two edges intersect.
     * Delegates to point-segment intersection check.
     */
    public static boolean edgesIntersect(Edge e1, Edge e2) {
        return doIntersect(e1.getBeginning(), e1.getEnd(), e2.getBeginning(), e2.getEnd());
    }

    /**
     * Determines if segments p1p2 and q1q2 intersect.
     * General Case: if orientation(p1, p2, q1) != orientation(p1, p2, q2) AND
     *                         orientation(q1, q2, p1) != orientation(q1, q2, p2)
     * Special Case: check if any of the points lie on the other segment (collinear case).
     * Based on: Orientation Test & Segment Intersection Theorem.
     * Based on code from: <a href="https://www.geeksforgeeks.org/check-if-two-given-line-segments-intersect/">GeeksForGeeks</a>
     */
    private static boolean doIntersect(Point p1, Point p2, Point q1, Point q2) {
        int o1 = orientation(p1, p2, q1);
        int o2 = orientation(p1, p2, q2);
        int o3 = orientation(q1, q2, p1);
        int o4 = orientation(q1, q2, p2);

        // General case
        if (o1 != o2 && o3 != o4) return true;

        // Special Cases (collinearity)
        if (o1 == 0 && onSegment(p1, q1, p2)) return true;
        if (o2 == 0 && onSegment(p1, q2, p2)) return true;
        if (o3 == 0 && onSegment(q1, p1, q2)) return true;
        return o4 == 0 && onSegment(q1, p2, q2);
    }

    /**
     * Returns orientation of ordered triplet (p, q, r).
     * 0 --> collinear
     * 1 --> clockwise
     * 2 --> counterclockwise
     * Orientation test is based on the sign of the cross product of vectors pq and qr.
     */
    private static int orientation(Point p, Point q, Point r) {
        double val = (q.getY() - p.getY()) * (r.getX() - q.getX())
                - (q.getX() - p.getX()) * (r.getY() - q.getY());

        if (Math.abs(val) < PhysicsConfig.EPSILON) return 0; // collinear
        return (val > 0) ? 1 : 2; // 1 = clockwise, 2 = counterclockwise
    }

    /**
     * Checks whether point q lies on the segment pr.
     * Assumes p, q, r are collinear.
     */
    private static boolean onSegment(Point p, Point q, Point r) {
        return q.getX() <= Math.max(p.getX(), r.getX()) && q.getX() >= Math.min(p.getX(), r.getX())
                && q.getY() <= Math.max(p.getY(), r.getY()) && q.getY() >= Math.min(p.getY(), r.getY());
    }

    /**
     * Returns the exact point of intersection between two line segments if they intersect.
     * Otherwise, returns null.
     * Based on solving the parametric form of line equations.
     */
    public static Point getIntersection(Edge edgeA, Edge edgeB) {
        Point p1 = edgeA.getBeginning();
        Point p2 = edgeA.getEnd();
        Point q1 = edgeB.getBeginning();
        Point q2 = edgeB.getEnd();

        double x1 = p2.getX() - p1.getX();
        double y1 = p2.getY() - p1.getY();
        double x2 = q2.getX() - q1.getX();
        double y2 = q2.getY() - q1.getY();

        double denominator = x1 * y2 - y1 * x2;

        // Parallel lines (no intersection or infinite)
        if (Math.abs(denominator) < PhysicsConfig.EPSILON) return null;

        // Solve for t and u (parametric line equations)
        double t = ((q1.getX() - p1.getX()) * y2 - (q1.getY() - p1.getY()) * x2) / denominator;
        double u = ((q1.getX() - p1.getX()) * y1 - (q1.getY() - p1.getY()) * x1) / denominator;

        // Check if intersection occurs within the segment bounds
        if (t >= 0 && t <= 1 && u >= 0 && u <= 1) {
            double intersectX = p1.getX() + t * x1;
            double intersectY = p1.getY() + t * y1;
            return new Point(intersectX, intersectY);
        }

        return null; // intersection point is outside of the segments
    }

    // Placeholder methods for subclasses to override
    public void updateEdges() {
    }

    public void render(GraphicsContext gc, Color color) {
    }

    public void render(GraphicsContext gc, Color color, double strokeWidth) {
    }

    public void renderFill(GraphicsContext gc, Color color) {
    }

    public void renderFillWithStroke(GraphicsContext gc, Color color, Color stroke, double outerStrokeWidth) {
    }

    public void moveTo(Point newPoint) {
    }

    public void translate(double x, double y) {
    }
}
