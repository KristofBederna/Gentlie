package inf.elte.hu.gameengine_javafx.Components.WorldComponents;

import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;
import inf.elte.hu.gameengine_javafx.Misc.MapClasses.MapSaver;
import inf.elte.hu.gameengine_javafx.Misc.MapClasses.TileLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class TileSetComponent extends Component {
    private final TileLoader tileLoader;

    public TileSetComponent(String tileSetPath) {
        this.tileLoader = new TileLoader();
        loadSet(tileSetPath, tileLoader);
    }

    public TileSetComponent(String tileSetPath, String separator) {
        this.tileLoader = new TileLoader();
        loadSet(tileSetPath, tileLoader, separator);
    }

    public static void loadSet(String path, TileLoader tileLoader) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(MapSaver.class.getResourceAsStream(path))))) {

            String line;
            while ((line = reader.readLine()) != null) {
                processTileLoading(line, " ", tileLoader);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void loadSet(String path, TileLoader tileLoader, String separator) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(MapSaver.class.getResourceAsStream(path))))) {

            String line;
            while ((line = reader.readLine()) != null) {
                processTileLoading(line, separator, tileLoader);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void processTileLoading(String line, String separator, TileLoader tileLoader) {
        String[] row = line.split(separator);
        int value = Integer.parseInt(row[0]);
        String tilePath = row[1];

        tileLoader.addTilePath(value, tilePath);
    }

    public TileLoader getTileLoader() {
        return tileLoader;
    }
}
