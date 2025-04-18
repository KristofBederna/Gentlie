package inf.elte.hu.gameengine_javafx.Misc;

import inf.elte.hu.gameengine_javafx.Components.TileValueComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.WorldDataComponent;
import inf.elte.hu.gameengine_javafx.Entities.WorldEntity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Misc.Configs.MapConfig;
import inf.elte.hu.gameengine_javafx.Misc.Configs.WalkerConfig;
import inf.elte.hu.gameengine_javafx.Misc.MapClasses.World;

import java.util.ArrayList;
import java.util.Random;

public class Walker {
    private int x;
    private int y;
    private ArrayList<Walker> walkers;
    private WorldEntity world;
    private static final Random random = new Random(); // Shared random instance

    public Walker(int x, int y, WorldEntity world, ArrayList<Walker> walkers) {
        this.x = x;
        this.y = y;
        this.world = world;
        this.walkers = walkers;
    }

    public void walk() {
        while (getFilledPercentage() < WalkerConfig.stopPercentage) {
            if (isWallTile()) {
                changeDirection();
                continue;
            }

            placeTile();
            performRandomAction();  // Handle random actions (multiply, die, teleport)
            changeDirection();  // Move the walker in a random direction
        }
    }

    // Check if the current position is a wall tile
    private boolean isWallTile() {
        return MapConfig.wallTiles.contains(
                world.getComponent(WorldDataComponent.class)
                        .getMapData()
                        .getElementAt(new Point(y * MapConfig.scaledTileSize + MapConfig.scaledTileSize / 2,
                                x * MapConfig.scaledTileSize + MapConfig.scaledTileSize / 2))
                        .getComponent(TileValueComponent.class)
                        .getTileValue()
        );
    }

    // Perform a random action like multiply, die, or teleport
    private void performRandomAction() {
        int action = random.nextInt(10);
        if (action % WalkerConfig.moduloToTeleport == 0) {
            teleport();
        } else if (action % WalkerConfig.moduloToMultiply == 0) {
            multiply();
        } else if (action % WalkerConfig.moduloToDie == 0) {
            die();
        }
    }

    // Calculate the filled percentage of tiles in the world
    public int getFilledPercentage() {
        int filledTiles = 0;
        World map = WorldEntity.getInstance().getComponent(WorldDataComponent.class).getMapData();
        int totalTiles = map.size() * MapConfig.chunkHeight * MapConfig.chunkWidth;

        for (int i = 0; i < WalkerConfig.maxX; i++) {
            for (int j = 0; j < WalkerConfig.maxY; j++) {
                if (isWalkerTileAt(i, j)) {
                    filledTiles++;
                }
            }
        }

        return (filledTiles * 100) / totalTiles;
    }

    // Check if a specific tile is a wall tile
    private boolean isWalkerTileAt(int i, int j) {
        return WalkerConfig.placeTileNumber == (
                world.getComponent(WorldDataComponent.class)
                        .getMapData()
                        .getElementAt(new Point(j * MapConfig.scaledTileSize + MapConfig.scaledTileSize / 2,
                                i * MapConfig.scaledTileSize + MapConfig.scaledTileSize / 2))
                        .getComponent(TileValueComponent.class)
                        .getTileValue()
        );
    }

    // Change the direction of the walker
    private void changeDirection() {
        int direction = random.nextInt(4);

        switch (direction) {
            case 0 -> moveUp();
            case 1 -> moveDown();
            case 2 -> moveLeft();
            case 3 -> moveRight();
        }
    }

    // Move the walker up
    private void moveUp() {
        if (y < WalkerConfig.maxY) y++;
    }

    // Move the walker down
    private void moveDown() {
        if (y > 0) y--;
    }

    // Move the walker left
    private void moveLeft() {
        if (x > 0) x--;
    }

    // Move the walker right
    private void moveRight() {
        if (x < WalkerConfig.maxX) x++;
    }

    // Place the tile on the map and the saved chunk
    private void placeTile() {
        Point currentPoint = new Point(x * MapConfig.scaledTileSize, y * MapConfig.scaledTileSize);
        world.getComponent(WorldDataComponent.class).getMapData().setElementAt(currentPoint, WalkerConfig.placeTileNumber);
        world.getComponent(WorldDataComponent.class).getMapData().setElementAtSaved(currentPoint, WalkerConfig.placeTileNumber);
    }

    // Create a new walker to multiply the current one
    private void multiply() {
        if (walkers.size() < WalkerConfig.maxWalkers) {
            Walker newWalker = new Walker(this.x, this.y, this.world, this.walkers);
            walkers.add(newWalker);
        }
    }

    // Remove a walker from the list to simulate death
    private void die() {
        if (walkers.size() > 1) {
            walkers.removeLast(); // Remove the last walker
        }
    }

    // Teleport the walker to a new random position
    private void teleport() {
        this.x = random.nextInt(WalkerConfig.minX, WalkerConfig.maxX);
        this.y = random.nextInt(WalkerConfig.minY, WalkerConfig.maxY);
    }
}
