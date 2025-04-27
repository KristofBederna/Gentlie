package inf.elte.hu.gameengine_javafx.Misc.MapClasses;

import java.util.HashMap;

/**
 * The {@code TileLoader} class manages the mapping of tile values to their corresponding file paths.
 * It allows adding, retrieving, and reverse-mapping tile values to file paths for tiles in the game.
 */
public class TileLoader {
    private static final HashMap<Integer, String> tilePaths = new HashMap<>();

    /**
     * Adds a new tile path to the map, associating the given tile value with its corresponding file path.
     *
     * @param value the value of the tile
     * @param path  the file path to the tile image
     */
    public void addTilePath(Integer value, String path) {
        tilePaths.put(value, path);
    }

    /**
     * Retrieves the file path associated with the given tile value.
     *
     * @param value the value of the tile
     * @return the file path associated with the tile value, or {@code null} if no path exists for the value
     */
    public static String getTilePath(Integer value) {
        return tilePaths.get(value);
    }

    /**
     * Retrieves the tile value associated with the given file path.
     *
     * @param path the file path of the tile
     * @return the tile value associated with the file path, or {@code null} if no value exists for the path
     */
    public Integer getTileValue(String path) {
        for (HashMap.Entry<Integer, String> entry : tilePaths.entrySet()) {
            if (entry.getValue().equals(path)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
