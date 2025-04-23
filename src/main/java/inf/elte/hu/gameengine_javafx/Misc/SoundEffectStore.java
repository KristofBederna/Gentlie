package inf.elte.hu.gameengine_javafx.Misc;


import java.util.ArrayList;
import java.util.List;

public class SoundEffectStore {
    private static SoundEffectStore instance;
    private List<SoundEffect> soundEffects = new ArrayList<>();
    private List<SoundEffect> runningSoundEffects = new ArrayList<>();

    public static SoundEffectStore getInstance() {
        if (instance == null) instance = new SoundEffectStore();
        return instance;
    }

    public static void resetInstance() {
        instance = null;
    }

    public List<SoundEffect> getSoundEffects() {
        return soundEffects;
    }
    public void add(SoundEffect soundEffect) {
        soundEffects.add(soundEffect);
    }
    public void remove(SoundEffect soundEffect) {
        soundEffects.remove(soundEffect);
        runningSoundEffects.remove(soundEffect);
    }
    public void remove(String identifier) {
        soundEffects.removeIf(soundEffect -> identifier.equals(soundEffect.getIdentifier()));
        runningSoundEffects.removeIf(soundEffect -> identifier.equals(soundEffect.getIdentifier()));
    }

    public boolean contains(SoundEffect soundEffect) {
        return soundEffects.stream()
                .anyMatch(e -> e.getIdentifier().equals(soundEffect.getIdentifier()));
    }

    public List<SoundEffect> getRunningSoundEffects() {
        return runningSoundEffects;
    }
}
