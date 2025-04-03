package inf.elte.hu.gameengine_javafx.Systems.ResourceSystems;

import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.ResourceHub;
import inf.elte.hu.gameengine_javafx.Misc.BackgroundMusic;
import inf.elte.hu.gameengine_javafx.Misc.BackgroundMusicStore;
import inf.elte.hu.gameengine_javafx.Misc.Config;

import javax.sound.sampled.*;
import java.util.List;
import java.util.Random;

public class BackgroundMusicSystem extends GameSystem {
    private Clip currentClip;
    private final Random random = new Random();

    @Override
    public void start() {
        this.active = true;
        playRandomMusic(); // Start playing music
    }

    @Override
    protected void update() {
        // No need to check manually, the listener will handle track switching
    }

    private void playRandomMusic() {
        BackgroundMusicStore store = BackgroundMusicStore.getInstance();
        if (store == null || store.getBackgroundMusics().isEmpty()) return;

        List<BackgroundMusic> musicList = store.getBackgroundMusics();
        BackgroundMusic selectedMusic = musicList.get(random.nextInt(musicList.size()));
        String path = selectedMusic.getPath();

        Clip clip = ResourceHub.getInstance().getResourceManager(Clip.class).get(path);
        if (clip == null) {
            System.err.println("Background music not found: " + path);
            return;
        }

        if (!clip.isOpen()) {
            try {
                clip.open();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        if (currentClip != null) {
            currentClip.stop();
            currentClip.close(); // Free resources
        }

        currentClip = clip;
        setVolume(currentClip, Config.backgroundMusicVolume);

        // Add a listener to detect when the clip finishes playing
        currentClip.addLineListener(event -> {
            if (event.getType() == LineEvent.Type.STOP) {
                currentClip.close();
                playRandomMusic(); // Play the next song
            }
        });

        currentClip.start();
    }

    private void setVolume(Clip clip, float volume) {
        try {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float minGain = gainControl.getMinimum();
            float maxGain = gainControl.getMaximum();
            float gain = minGain + (maxGain - minGain) * volume;
            gainControl.setValue(gain);
        } catch (Exception e) {
            System.err.println("Failed to set volume: " + e.getMessage());
        }
    }
}
