package inf.elte.hu.gameengine_javafx.Entities;

import inf.elte.hu.gameengine_javafx.Components.FilePathComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.MapMeshComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.TileSetComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.WorldDataComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.WorldDimensionComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;

public class WorldEntity extends Entity {
    private static WorldEntity instance;

    private WorldEntity(double width, double height, String filePath, String tileSetPath, String separator) {
        this.addComponent(new WorldDimensionComponent(width, height));
        this.addComponent(new WorldDataComponent());
        this.addComponent(new FilePathComponent(filePath));
        this.addComponent(new TileSetComponent(tileSetPath, separator));
        this.addComponent(new MapMeshComponent());
        addToManager();
    }

    private WorldEntity(double width, double height, String filePath, String tileSetPath) {
        this.addComponent(new WorldDimensionComponent(width, height));
        this.addComponent(new WorldDataComponent());
        this.addComponent(new FilePathComponent(filePath));
        this.addComponent(new TileSetComponent(tileSetPath));
        this.addComponent(new MapMeshComponent());
        addToManager();
    }

    private WorldEntity(String filePath, String tileSetPath) {
        this.addComponent(new WorldDimensionComponent());
        this.addComponent(new WorldDataComponent());
        this.addComponent(new FilePathComponent(filePath));
        this.addComponent(new TileSetComponent(tileSetPath));
        this.addComponent(new MapMeshComponent());
        addToManager();
    }

    private WorldEntity(String filePath, String tileSetPath, String separator) {
        this.addComponent(new WorldDimensionComponent());
        this.addComponent(new WorldDataComponent());
        this.addComponent(new FilePathComponent(filePath));
        this.addComponent(new TileSetComponent(tileSetPath, separator));
        this.addComponent(new MapMeshComponent());
        addToManager();
    }

    public static WorldEntity getInstance(double width, double height, String filePath, String tileSetPath, String separator) {
        if (instance == null) {
            instance = new WorldEntity(width, height, filePath, tileSetPath, separator);
        }
        return instance;
    }

    public static WorldEntity getInstance(double width, double height, String filePath, String tileSetPath) {
        if (instance == null) {
            instance = new WorldEntity(width, height, filePath, tileSetPath);
        }
        return instance;
    }

    public static WorldEntity getInstance(String filePath, String tileSetPath) {
        if (instance == null) {
            instance = new WorldEntity(filePath, tileSetPath);
        }
        return instance;
    }

    public static WorldEntity getInstance(String filePath, String tileSetPath, String separator) {
        if (instance == null) {
            instance = new WorldEntity(filePath, tileSetPath, separator);
        }
        return instance;
    }

    public static WorldEntity getInstance() {
        return instance;
    }

    public static void resetInstance() {
        instance = null;
    }
}