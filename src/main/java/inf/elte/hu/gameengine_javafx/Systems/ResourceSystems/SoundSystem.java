package inf.elte.hu.gameengine_javafx.Systems.ResourceSystems;

import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.ResourceHub;
import inf.elte.hu.gameengine_javafx.Entities.CameraEntity;
import inf.elte.hu.gameengine_javafx.Misc.Configs.ResourceConfig;
import inf.elte.hu.gameengine_javafx.Misc.SoundEffect;
import inf.elte.hu.gameengine_javafx.Misc.SoundEffectStore;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class SoundSystem extends GameSystem {
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

        SoundEffectStore soundStore = SoundEffectStore.getInstance();

        if (soundStore != null) {
            for (SoundEffect soundEffect : soundStore.getSoundEffects()) {
                Entity owner = soundEffect.getOwner();
                PositionComponent entityPos = owner.getComponent(PositionComponent.class);
                double maxDistance = soundEffect.getMaxDistance();
                float minVolume = soundEffect.getMinVolume();
                float maxVolume = soundEffect.getMaxVolume();

                double distance = calculateDistance(listenerPos, entityPos);
                float volume = calculateVolume(distance, maxDistance, minVolume, maxVolume);

                Clip clip = ResourceHub.getInstance().getResourceManager(Clip.class).get(soundEffect.getPath());

                if (clip == null) {
                    System.err.println("Clip not found for: " + soundEffect.getPath());
                    continue;
                }

                if (!clip.isOpen()) {
                    try {
                        clip.open();
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                }

                if (!soundEffect.isAlreadyPlayed()) {
                    setVolume(clip, volume);
                    playSound(clip, volume);
                    soundEffect.setAlreadyPlayed(true);
                } else if (soundEffect.isAllowLooping()) {
                    if (!clip.isRunning()) {
                        setVolume(clip, volume);
                        playSound(clip, volume);
                    }
                }
            }
        }
    }


    private double calculateDistance(PositionComponent a, PositionComponent b) {
        double dx = b.getGlobalX() - a.getGlobalX();
        double dy = b.getGlobalY() - a.getGlobalY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    private float calculateVolume(double distance, double maxDistance, float minVolume, float maxVolume) {
        if (maxDistance == 0) return 0f;
        if (distance > maxDistance) return minVolume;
        return (float) (maxVolume / (1 + (distance / maxDistance) * (distance / maxDistance)));
    }

    private void setVolume(Clip clip, float volume) {
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

    private void playSound(Clip clip, float volume) {
        if (volume == 0) return;
        if (!clip.isRunning()) {
            clip.setFramePosition(0);
            clip.start();
        }
    }
}