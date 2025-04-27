package inf.elte.hu.gameengine_javafx.Systems.RenderingSystems;

import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.HitBoxComponent;
import inf.elte.hu.gameengine_javafx.Components.PathfindingComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.CentralMassComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.DimensionComponent;
import inf.elte.hu.gameengine_javafx.Components.RenderingComponents.ImageComponent;
import inf.elte.hu.gameengine_javafx.Components.RenderingComponents.ZIndexComponent;
import inf.elte.hu.gameengine_javafx.Components.ShapeComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.MapMeshComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.WorldDataComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Core.ResourceHub;
import inf.elte.hu.gameengine_javafx.Core.ResourceManager;
import inf.elte.hu.gameengine_javafx.Entities.*;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Line;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Rectangle;
import inf.elte.hu.gameengine_javafx.Misc.Configs.DisplayConfig;
import inf.elte.hu.gameengine_javafx.Misc.Layers.GameCanvas;
import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

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

        if (gc == null || gc.getCanvas() == null) {
            System.err.println("RenderSystem: GraphicsContext or Canvas is null!");
            return;
        }

        Platform.runLater(() -> {
            gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

            List<Entity> visibleEntities = EntityHub.getInstance().getEntitiesInsideViewport(CameraEntity.getInstance());
            if (visibleEntities == null) {
                return;
            }
            List<Entity> sortedEntities = sortByZIndex(visibleEntities);

            processEntities(sortedEntities, gc);
            renderParticles(gc);
            if (DisplayConfig.renderDebugMode) {
                renderCurrentlyOccupiedTile();
                renderMapMesh(gc);
                renderPathFindingRoute(gc);
                renderPathFindingNeighbours(gc);
                renderShapes(gc);
            }

            setFocused();
        });
    }

    private void renderShapes(GraphicsContext gc) {
        for (Entity entity : EntityHub.getInstance().getEntitiesWithComponent(ShapeComponent.class)) {
            if (entity == null) {
                continue;
            }
            entity.getComponent(ShapeComponent.class).getShape().render(gc, Color.PINK);
        }
    }

    private void renderPathFindingNeighbours(GraphicsContext gc) {
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
     * @param gc             The graphics context used to render the entities.
     */
    private void processEntities(List<Entity> sortedEntities, GraphicsContext gc) {
        for (Entity entity : sortedEntities) {
            if (entity == null) {
                continue;
            }
            PositionComponent position = entity.getComponent(PositionComponent.class);
            ImageComponent imgComponent = entity.getComponent(ImageComponent.class);

            if (position == null || imgComponent == null) continue;

            double width = imgComponent.getWidth();
            double height = imgComponent.getHeight();

            double renderX = CameraEntity.getRenderX(position.getGlobalX());
            double renderY = CameraEntity.getRenderY(position.getGlobalY());

            renderEntity(entity, renderX, renderY, width, height, imgComponent, gc);
        }
    }

    /**
     * Renders particles that are associated with entities in the game world.
     *
     * @param gc The graphics context used to render the particles.
     */
    private void renderParticles(GraphicsContext gc) {
        for (Entity entity : EntityHub.getInstance().getEntitiesWithType(ParticleEntity.class)) {
            if (entity == null) {
                continue;
            }
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
    private void renderEntity(Entity entity, double renderX, double renderY, double width, double height, ImageComponent imgComponent, GraphicsContext gc) {
        ResourceManager<Image> imageManager = ResourceHub.getInstance().getResourceManager(Image.class);
        if (imageManager == null) return;
        Image img = imageManager.get(imgComponent.getImagePath());

        if (img == null) {
            System.err.println("RenderSystem: Missing image for " + imgComponent.getImagePath());
            return;
        }

        gc.drawImage(img, renderX * DisplayConfig.relativeWidthRatio, renderY * DisplayConfig.relativeHeightRatio, width * DisplayConfig.relativeWidthRatio, height * DisplayConfig.relativeHeightRatio);

        if (DisplayConfig.renderDebugMode) {
            renderHitBox(entity, gc);
        }
    }

    /**
     * Renders the hitbox of an entity if it exists.
     *
     * @param entity The entity whose hitbox is to be rendered.
     * @param gc     The graphics context used to render the hitbox.
     */
    private void renderHitBox(Entity entity, GraphicsContext gc) {
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
    private List<Entity> sortByZIndex(List<Entity> visibleEntities) {
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
    private void renderCurrentlyOccupiedTile() {
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
    private void setFocused() {
        if (!GameCanvas.getInstance().isFocused()) {
            GameCanvas.getInstance().requestFocus();
        }
    }

    /**
     * Renders the pathfinding route for entities that have a PathfindingComponent.
     *
     * @param gc The graphics context used to render the pathfinding route.
     */
    private void renderPathFindingRoute(GraphicsContext gc) {
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
    private void renderMapMesh(GraphicsContext gc) {
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
}
