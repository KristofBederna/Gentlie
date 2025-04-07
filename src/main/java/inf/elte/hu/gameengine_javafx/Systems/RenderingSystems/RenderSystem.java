package inf.elte.hu.gameengine_javafx.Systems.RenderingSystems;

import Game.Components.AttackBoxComponent;
import Game.Components.DaytimeComponent;
import Game.Entities.SkyBoxEntity;
import Game.Entities.SnowBallEntity;
import Game.Entities.WaterEntity;
import Game.Misc.Enums.Daytime;
import inf.elte.hu.gameengine_javafx.Components.HitBoxComponents.HitBoxComponent;
import inf.elte.hu.gameengine_javafx.Components.HitBoxComponents.LightHitBoxComponent;
import inf.elte.hu.gameengine_javafx.Components.LightComponent;
import inf.elte.hu.gameengine_javafx.Components.PathfindingComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.CentralMassComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.DimensionComponent;
import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.RadiusComponent;
import inf.elte.hu.gameengine_javafx.Components.RenderingComponents.ColorComponent;
import inf.elte.hu.gameengine_javafx.Components.RenderingComponents.ImageComponent;
import inf.elte.hu.gameengine_javafx.Components.RenderingComponents.ZIndexComponent;
import inf.elte.hu.gameengine_javafx.Components.ShapeComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.MapMeshComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.WorldDataComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Core.EntityManager;
import inf.elte.hu.gameengine_javafx.Core.ResourceHub;
import inf.elte.hu.gameengine_javafx.Core.ResourceManager;
import inf.elte.hu.gameengine_javafx.Entities.*;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.*;
import inf.elte.hu.gameengine_javafx.Misc.Config;
import inf.elte.hu.gameengine_javafx.Misc.Layers.GameCanvas;
import inf.elte.hu.gameengine_javafx.Misc.Time;
import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * The RenderSystem is responsible for rendering entities, pathfinding routes, map mesh, and other visual elements
 * in the game world. It manages the rendering process, sorting entities by their Z-index, and updating the visual representation
 * of the world, including entities, paths, and lighting.
 * It also ensures that only the entities within the camera's viewport are rendered.
 */
public class RenderSystem extends GameSystem {

    /**
     * Starts the RenderSystem by activating it.
     */
    @Override
    public void start() {
        this.active = true;
    }

    /**
     * Updates the graphics context and processes the rendering of entities, map mesh, pathfinding routes,
     * particles, and other visual elements in the game world.
     * Ensures that entities within the camera's viewport are rendered in order based on their Z-index.
     */
    @Override
    public void update() {
        if (CameraEntity.getInstance() == null) {
            return;
        }
        if (CameraEntity.getInstance().getOwner() == null) {
            return;
        }
        GraphicsContext gc = GameCanvas.getInstance().getGraphicsContext2D();
        CameraEntity cameraEntity = CameraEntity.getInstance();

        if (gc == null || gc.getCanvas() == null) {
            System.err.println("RenderSystem: GraphicsContext or Canvas is null!");
            return;
        }

        Platform.runLater(() -> {
            gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
            if (!EntityHub.getInstance().getEntitiesWithType(SkyBoxEntity.class).isEmpty()) {
                if (EntityHub.getInstance().getEntitiesWithType(SkyBoxEntity.class).getFirst().getComponent(DaytimeComponent.class).getDaytime() == Daytime.DAY) {
                    EntityHub.getInstance().getEntitiesWithType(SkyBoxEntity.class).getFirst().getComponent(ShapeComponent.class).getShape().renderFill(gc, new Color(0.53, 0.81, 0.98, 1.0));
                } else {
                    EntityHub.getInstance().getEntitiesWithType(SkyBoxEntity.class).getFirst().getComponent(ShapeComponent.class).getShape().renderFill(gc, new Color(0.05, 0.05, 0.2, 1.0));
                }
            }

            List<Entity> visibleEntities = EntityHub.getInstance().getEntitiesInsideViewport(CameraEntity.getInstance());
            if (visibleEntities == null) {
                return;
            }
            List<Entity> sortedEntities = sortByZIndex(visibleEntities);

            processEntities(sortedEntities, cameraEntity, gc);
            renderParticles(gc);
            if (Config.renderDebugMode) {
                renderCurrentlyOccupiedTile();
                renderMapMesh(gc);
                renderPathFindingRoute(gc);
                renderPathFindingNeighbours(gc);
                renderShapes(gc);
            }
            if (!EntityHub.getInstance().getEntitiesWithType(WaterEntity.class).isEmpty()) {
                EntityHub.getInstance().getEntitiesWithType(WaterEntity.class).getFirst().getComponent(ShapeComponent.class).getShape().renderFill(gc, new Color(0.29, 0.56, 0.89, 0.8));
            }
            //handleLighting(gc);

            setFocused();
        });
    }

