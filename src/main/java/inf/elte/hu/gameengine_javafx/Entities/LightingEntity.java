package inf.elte.hu.gameengine_javafx.Entities;

import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.HitBoxComponents.HitBoxComponent;
import inf.elte.hu.gameengine_javafx.Components.HitBoxComponents.LightHitBoxComponent;
import inf.elte.hu.gameengine_javafx.Components.LightComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.VelocityComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.CentralMassComponent;
import inf.elte.hu.gameengine_javafx.Components.RadiusComponent;
import inf.elte.hu.gameengine_javafx.Components.RenderingComponents.ColorComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.*;
import inf.elte.hu.gameengine_javafx.Misc.LightType;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class LightingEntity extends Entity {
    List<Line> listOfRays = new ArrayList<>();

    public LightingEntity(double x, double y, LightType type, double intensity, Color color, double radius, int rays) {
        addComponent(new LightComponent(type, intensity));
        addComponent(new ColorComponent(color));
        addComponent(new RadiusComponent(radius));
        this.getComponent(PositionComponent.class).setLocalPosition(x, y, this);
        addComponent(new VelocityComponent());
        addComponent(new LightHitBoxComponent(new ArrayList<>()));

        calculateRays(rays);

        addToManager();
    }

    private void calculateRays(int rays) {
        listOfRays.clear();
        PositionComponent pos = getComponent(PositionComponent.class);
        RadiusComponent rad = getComponent(RadiusComponent.class);
        double centerX = pos.getGlobalX();
        double centerY = pos.getGlobalY();
        double radius = rad.getRadius();

        for (int i = 0; i < rays; i++) {
            double angle = Math.toRadians(i * (360.0 / rays));
            double endX = centerX + radius * Math.cos(angle);
            double endY = centerY + radius * Math.sin(angle);

            Line ray = new Line(new Point(centerX, centerY), new Point(endX, endY));
            listOfRays.add(ray);
        }
    }

    public void calculateCollisions() {
        calculateRays(100);
        List<Entity> entities = EntityHub.getInstance().getEntitiesWithComponent(HitBoxComponent.class);

        for (Line line : listOfRays) {
            Point start = line.getPoints().getFirst();
            Point closestIntersection = null;
            double minDistance = Double.MAX_VALUE;

            for (Entity entity : entities) {
                if (entity == null) {
                    continue;
                }
                if (entity instanceof PlayerEntity) {
                    continue;
                }
                if (EntityHub.getInstance().getEntitiesWithComponent(HitBoxComponent.class).contains(entity)) {
                    continue;
                }
                HitBoxComponent hitboxComponent = entity.getComponent(HitBoxComponent.class);
                ComplexShape hitbox = hitboxComponent.getHitBox();

                for (Edge edge : hitbox.getEdges()) {
                    Point intersection = Shape.getIntersection(line.getEdges().getFirst(), edge);
                    if (intersection != null) {
                        double distance = start.distanceTo(intersection);
                        if (distance < minDistance) {
                            minDistance = distance;
                            closestIntersection = intersection;
                        }
                    }
                }
            }

            if (closestIntersection != null) {
                line.getPoints().getLast().setX(closestIntersection.getX());
                line.getPoints().getLast().setY(closestIntersection.getY());
            }
        }
    }

    public void mergeRays(LightingEntity other) {
        this.listOfRays.addAll(other.getRays());
    }

    public ComplexShape createShapeFromRays() {
        List<Point> points = new ArrayList<>();
        for (Line line : listOfRays) {
            points.add(line.getEdges().getFirst().getEnd());
        }
        int rightMostIndex = -1;
        Point rightMostPoint = null;

        for (int i = 0; i < points.size(); i++) {
            if (rightMostPoint == null || points.get(i).getX() > rightMostPoint.getX()) {
                rightMostPoint = points.get(i);
                rightMostIndex = i;
            }
        }
        List<Point> reorderedPoints = new ArrayList<>();
        for (int i = rightMostIndex; i < points.size(); i++) {
            reorderedPoints.add(points.get(i));
        }
        for (int i = 0; i < rightMostIndex; i++) {
            reorderedPoints.add(points.get(i));
        }
        ComplexShape complexShape = new ComplexShape(reorderedPoints);
        getComponent(LightHitBoxComponent.class).setHitBox(complexShape);
        return complexShape;
    }


    public void matchPositionToEntity(Entity entity) {
        PositionComponent pos = getComponent(PositionComponent.class);
        CentralMassComponent parentPos = entity.getComponent(CentralMassComponent.class);
        pos.setLocalPosition(parentPos.getCentralX(), parentPos.getCentralY(), this);
        pos.updateGlobalPosition(this);
    }


    public void renderRays(GraphicsContext gc) {
        for (Line ray : listOfRays) {
            ray.render(gc, getComponent(ColorComponent.class).getColor(), 5);
        }
    }

    public List<Line> getRays() {
        return listOfRays;
    }
}
