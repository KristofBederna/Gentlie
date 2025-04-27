package inf.elte.hu.gameengine_javafx.Misc.Sound;

public class BackgroundMusic {
    private final String path;
    private final String identifier;
    private final float maxVolume;
    private final float minVolume;
    private final boolean allowLooping;
    private boolean alreadyPlayed;

    public BackgroundMusic(String path, String identifier, float maxVolume, float minVolume, boolean allowLooping) {
        this.path = path;
        this.identifier = identifier;
        this.maxVolume = maxVolume;
        this.minVolume = minVolume;
        this.allowLooping = allowLooping;
        this.alreadyPlayed = false;

        BackgroundMusicStore.getInstance().add(this);
    }


    public String getPath() {
        return path;
    }

    public String getIdentifier() {
        return identifier;
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
}