    private static void renderShapes(GraphicsContext gc) {
        for (Entity entity : EntityHub.getInstance().getEntitiesWithComponent(ShapeComponent.class)) {
            if (entity == null) {
                continue;
            }
            entity.getComponent(ShapeComponent.class).getShape().render(gc, Color.PINK);
        }
    }

    private static void renderPathFindingNeighbours(GraphicsContext gc) {
        for (Entity entity : EntityHub.getInstance().getEntitiesWithComponent(PathfindingComponent.class)) {
            if (entity == null) {
                continue;
            }
            PathfindingComponent pathfindingComponent = entity.getComponent(PathfindingComponent.class);
            for (Point neighbour : pathfindingComponent.getNeighbours(entity.getComponent(CentralMassComponent.class).getCentral())) {
                neighbour.render(gc, 5, Color.RED);
            }
        }
    }

    /**
     * Processes and renders the entities inside the camera's viewport, sorted by their Z-index.
     *
     * @param sortedEntities List of entities that need to be rendered, sorted by Z-index.
     * @param cameraEntity   The camera entity used to adjust the rendering coordinates.
     * @param gc             The graphics context used to render the entities.
     */
    private static void processEntities(List<Entity> sortedEntities, CameraEntity cameraEntity, GraphicsContext gc) {
        for (Entity entity : sortedEntities) {
            PositionComponent position = entity.getComponent(PositionComponent.class);
            ImageComponent imgComponent = entity.getComponent(ImageComponent.class);

            if (position == null || imgComponent == null) continue;

            double width = imgComponent.getWidth();
            double height = imgComponent.getHeight();

            double renderX = position.getGlobalX() - cameraEntity.getComponent(PositionComponent.class).getGlobalX();
            double renderY = position.getGlobalY() - cameraEntity.getComponent(PositionComponent.class).getGlobalY();

            renderEntity(entity, renderX, renderY, width, height, imgComponent, gc);
        }
    }

    /**
     * Renders particles that are associated with entities in the game world.
     *
     * @param gc The graphics context used to render the particles.
     */
    private static void renderParticles(GraphicsContext gc) {
        for (Entity entity : EntityHub.getInstance().getEntitiesWithType(ParticleEntity.class)) {
            ((ParticleEntity) entity).alignShapeWithEntity(entity);
            ((ParticleEntity) entity).render(gc);
        }
    }

    /**
     * Renders a single entity by drawing its image and hitbox on the screen.
     *
     * @param entity       The entity to render.
     * @param renderX      The X position of the entity relative to the camera.
     * @param renderY      The Y position of the entity relative to the camera.
     * @param width        The width of the entity.
     * @param height       The height of the entity.
     * @param imgComponent The image component containing the entity's image data.
     * @param gc           The graphics context used to render the entity.
     */
    private static void renderEntity(Entity entity, double renderX, double renderY, double width, double height, ImageComponent imgComponent, GraphicsContext gc) {
        ResourceManager<Image> imageManager = ResourceHub.getInstance().getResourceManager(Image.class);
        if (imageManager == null) return;
        Image img = imageManager.get(imgComponent.getImagePath());

        if (img == null) {
            System.err.println("RenderSystem: Missing image for " + imgComponent.getImagePath());
            return;
        }

        gc.drawImage(img, renderX*Config.relativeWidthRatio, renderY*Config.relativeHeightRatio, width*Config.relativeWidthRatio, height*Config.relativeHeightRatio);

        if (Config.renderDebugMode) {
            renderHitBox(entity, gc);
            renderAttackBox(entity, gc);
        }
    }

