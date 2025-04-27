package inf.elte.hu.gameengine_javafx.Misc.MapClasses;

import inf.elte.hu.gameengine_javafx.Entities.TileEntity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Misc.Configs.MapConfig;
import inf.elte.hu.gameengine_javafx.Misc.Tuple;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@code World} class represents the game world, consisting of chunks of tiles.
 * It provides methods for managing and retrieving chunks, as well as accessing specific elements within those chunks.
 */
public class World {
    private final Map<Tuple<Integer, Integer>, Chunk> chunks = new HashMap<>();
    private Map<Tuple<Integer, Integer>, Chunk> savedChunks = new HashMap<>();

    /**
     * Constructs a new empty {@code World} with no chunks loaded.
     */
    public World() {
    }

    /**
     * Gets the map of saved chunks in the world.
     *
     * @return the map of saved chunks, with coordinates as keys
     */
    public Map<Tuple<Integer, Integer>, Chunk> getSavedChunks() {
        return savedChunks;
    }

    /**
     * Sets the map of saved chunks in the world.
     *
     * @param savedChunks the map of saved chunks to set
     */
    public void setSavedChunks(Map<Tuple<Integer, Integer>, Chunk> savedChunks) {
        this.savedChunks = savedChunks;
    }

    /**
     * Clears all loaded chunks from the world.
     */
    public void clear() {
        chunks.clear();
    }

    /**
     * Retrieves a chunk at the specified world coordinates (x, y).
     *
     * @param x the x-coordinate of the chunk
     * @param y the y-coordinate of the chunk
     * @return the chunk at the specified coordinates, or {@code null} if not loaded
     */
    public Chunk get(int x, int y) {
        return chunks.getOrDefault(new Tuple<>(x, y), null);
    }

    /**
     * Gets the number of chunks currently loaded in the world.
     *
     * @return the number of loaded chunks
     */
    public int size() {
        return chunks.size();
    }

    /**
     * Retrieves the map of all chunks in the world.
     *
     * @return the map of all chunks, with coordinates as keys
     */
    public Map<Tuple<Integer, Integer>, Chunk> getWorld() {
        return chunks;
    }

    /**
     * Adds a chunk at the specified coordinates (x, y) to the world.
     *
     * @param x the x-coordinate where the chunk should be placed
     * @param y the y-coordinate where the chunk should be placed
     * @param chunk the chunk to add
     */
    public void addChunk(int x, int y, Chunk chunk) {
        chunks.put(new Tuple<>(x, y), chunk);
        savedChunks.put(new Tuple<>(x, y), chunk);
    }

    /**
     * Retrieves the tile element at the specified world coordinates as a {@code Point} object.
     *
     * @param point the {@code Point} object representing the world coordinates
     * @return the {@code TileEntity} at the specified point, or {@code null} if not found
     */
    public TileEntity getElementAt(Point point) {
        int tileX = Math.floorDiv((int) point.getX(), (int) MapConfig.scaledTileSize);
        int tileY = Math.floorDiv((int) point.getY(), (int) MapConfig.scaledTileSize);

        int chunkX = Math.floorDiv(tileX, MapConfig.chunkWidth);
        int chunkY = Math.floorDiv(tileY, MapConfig.chunkHeight);

        int localX = Math.floorMod(tileY, MapConfig.chunkWidth);
        int localY = Math.floorMod(tileX, MapConfig.chunkHeight);

        Chunk chunk = chunks.get(new Tuple<>(chunkX, chunkY));
        if (chunk != null) {
            return chunk.getElement(localX, localY);
        }
        return null;
    }

    /**
     * Retrieves the tile element at the specified world coordinates as a {@code Point} object, inside the cache.
     *
     * @param point the {@code Point} object representing the world coordinates
     * @return the {@code TileEntity} at the specified point, or {@code null} if not found
     */
    public TileEntity getElementAtSaved(Point point) {
        int tileX = Math.floorDiv((int) point.getX(), (int) MapConfig.scaledTileSize);
        int tileY = Math.floorDiv((int) point.getY(), (int) MapConfig.scaledTileSize);

        int chunkX = Math.floorDiv(tileX, MapConfig.chunkWidth);
        int chunkY = Math.floorDiv(tileY, MapConfig.chunkHeight);

        int localX = Math.floorMod(tileY, MapConfig.chunkWidth);
        int localY = Math.floorMod(tileX, MapConfig.chunkHeight);

        Chunk chunk = savedChunks.get(new Tuple<>(chunkX, chunkY));
        if (chunk != null) {
            return chunk.getElement(localX, localY);
        }
        return null;
    }

    /**
     * Sets the tile element at the specified world coordinates as a {@code Point} object.
     *
     * @param point the {@code Point} object representing the world coordinates
     * @param value the value of the tile saved
     */
    public void setElementAt(Point point, int value) {
        int tileX = Math.floorDiv((int) point.getX(), (int) MapConfig.scaledTileSize);
        int tileY = Math.floorDiv((int) point.getY(), (int) MapConfig.scaledTileSize);

        int chunkX = Math.floorDiv(tileX, MapConfig.chunkWidth);
        int chunkY = Math.floorDiv(tileY, MapConfig.chunkHeight);

        int localX = Math.floorMod(tileY, MapConfig.chunkWidth);
        int localY = Math.floorMod(tileX, MapConfig.chunkHeight);

        Chunk chunk = chunks.get(new Tuple<>(chunkX, chunkY));
        if (chunk != null) {
            chunk.setElement(localX, localY, value);
        }
    }

    /**
     * Sets the tile element at the specified world coordinates as a {@code Point} object, inside the cache.
     *
     * @param point the {@code Point} object representing the world coordinates
     * @param value the value of the tile saved
     */
    public void setElementAtSaved(Point point, int value) {
        int tileX = Math.floorDiv((int) point.getX(), (int) MapConfig.scaledTileSize);
        int tileY = Math.floorDiv((int) point.getY(), (int) MapConfig.scaledTileSize);

        int chunkX = Math.floorDiv(tileX, MapConfig.chunkWidth);
        int chunkY = Math.floorDiv(tileY, MapConfig.chunkHeight);

        int localX = Math.floorMod(tileY, MapConfig.chunkWidth);
        int localY = Math.floorMod(tileX, MapConfig.chunkHeight);

        Chunk savedChunk = savedChunks.get(new Tuple<>(chunkX, chunkY));
        if (savedChunk != null) {
            savedChunk.setElement(localX, localY, value);
        }
    }
}
