package inf.elte.hu.gameengine_javafx.Misc.MapClasses;

import inf.elte.hu.gameengine_javafx.Components.TileValueComponent;
import inf.elte.hu.gameengine_javafx.Components.WorldComponents.TileSetComponent;
import inf.elte.hu.gameengine_javafx.Entities.TileEntity;
import inf.elte.hu.gameengine_javafx.Entities.WorldEntity;
import inf.elte.hu.gameengine_javafx.Misc.Config;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@code WorldGenerator} class is responsible for generating chunks of the game world.
 * It creates a {@link Chunk} filled with {@link TileEntity} objects based on the given chunk coordinates
 * and tile values, which are determined by the tile set.
 */
public class WorldGenerator {

    /**
     * Generates a new chunk of the world based on the given chunk coordinates and dimensions.
     *
     * @param chunkX     the X-coordinate of the chunk in the world
     * @param chunkY     the Y-coordinate of the chunk in the world
     * @param chunkWidth the width of the chunk (in tiles)
     * @param chunkHeight the height of the chunk (in tiles)
     * @return a {@link Chunk} object representing the generated chunk
     */
    public static Chunk generateChunk(int chunkX, int chunkY, int chunkWidth, int chunkHeight) {
        List<List<TileEntity>> tiles = new ArrayList<>();

        int[][] tileValues = new int[chunkWidth][chunkHeight];

        // Generate tile values for each tile in the chunk
        for (int y = 0; y < chunkHeight; y++) {
            for (int x = 0; x < chunkWidth; x++) {
                tileValues[y][x] = getNextTileValue(x, y, tileValues, chunkWidth, chunkHeight);
            }
        }

        // Create TileEntities based on generated tile values and world coordinates
        for (int y = 0; y < chunkHeight; y++) {
            List<TileEntity> row = new ArrayList<>();
            for (int x = 0; x < chunkWidth; x++) {
                int worldX = chunkX * chunkWidth * Config.tileSize + x * Config.tileSize;
                int worldY = chunkY * chunkHeight * Config.tileSize + y * Config.tileSize;

                int tileValue = tileValues[y][x];
                String tilePath = TileLoader.getTilePath(tileValue);
                if (tilePath == null) {
                    tilePath = "default.png";
                }
                TileEntity tile = new TileEntity(tileValue, worldX, worldY, "/assets/tiles/" + tilePath + ".png", Config.tileSize, Config.tileSize, Config.wallTiles.contains(tileValue));
                row.add(tile);
            }
            tiles.add(row);
        }

        return new Chunk(tiles);
    }

    /**
     * Returns the tile value for the next tile to be placed at the specified coordinates.
     * Currently, it always returns the tile value 4.
     *
     * @param x          the x-coordinate of the tile in the chunk
     * @param y          the y-coordinate of the tile in the chunk
     * @param tileValues the 2D array of tile values for the chunk
     * @param chunkWidth the width of the chunk
     * @param chunkHeight the height of the chunk
     * @return the tile value to be placed at the specified coordinates
     */
    private static int getNextTileValue(int x, int y, int[][] tileValues, int chunkWidth, int chunkHeight) {
        return 4; // Currently, the method returns a static value of 4 for all tiles.
    }
}
