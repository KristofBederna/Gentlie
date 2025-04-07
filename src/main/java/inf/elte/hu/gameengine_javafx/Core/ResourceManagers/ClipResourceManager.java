package inf.elte.hu.gameengine_javafx.Core.ResourceManagers;

import inf.elte.hu.gameengine_javafx.Core.ResourceManager;

import javax.sound.sampled.*;

public class ClipResourceManager extends ResourceManager<Clip> {
    public ClipResourceManager() {
        super(key -> {
            try {
                return AudioSystem.getClip();
            } catch (LineUnavailableException e) {
                System.err.println("Error creating clip for: " + key);
                return null;
            }
        });
    }
}
