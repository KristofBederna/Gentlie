package inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.SoundSystems;

import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.ResourceHub;
import inf.elte.hu.gameengine_javafx.Misc.Configs.ResourceConfig;
import inf.elte.hu.gameengine_javafx.Misc.Sound.BackgroundMusic;
import inf.elte.hu.gameengine_javafx.Misc.Sound.BackgroundMusicStore;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class BackgroundMusicSystem extends GameSystem {
    private Clip currentClip;
    private LineListener currentListener;
    private BackgroundMusic lastPlayed;
    private final Random random = new Random();
    private boolean shuffleMode = true;
    private boolean preventImmediateRepeats = true;
    private List<BackgroundMusic> playQueue = new ArrayList<>();
    private final Object playbackLock = new Object();

    @Override
    public void start() {
        this.active = true;
    }

    @Override
    protected void update() {
        synchronized (playbackLock) {
            if (currentClip != null && currentClip.isOpen()) {
                setVolume(currentClip, ResourceConfig.backgroundMusicVolume * ResourceConfig.masterVolume);
            }

            if (shouldPlayNewTrack()) {
                playNextMusic();
            }
        }
    }

    private boolean shouldPlayNewTrack() {
        return currentClip == null || !currentClip.isOpen() || !currentClip.isActive();
    }

    private void playNextMusic() {
        synchronized (playbackLock) {
            BackgroundMusicStore store = BackgroundMusicStore.getInstance();
            if (store == null || store.getBackgroundMusics().isEmpty()) return;

            List<BackgroundMusic> availableMusic = getAvailableMusicList(store);
            if (availableMusic.isEmpty()) {
                availableMusic = new ArrayList<>(store.getBackgroundMusics());
            }

            BackgroundMusic selectedMusic = selectMusic(availableMusic);
            playMusic(selectedMusic);
        }
    }

    private List<BackgroundMusic> getAvailableMusicList(BackgroundMusicStore store) {
        List<BackgroundMusic> allMusic = store.getBackgroundMusics();

        if (preventImmediateRepeats && lastPlayed != null) {
            return allMusic.stream()
                    .filter(music -> !music.equals(lastPlayed))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>(allMusic);
    }

    private BackgroundMusic selectMusic(List<BackgroundMusic> availableMusic) {
        if (shuffleMode) {
            return availableMusic.get(random.nextInt(availableMusic.size()));
        } else {
            if (playQueue.isEmpty()) {
                playQueue.addAll(availableMusic);
            }
            return playQueue.removeFirst();
        }
    }

    private void playMusic(BackgroundMusic music) {
        synchronized (playbackLock) {
            try { Thread.sleep(30); }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); }

            String path = music.getPath();
            Clip clip = ResourceHub.getInstance().getResourceManager(Clip.class).get(path);

            if (!isValidClip(clip)) {
                System.err.println("Invalid clip: " + path);
                return;
            }

            cleanupCurrentClip();

            try {
                currentClip = clip;
                lastPlayed = music;
                setVolume(currentClip, ResourceConfig.backgroundMusicVolume * ResourceConfig.masterVolume);
                currentClip.setFramePosition(0);

                currentListener = event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        synchronized (playbackLock) {
                            if (currentClip != null &&
                                    event.getFramePosition() >= currentClip.getFrameLength() - 1) {
                                cleanupCurrentClip();
                                if (this.active) {
                                    playNextMusic();
                                }
                            }
                        }
                    }
                };

                currentClip.addLineListener(currentListener);
                currentClip.start();
            } catch (Exception e) {
                System.err.println("Error playing music: " + e.getMessage());
                cleanupCurrentClip();
            }
        }
    }

    private void cleanupCurrentClip() {
        synchronized (playbackLock) {
            if (currentClip != null) {
                try {
                    if (currentListener != null) {
                        currentClip.removeLineListener(currentListener);
                        currentListener = null;
                    }

                    if (currentClip.isRunning()) {
                        currentClip.stop();
                    }
                    currentClip.setFramePosition(0);
                    currentClip.close();
                } catch (Exception e) {
                    System.err.println("Error cleaning up clip: " + e.getMessage());
                } finally {
                    currentClip = null;
                }
            }
        }
    }

    private boolean isValidClip(Clip clip) {
        return clip != null && clip.isOpen() && clip.getBufferSize() > 0;
    }

    private void setVolume(Clip clip, float volume) {
        if (!isValidClip(clip)) return;

        try {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            if (ResourceConfig.linearVolumeControl) {
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

    // Public control methods
    public void setShuffleMode(boolean shuffle) {
        synchronized (playbackLock) {
            this.shuffleMode = shuffle;
            if (!shuffle) playQueue.clear();
        }
    }

    public void setPreventImmediateRepeats(boolean prevent) {
        synchronized (playbackLock) {
            this.preventImmediateRepeats = prevent;
        }
    }

    public void stopMusic() {
        synchronized (playbackLock) {
            cleanupCurrentClip();
        }
    }

    public void pauseMusic() {
        synchronized (playbackLock) {
            if (isValidClip(currentClip) && currentClip.isRunning()) {
                currentClip.stop();
            }
        }
    }

    public void resumeMusic() {
        synchronized (playbackLock) {
            if (isValidClip(currentClip) && !currentClip.isRunning()) {
                currentClip.start();
            }
        }
    }

    public boolean isPlaying() {
        synchronized (playbackLock) {
            return isValidClip(currentClip) && currentClip.isRunning();
        }
    }
}