    private static void renderAttackBox(Entity entity, GraphicsContext gc) {
        if (entity == null)
            return;
        if (entity.getComponent(AttackBoxComponent.class) == null)
            return;
        entity.getComponent(AttackBoxComponent.class).getAttackBox().render(gc, Color.DARKCYAN);
    }

    /**
     * Renders the hitbox of an entity if it exists.
     *
     * @param entity The entity whose hitbox is to be rendered.
     * @param gc     The graphics context used to render the hitbox.
     */
    private static void renderHitBox(Entity entity, GraphicsContext gc) {
        HitBoxComponent hitBox = entity.getComponent(HitBoxComponent.class);
        if (hitBox != null) {
            hitBox.getHitBox().render(gc, Color.RED);
        }
    }

    /**
     * Sorts a list of entities by their Z-index in ascending order.
     *
     * @param visibleEntities List of entities to be sorted.
     * @return A sorted list of entities based on their Z-index.
     */
    private static List<Entity> sortByZIndex(List<Entity> visibleEntities) {
        return visibleEntities.stream()
                .filter(entity -> entity.getComponent(ZIndexComponent.class) != null)
                .sorted((e1, e2) -> {
                    ZIndexComponent zIndex1 = e1.getComponent(ZIndexComponent.class);
                    ZIndexComponent zIndex2 = e2.getComponent(ZIndexComponent.class);
                    return Integer.compare(zIndex1.getZ_index(), zIndex2.getZ_index());
                })
                .toList();
    }

    /**
     * Renders the tile currently occupied by the player.
     */
    private static void renderCurrentlyOccupiedTile() {
        if (EntityHub.getInstance().getEntitiesWithType(PlayerEntity.class).isEmpty()) {
            return;
        }
        TileEntity tile = WorldEntity.getInstance().getComponent(WorldDataComponent.class).getElement(EntityHub.getInstance().getEntitiesWithType(PlayerEntity.class).getFirst().getComponent(CentralMassComponent.class).getCentral());
        Rectangle rectangle = new Rectangle(tile.getComponent(PositionComponent.class).getGlobal(), tile.getComponent(DimensionComponent.class).getWidth(), tile.getComponent(DimensionComponent.class).getHeight());
        rectangle.renderFill(GameCanvas.getInstance().getGraphicsContext2D(), Color.ORANGE);
    }

    /**
     * Requests focus for the game canvas if it is not already focused.
     */
    private static void setFocused() {
        if (!GameCanvas.getInstance().isFocused()) {
            GameCanvas.getInstance().requestFocus();
        }
    }

    /**
     * Renders the pathfinding route for entities that have a PathfindingComponent.
     *
     * @param gc The graphics context used to render the pathfinding route.
     */
    private static void renderPathFindingRoute(GraphicsContext gc) {
        for (Entity entity : EntityHub.getInstance().getEntitiesWithComponent(PathfindingComponent.class)) {
            if (entity == null) continue;
            PathfindingComponent pathfindingComponent = entity.getComponent(PathfindingComponent.class);
            if (pathfindingComponent.getPath() == null) {
                continue;
            }
            if (pathfindingComponent.getPath().isEmpty()) {
                continue;
            }
            for (Point neighbour : pathfindingComponent.getNeighbours(pathfindingComponent.getPath().getFirst())) {
                Line line = new Line(pathfindingComponent.getPath().getFirst(), neighbour);
                line.render(gc, Color.ORANGE, 5);
            }
            Point last = pathfindingComponent.getPath().getFirst();
            for (Point point : pathfindingComponent.getPath()) {
                if (point.equals(last)) {
                    continue;
                }
                Line line = new Line(last, point);
                last = point;
                line.render(gc, Color.PINK, 5);
            }
        }
    }

