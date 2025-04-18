package inf.elte.hu.gameengine_javafx.Misc.MapClasses;

import inf.elte.hu.gameengine_javafx.Entities.TileEntity;
import inf.elte.hu.gameengine_javafx.Misc.Configs.MapConfig;

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

        int[][] tileValues = new int[chunkHeight][chunkWidth];

        // Generate tile values for each tile in the chunk
        generateBlankMap(chunkWidth, chunkHeight, tileValues);

        // Create TileEntities based on generated tile values and world coordinates
        for (int y = 0; y < chunkHeight; y++) {
            generateRow(chunkX, chunkY, chunkWidth, chunkHeight, y, tileValues, tiles);
        }

        return new Chunk(tiles);
    }

    private static void generateRow(int chunkX, int chunkY, int chunkWidth, int chunkHeight, int y, int[][] tileValues, List<List<TileEntity>> tiles) {
        List<TileEntity> row = new ArrayList<>();
        for (int x = 0; x < chunkWidth; x++) {
            generateTile(chunkX, chunkY, chunkWidth, chunkHeight, x, y, tileValues, row);
        }
        tiles.add(row);
    }

    private static void generateTile(int chunkX, int chunkY, int chunkWidth, int chunkHeight, int x, int y, int[][] tileValues, List<TileEntity> row) {
        int worldX = (int) (chunkX * chunkWidth * MapConfig.scaledTileSize + x * MapConfig.scaledTileSize);
        int worldY = (int) (chunkY * chunkHeight * MapConfig.scaledTileSize + y * MapConfig.scaledTileSize);

        int tileValue = tileValues[y][x];
        String tilePath = TileLoader.getTilePath(tileValue);
        if (tilePath == null) {
            tilePath = "default.png";
        }
        TileEntity tile = new TileEntity(tileValue, worldX, worldY, "/assets/tiles/" + tilePath + ".png", MapConfig.scaledTileSize, MapConfig.scaledTileSize);
        row.add(tile);
    }

    private static void generateBlankMap(int chunkWidth, int chunkHeight, int[][] tileValues) {
        for (int y = 0; y < chunkHeight; y++) {
            for (int x = 0; x < chunkWidth; x++) {
                tileValues[y][x] = MapConfig.defaultTileCode;
            }
        }
    }
}
