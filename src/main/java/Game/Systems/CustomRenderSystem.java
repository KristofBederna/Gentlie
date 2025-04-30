package Game.Systems;

import Game.Components.AttackBoxComponent;
import Game.Components.DaytimeComponent;
import Game.Entities.SkyBoxEntity;
import Game.Entities.WaterEntity;
import Game.Misc.Enums.Daytime;
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
import inf.elte.hu.gameengine_javafx.Maths.Geometry.*;
import inf.elte.hu.gameengine_javafx.Misc.Configs.DisplayConfig;
import inf.elte.hu.gameengine_javafx.Misc.Direction;
import inf.elte.hu.gameengine_javafx.Misc.Layers.GameCanvas;
import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.List;

public class CustomRenderSystem extends GameSystem {
    @Override
    public void start() {
        this.active = true;
    }

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
            if (DisplayConfig.renderDebugMode) {
                renderCurrentlyOccupiedTile();
                renderMapMesh(gc);
                renderPathFindingRoute(gc);
                renderPathFindingNeighbours(gc);
                renderShapes(gc);
            }
            if (!EntityHub.getInstance().getEntitiesWithType(WaterEntity.class).isEmpty()) {
                EntityHub.getInstance().getEntitiesWithType(WaterEntity.class).getFirst().getComponent(ShapeComponent.class).getShape().renderFill(gc, new Color(0.29, 0.56, 0.89, 0.8));
            }

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

    private static void renderParticles(GraphicsContext gc) {
        for (Entity entity : EntityHub.getInstance().getEntitiesWithType(ParticleEntity.class)) {
            ((ParticleEntity) entity).alignShapeWithEntity(entity);
            ((ParticleEntity) entity).render(gc);
        }
    }

    private static void renderEntity(Entity entity, double renderX, double renderY, double width, double height, ImageComponent imgComponent, GraphicsContext gc) {
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

    private static void renderHitBox(Entity entity, GraphicsContext gc) {
        HitBoxComponent hitBox = entity.getComponent(HitBoxComponent.class);
        if (hitBox != null) {
            hitBox.getHitBox().render(gc, Color.RED);
        }
    }

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

    private static void renderCurrentlyOccupiedTile() {
        if (EntityHub.getInstance().getEntitiesWithType(PlayerEntity.class).isEmpty()) {
            return;
        }
        TileEntity tile = WorldEntity.getInstance().getComponent(WorldDataComponent.class).getElement(EntityHub.getInstance().getEntitiesWithType(PlayerEntity.class).getFirst().getComponent(CentralMassComponent.class).getCentral());
        Rectangle rectangle = new Rectangle(tile.getComponent(PositionComponent.class).getGlobal(), tile.getComponent(DimensionComponent.class).getWidth(), tile.getComponent(DimensionComponent.class).getHeight());
        rectangle.renderFill(GameCanvas.getInstance().getGraphicsContext2D(), Color.ORANGE);
    }

    private static void setFocused() {
        if (!GameCanvas.getInstance().isFocused()) {
            GameCanvas.getInstance().requestFocus();
        }
    }

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
}
