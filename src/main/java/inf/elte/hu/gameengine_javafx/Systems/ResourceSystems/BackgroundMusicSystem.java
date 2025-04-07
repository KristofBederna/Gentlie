package inf.elte.hu.gameengine_javafx.Systems.ResourceSystems;

import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.ResourceHub;
import inf.elte.hu.gameengine_javafx.Misc.BackgroundMusic;
import inf.elte.hu.gameengine_javafx.Misc.BackgroundMusicStore;
import inf.elte.hu.gameengine_javafx.Misc.Config;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Random;

public class BackgroundMusicSystem extends GameSystem {
    private Clip currentClip;
    private final Random random = new Random();

    @Override
    public void start() {
        this.active = true;
    }

    @Override
    protected void update() {
        if (currentClip != null) {
            setVolume(currentClip, Config.backgroundMusicVolume * Config.masterVolume);
        }

        if (currentClip == null || !currentClip.isOpen() || !currentClip.isActive()) {
            playRandomMusic();
        }
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
                InputStream resource = getClass().getResourceAsStream(path);
                if (resource == null) {
                    System.err.println("Audio file not found at path: " + path);
                    return;
                }
                resource = new BufferedInputStream(resource);
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(resource);

                clip.open(audioStream);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        if (currentClip != null && currentClip.isRunning()) {
            currentClip.stop();
        }

        currentClip = clip;
        setVolume(currentClip, Config.backgroundMusicVolume * Config.masterVolume);

        currentClip.addLineListener(event -> {
            if (event.getType() == LineEvent.Type.STOP) {
                try {
                    currentClip.close();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    currentClip = null;
                    playRandomMusic();
                }
            }
        });

        currentClip.start();
    }

    private void setVolume(Clip clip, float volume) {
        if (clip == null || !clip.isOpen()) return;

        try {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

            if (Config.linearVolumeControl) {
                float minGain = gainControl.getMinimum();
                float maxGain = gainControl.getMaximum();
                float gain = minGain + (maxGain - minGain) * volume;
                gainControl.setValue(gain);
            } else {
                float dB = (float) (Math.log10(Math.max(volume, 0.0001f)) * 20);
                gainControl.setValue(dB);
            }

        } catch (Exception e) {
            System.err.println("Failed to set volume: " + e.getMessage());
        }
    }
}
