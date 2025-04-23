package inf.elte.hu.gameengine_javafx.Misc;


import java.util.ArrayList;
import java.util.List;

public class BackgroundMusicStore {
    private static BackgroundMusicStore instance;
    private List<BackgroundMusic> backgroundMusics = new ArrayList<>();

    public static BackgroundMusicStore getInstance() {
        if (instance == null) instance = new BackgroundMusicStore();
        return instance;
    }

    public static void resetInstance() {
        instance = null;
    }

    public List<BackgroundMusic> getBackgroundMusics() {
        return backgroundMusics;
    }
    public void add(BackgroundMusic backgroundMusic) {
        backgroundMusics.add(backgroundMusic);
    }
    public void remove(BackgroundMusic backgroundMusic) {
        backgroundMusics.remove(backgroundMusic);
    }
    public void remove(String identifier) {
        backgroundMusics.removeIf(backgroundMusic -> identifier.equals(backgroundMusic.getIdentifier()));
    }
}