    /**
     * Renders the map mesh, which is a grid of points representing the world map.
     *
     * @param gc The graphics context used to render the map mesh.
     */
    private static void renderMapMesh(GraphicsContext gc) {
        MapMeshComponent meshComponent = WorldEntity.getInstance().getComponent(MapMeshComponent.class);
        if (meshComponent != null) {
            for (List<Point> row : meshComponent.getMapCoordinates()) {
                for (Point point : row) {
                    if (point == null) continue;
                    point.renderFill(gc, 5, Color.YELLOW);
                }
            }
        }
    }

    /**
     * Handles the rendering of the lighting system, including the rendering of rays and light effects.
     *
     * @param gc The graphics context used to render the lighting.
     */
    private void handleLighting(GraphicsContext gc) {
        List<Point> points = new ArrayList<>();
        points.add(new Point(0, 0));
        points.add(new Point(0, 1500));
        points.add(new Point(3000, 1500));
        points.add(new Point(3000, 0));
        ComplexShape darkness = new ComplexShape(points);
        for (Entity entity : EntityHub.getInstance().getEntitiesWithComponent(LightComponent.class)) {
            if (entity.getId() == EntityHub.getInstance().getEntitiesWithComponent(LightComponent.class).getFirst().getId()) {
                ((LightingEntity) entity).matchPositionToEntity(EntityHub.getInstance().getEntitiesWithType(PlayerEntity.class).getFirst());
            }

            ((LightingEntity) entity).calculateCollisions();
            //((LightingEntity)entity).renderRays(gc);

            ComplexShape complexShape = ((LightingEntity) entity).createShapeFromRays();
            double firstPos = complexShape.getPoints().getFirst().getX();
            darkness.getPoints().add(new Point(firstPos, 0));
            darkness.updateEdges();
            darkness.addShape(complexShape);
            darkness.getPoints().add(new Point(firstPos, complexShape.getPoints().getFirst().getY()));
            darkness.updateEdges();
            darkness.getPoints().add(new Point(firstPos, 0));
            darkness.updateEdges();
            complexShape.renderFill(gc, new Color(1, 1, 1, 0.2));
        }
        for (Edge edge : darkness.getEdges()) {
            new Line(edge.getBeginning(), edge.getEnd()).render(gc, Color.YELLOW, 3);
        }
        for (Entity entity : EntityHub.getInstance().getEntitiesWithType(LightingEntity.class)) {
            new Point(entity.getComponent(PositionComponent.class).getGlobalX(), entity.getComponent(PositionComponent.class).getGlobalY()).render(gc, 3, Color.CYAN);
        }
        List<Point> toRemove = new ArrayList<>();
        for (Point point : darkness.getPoints()) {
            boolean isLit = false;

            for (Entity entity : EntityHub.getInstance().getEntitiesWithType(LightingEntity.class)) {
                double entityX = entity.getComponent(PositionComponent.class).getGlobalX();
                double entityY = entity.getComponent(PositionComponent.class).getGlobalY();
                double radius = entity.getComponent(RadiusComponent.class).getRadius();
                LightHitBoxComponent hitBox = entity.getComponent(LightHitBoxComponent.class);

                if (point.distanceTo(new Point(entityX, entityY)) <= radius - 1) {
                    if (!hitBox.getHitBox().getPoints().contains(point)) {
                        isLit = true;
                        toRemove.add(point);
                        break;
                    }
                }
            }

            if (!isLit) {
                point.renderFill(gc, 3, Color.ORANGE);
            }
        }

        for (Point point : toRemove) {
            darkness.getPoints().remove(point);
        }
        darkness.updateEdges();
        darkness.renderFill(gc, new Color(0, 0, 0, 0.7));
    }
}
