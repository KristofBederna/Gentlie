package inf.elte.hu.gameengine_javafx.Systems.ResourceSystems;

import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Entities.CameraEntity;
import inf.elte.hu.gameengine_javafx.Misc.Configs.ResourceConfig;
import inf.elte.hu.gameengine_javafx.Misc.SoundEffect;
import inf.elte.hu.gameengine_javafx.Misc.SoundEffectStore;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SoundSystem extends GameSystem {
    private final Map<String, Clip> playingClips = new HashMap<>();

    @Override
    public void start() {
        this.active = true;
    }

    @Override
    public void update() {
        if (CameraEntity.getInstance() == null || CameraEntity.getInstance().getOwner() == null) {
            return;
        }

        Entity listenerEntity = CameraEntity.getInstance().getOwner();
        PositionComponent listenerPos = listenerEntity.getComponent(PositionComponent.class);
        if (listenerPos == null) return;

        if (SoundEffectStore.getInstance() == null) return;

        ArrayList<SoundEffect> toRemove = new ArrayList<>();

        for (SoundEffect soundEffect : SoundEffectStore.getInstance().getSoundEffects()) {
            Entity owner = soundEffect.getOwner();
            if (owner == null) {
                toRemove.add(soundEffect);
                continue;
            }
            PositionComponent entityPos = owner.getComponent(PositionComponent.class);
            if (entityPos == null) continue;

            double maxDistance = soundEffect.getMaxDistance();
            float minVolume = soundEffect.getMinVolume();
            float maxVolume = soundEffect.getMaxVolume();

            double distance = calculateDistance(listenerPos, entityPos);
            float volume = calculateVolume(distance, maxDistance, minVolume, maxVolume);

            Clip clip = playingClips.get(soundEffect.getIdentifier());

            if (!soundEffect.isAlreadyPlayed()) {
                clip = createClipForEntity(soundEffect.getPath());
                if (clip == null) {
                    continue;
                }
                setVolume(clip, volume);
                clip.setFramePosition(0);
                clip.start();
                soundEffect.setStarted();

                soundEffect.setAlreadyPlayed(true);
                SoundEffectStore.getInstance().getRunningSoundEffects().add(soundEffect);
                playingClips.put(soundEffect.getIdentifier(), clip);

            } else if (soundEffect.isAllowLooping()) {
                if (clip == null) {
                    continue;
                }
                if (!clip.isRunning()) {
                    setVolume(clip, volume);
                    clip.setFramePosition(0);
                    clip.start();
                    soundEffect.setStarted();
                }
            } else {
                if (clip == null) {
                    continue;
                }
                if (System.currentTimeMillis() - (clip.getMicrosecondLength() / 1000) - 20 > soundEffect.getStarted()) {
                    clip.stop();
                    toRemove.add(soundEffect);
                }
            }
        }
        for (SoundEffect soundEffect : toRemove) {
            Clip clip = playingClips.get(soundEffect.getIdentifier());
            if (clip != null && clip.isRunning()) {
                clip.stop();
                clip.close();
            }
            playingClips.remove(soundEffect.getIdentifier());
            SoundEffectStore.getInstance().remove(soundEffect.getIdentifier());
        }
    }

    private double calculateDistance(PositionComponent a, PositionComponent b) {
        double dx = b.getGlobalX() - a.getGlobalX();
        double dy = b.getGlobalY() - a.getGlobalY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    private float calculateVolume(double distance, double maxDistance, float minVolume, float maxVolume) {
        if (maxDistance == 0) return maxVolume;
        if (distance >= maxDistance) return minVolume;

        float fade = (float) Math.exp(-distance / (0.5 * maxDistance));
        return Math.max(minVolume, maxVolume * fade);
    }


    private void setVolume(Clip clip, float volume) {
        try {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            if (ResourceConfig.linearVolumeControl) {
                float minGain = gainControl.getMinimum();
                float maxGain = gainControl.getMaximum();
                float gain = minGain + (maxGain - minGain) * (volume * ResourceConfig.masterVolume);
                gainControl.setValue(gain);
            } else {
                float dB = (float) (Math.log10(Math.max(volume * ResourceConfig.masterVolume, 0.0001f)) * 20);
                gainControl.setValue(dB);
            }
        } catch (Exception e) {
            System.err.println("Failed to set volume: " + e.getMessage());
        }
    }

    private Clip createClipForEntity(String path) {
        try {
            InputStream inputStream = SoundSystem.class.getResourceAsStream(path);

            if (!(inputStream instanceof BufferedInputStream)) {
                inputStream = new BufferedInputStream(inputStream);
            }

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputStream);

            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);

            return clip;
        } catch (Exception e) {
            System.err.println("Failed to load audio stream: " + e.getMessage());
            return null;
        }
    }

    public void stopAllTracks() {
        for (Clip clip : playingClips.values()) {
            clip.stop();
        }
    }
}