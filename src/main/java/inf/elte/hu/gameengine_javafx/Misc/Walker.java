package inf.elte.hu.gameengine_javafx.Misc;

import inf.elte.hu.gameengine_javafx.Components.TileValueComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.MapMeshComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.WorldDataComponent;
import inf.elte.hu.gameengine_javafx.Entities.TileEntity;
import inf.elte.hu.gameengine_javafx.Entities.WorldEntity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Misc.MapClasses.World;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class Walker {
    private int x;
    private int y;
    private ArrayList<Walker> walkers;
    private WorldEntity world;
    private static final int MAX_X = 30;
    private static final int MAX_Y = 30;
    private static final int STOP_PERCENTAGE = 30;

    public Walker(int x, int y, WorldEntity world, ArrayList<Walker> walkers) {
        this.x = x;
        this.y = y;
        this.world = world;
        this.walkers = walkers;
    }

    public void walk() {
        Random r = new Random();
        while (getFilledPercentage() < STOP_PERCENTAGE) {
            System.out.println(getFilledPercentage());
            if (Config.wallTiles.contains(world.getComponent(WorldDataComponent.class).getElement(x, y).getComponent(TileValueComponent.class).getTileValue())) {
                changeDirection();
                continue;
            }

            placeTile();
            if (r.nextInt(10) % 3 == 0) {
                multiply();
            } else if (r.nextInt(10) % 4 == 0) {
                die();
            } else if (r.nextInt(10) % 9 == 0) {
                teleport();
            }
            changeDirection();
        }
    }

    private void teleport() {
        Random r = new Random();
        this.x = r.nextInt(1, MAX_X);  // Random number between 1 and MAX_X
        this.y = r.nextInt(1, MAX_Y);  // Random number between 1 and MAX_Y
    }

    public int getFilledPercentage() {
        int filledTiles = 0;
        int totalTiles = MAX_X * MAX_Y; // Total number of tiles

        // Count the filled tiles (0s)
        for (int i = 0; i < MAX_X; i++) {
            for (int j = 0; j < MAX_Y; j++) {
                if (Config.wallTiles.contains(world.getComponent(WorldDataComponent.class).getElement(i, j).getComponent(TileValueComponent.class).getTileValue())) {
                    filledTiles++;
                }
            }
        }

        // Calculate the percentage of filled tiles
        return (filledTiles * 100) / totalTiles;
    }

    private void changeDirection() {
        Random random = new Random();
        int direction = random.nextInt(4);

        switch (direction) {
            case 0 -> { if (y < MAX_Y) y++; } // Move Up
            case 1 -> { if (y > 0) y--; } // Move Down
            case 2 -> { if (x > 0) x--; } // Move Left
            case 3 -> { if (x < MAX_X) x++; } // Move Right
        }
        System.out.println("Moving direction: " + direction);
    }

    private void placeTile() {
        // Mark the tile as non-walkable (1)
        this.world.getComponent(WorldDataComponent.class).getMapData().setElementAt(new Point(x*Config.tileSize, y*Config.tileSize), new TileEntity(1, x*Config.tileSize, y*Config.tileSize, "/assets/images/default.png", Config.tileSize, Config.tileSize, true));
        this.world.getComponent(WorldDataComponent.class).getMapData().getSavedChunks().get(new Tuple<>(Math.floorDiv(x, Config.chunkWidth), Math.floorDiv(y, Config.chunkHeight))).setElement(x % Config.chunkWidth, y % Config.chunkHeight, 1);
    }

    private void multiply() {
        if (walkers.size() >= 3) {
            return;  // Limit the number of walkers to avoid too many
        }
        System.out.println("multiplying " + walkers.size());
        Walker walker = new Walker(this.x, this.y, this.world, this.walkers);
        walkers.add(walker);
    }

    private void die() {
        if (walkers.size() <= 1) {
            return; // No walkers to remove
        }
        if (!walkers.isEmpty()) {
            walkers.remove(walkers.size() - 1); // Safely remove the last walker
        }
        System.out.println("killed walker: " + walkers.size());
    }
}
