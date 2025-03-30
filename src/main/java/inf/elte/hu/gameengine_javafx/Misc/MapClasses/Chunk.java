package inf.elte.hu.gameengine_javafx.Misc.MapClasses;

import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.DimensionComponent;
import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Entities.TileEntity;
import inf.elte.hu.gameengine_javafx.Misc.Config;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@code Chunk} class represents a chunk of tiles within the game world.
 * It manages a 2D list of {@code TileEntity} objects, providing methods to access and modify elements within the chunk.
 */
public class Chunk {
    private List<List<TileEntity>> chunk;

    /**
     * Constructs a {@code Chunk} with the provided list of tile entities.
     *
     * @param chunk the 2D list of {@code TileEntity} objects representing the chunk
     */
    public Chunk(List<List<TileEntity>> chunk) {
        this.chunk = chunk;
    }

    /**
     * Constructs an empty {@code Chunk} with no tiles.
     */
    public Chunk() {
        this.chunk = new ArrayList<>();
    }

    /**
     * Retrieves the 2D list of tile entities that make up the chunk.
     *
     * @return the 2D list of {@code TileEntity} objects in the chunk
     */
    public List<List<TileEntity>> getChunk() {
        return chunk;
    }

    /**
     * Sets the 2D list of tile entities that make up the chunk.
     *
     * @param chunk the 2D list of {@code TileEntity} objects to set
     */
    public void setChunk(List<List<TileEntity>> chunk) {
        this.chunk = chunk;
    }

    /**
     * Adds the specified 2D list of tile entities to the chunk.
     *
     * @param chunk the 2D list of {@code TileEntity} objects to add
     */
    public void addChunk(List<List<TileEntity>> chunk) {
        this.chunk.addAll(chunk);
    }

    /**
     * Adds the tiles from another chunk to this chunk.
     *
     * @param chunk the {@code Chunk} whose tiles are to be added to this chunk
     */
    public void addChunk(Chunk chunk) {
        this.chunk.addAll(chunk.getChunk());
    }

    /**
     * Clears all tiles in the chunk.
     */
    public void clear() {
        this.chunk.clear();
    }

    /**
     * Retrieves the list of {@code TileEntity} objects at the specified index in the chunk.
     *
     * @param number the index of the row to retrieve
     * @return the list of {@code TileEntity} objects at the specified index
     */
    public List<TileEntity> get(int number) {
        return chunk.get(number);
    }

    /**
     * Returns the number of rows in the chunk.
     *
     * @return the number of rows in the chunk
     */
    public int size() {
        return chunk.size();
    }

    /**
     * Retrieves the {@code TileEntity} at the specified coordinates (x, y) in the chunk.
     *
     * @param x the row index of the tile entity
     * @param y the column index of the tile entity
     * @return the {@code TileEntity} at the specified coordinates, or {@code null} if out of bounds
     */
    public TileEntity getElement(int x, int y) {
        return chunk.get(x).get(y);
    }

    /**
     * Sets the tile at the specified coordinates (x, y) in the chunk to a new tile with the given value.
     *
     * @param x     the row index of the tile to set
     * @param y     the column index of the tile to set
     * @param value the new value to set for the tile
     */
    public void setElement(int x, int y, int value) {
        chunk.get(x).get(y).changeValues(value);
    }
}
