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

/**
 * The Walker class simulates an entity that moves around the map,
 * placing tiles, multiplying, dying, or teleporting based on random events.
 */
public class Walker {
    private int x;
    private int y;
    private final ArrayList<Walker> walkers;
    private final WorldEntity world;
    private static final Random random = new Random(); // Shared random instance

    /**
     * Constructs a Walker instance.
     *
     * @param x       The initial x-coordinate.
     * @param y       The initial y-coordinate.
     * @param world   The world entity the walker interacts with.
     * @param walkers The shared list of walkers.
     */
    public Walker(int x, int y, WorldEntity world, ArrayList<Walker> walkers) {
        this.x = x;
        this.y = y;
        this.world = world;
        this.walkers = walkers;
    }

    /**
     * Begins the walking process until the filled percentage reaches the stop threshold.
     */
    public void walk() {
        while (getFilledPercentage() < WalkerConfig.stopPercentage) {
            if (isWallTile()) {
                changeDirection();
                continue;
            }

            placeTile();
            performRandomAction();  // Handle random actions (multiply, die, teleport)
            changeDirection();      // Move the walker in a random direction
        }
    }

    /**
     * Checks if the current tile is a wall tile.
     *
     * @return True if the current tile is a wall tile, otherwise false.
     */
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

    /**
     * Performs a random action: teleport, multiply, or die.
     */
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

    /**
     * Calculates the percentage of the map that has been filled with walker tiles.
     *
     * @return The filled percentage as an integer.
     */
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

    /**
     * Checks if a specific tile at (i, j) is a walker tile.
     *
     * @param i The x-coordinate.
     * @param j The y-coordinate.
     * @return True if the tile is a walker tile, otherwise false.
     */
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

    /**
     * Changes the direction of the walker randomly.
     */
    private void changeDirection() {
        int direction = random.nextInt(4);

        switch (direction) {
            case 0 -> moveUp();
            case 1 -> moveDown();
            case 2 -> moveLeft();
            case 3 -> moveRight();
        }
    }

    /**
     * Moves the walker one step upward if possible.
     */
    private void moveUp() {
        if (y < WalkerConfig.maxY) y++;
    }

    /**
     * Moves the walker one step downward if possible.
     */
    private void moveDown() {
        if (y > 0) y--;
    }

    /**
     * Moves the walker one step left if possible.
     */
    private void moveLeft() {
        if (x > 0) x--;
    }

    /**
     * Moves the walker one step right if possible.
     */
    private void moveRight() {
        if (x < WalkerConfig.maxX) x++;
    }

    /**
     * Places a tile at the walker's current position.
     */
    private void placeTile() {
        Point currentPoint = new Point(x * MapConfig.scaledTileSize, y * MapConfig.scaledTileSize);
        world.getComponent(WorldDataComponent.class).getMapData().setElementAt(currentPoint, WalkerConfig.placeTileNumber);
        world.getComponent(WorldDataComponent.class).getMapData().setElementAtSaved(currentPoint, WalkerConfig.placeTileNumber);
    }

    /**
     * Creates a new walker at the current position if the walker count allows it.
     */
    private void multiply() {
        if (walkers.size() < WalkerConfig.maxWalkers) {
            Walker newWalker = new Walker(this.x, this.y, this.world, this.walkers);
            walkers.add(newWalker);
        }
    }

    /**
     * Removes the last walker in the list to simulate death if there are multiple walkers.
     */
    private void die() {
        if (walkers.size() > 1) {
            walkers.removeLast(); // Remove the last walker
        }
    }

    /**
     * Teleports the walker to a random position within bounds.
     */
    private void teleport() {
        this.x = random.nextInt(WalkerConfig.minX, WalkerConfig.maxX);
        this.y = random.nextInt(WalkerConfig.minY, WalkerConfig.maxY);
    }
}
