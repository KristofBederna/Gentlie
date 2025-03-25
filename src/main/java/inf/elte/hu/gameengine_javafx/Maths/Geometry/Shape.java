package inf.elte.hu.gameengine_javafx.Maths.Geometry;

import inf.elte.hu.gameengine_javafx.Misc.Config;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;

public class Shape {
    protected List<Point> points;
    protected List<Edge> edges;

    public List<Edge> getEdges() {
        return edges;
    }

    public List<Point> getPoints() {
        return points;
    }

    public static boolean intersect(Shape a, Shape b) {
        for (Edge edge1 : a.getEdges()) {
            for (Edge edge2 : b.getEdges()) {
                if (doIntersect(edge1.getBeginning(), edge1.getEnd(), edge2.getBeginning(), edge2.getEnd())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean doIntersect(Point p1, Point p2, Point q1, Point q2) {
        int o1 = orientation(p1, p2, q1);
        int o2 = orientation(p1, p2, q2);
        int o3 = orientation(q1, q2, p1);
        int o4 = orientation(q1, q2, p2);

        if (o1 != o2 && o3 != o4) {
            return true;
        }

        if (o1 == 0 && onSegment(p1, q1, p2)) return true;
        if (o2 == 0 && onSegment(p1, q2, p2)) return true;
        if (o3 == 0 && onSegment(q1, p1, q2)) return true;
        return o4 == 0 && onSegment(q1, p2, q2);
    }

    private static int orientation(Point p, Point q, Point r) {
        double val = (q.getY() - p.getY()) * (r.getX() - q.getX()) - (q.getX() - p.getX()) * (r.getY() - q.getY());
        if (Math.abs(val) < Config.EPSILON) {
            return 0;
        } else if (val > 0) {
            return 1;
        } else {
            return 2;
        }
    }

    private static boolean onSegment(Point p, Point q, Point r) {
        return q.getX() <= Math.max(p.getX(), r.getX()) && q.getX() >= Math.min(p.getX(), r.getX())
                && q.getY() <= Math.max(p.getY(), r.getY()) && q.getY() >= Math.min(p.getY(), r.getY());
    }

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

        if (Math.abs(denominator) < Config.EPSILON) {
            return null;
        }

        double t = ((q1.getX() - p1.getX()) * y2 - (q1.getY() - p1.getY()) * x2) / denominator;
        double u = ((q1.getX() - p1.getX()) * y1 - (q1.getY() - p1.getY()) * x1) / denominator;

        if (t >= 0 && t <= 1 && u >= 0 && u <= 1) {
            double intersectX = p1.getX() + t * x1;
            double intersectY = p1.getY() + t * y1;
            return new Point(intersectX, intersectY);
        }

        return null;
    }

    public void updateEdges() {
    }

    public void render(GraphicsContext gc, Color color) {
    }

    public void renderFill(GraphicsContext gc, Color color) {
    }

    public void renderFillWithStroke(GraphicsContext gc, Color color, double outerStrokeWidth) {
    }

    public void moveTo(Point newPoint) {
    }

    public void translate(double x, double y) {
    }
}
