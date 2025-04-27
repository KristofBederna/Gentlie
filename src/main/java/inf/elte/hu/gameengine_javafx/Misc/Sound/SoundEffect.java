package inf.elte.hu.gameengine_javafx.Misc.Sound;

import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;

public class SoundEffect {
    private final Entity owner;
    private final String path;
    private final String identifier;
    private final float maxVolume;
    private final float minVolume;
    private final double maxDistance;
    private final boolean allowLooping;
    private boolean alreadyPlayed;
    private long started;

    public SoundEffect(Entity owner, String path, String identifier, float maxVolume, float minVolume, double maxDistance, boolean allowLooping) {
        this.owner = owner;
        this.path = path;
        this.identifier = identifier;
        this.maxVolume = maxVolume;
        this.minVolume = minVolume;
        this.maxDistance = maxDistance;
        this.allowLooping = allowLooping;
        this.alreadyPlayed = false;
    }

    public Entity getOwner() {
        return owner;
    }

    public String getPath() {
        return path;
    }

    public String getIdentifier() {
        return identifier;
    }

    public double getMaxDistance() {
        return maxDistance;
    }
    public float getMinVolume() {
        return minVolume;
    }
    public float getMaxVolume() {
        return maxVolume;
    }
    public boolean isAllowLooping() {
        return allowLooping;
    }
    public boolean isAlreadyPlayed() {
        return alreadyPlayed;
    }
    public void setAlreadyPlayed(boolean alreadyPlayed) {
        this.alreadyPlayed = alreadyPlayed;
    }

    public long getStarted() {
        return started;
    }

    public void setStarted() {
        started = System.currentTimeMillis();
    }
}
