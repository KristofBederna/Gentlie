package inf.elte.hu.gameengine_javafx.Core.ResourceManagers;

import inf.elte.hu.gameengine_javafx.Core.ResourceManager;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ClipResourceManager extends ResourceManager<Clip> {
    public ClipResourceManager() {
        super(key -> {
            try {
                AudioInputStream audioStream;

                InputStream resource = ClipResourceManager.class.getResourceAsStream(key);
                if (resource == null) {
                    System.err.println("Error: Sound file not found at " + key);
                    return null;
                }
                resource = new BufferedInputStream(resource);
                audioStream = AudioSystem.getAudioInputStream(resource);

                Clip clip = AudioSystem.getClip();
                try {
                    clip.open(audioStream);
                    clip.setFramePosition(0); // Initialize at start position
                    return clip;
                } catch (LineUnavailableException | IOException e) {
                    try {
                        audioStream.close();
                    } catch (IOException ex) {
                        System.err.println("Error closing stream: " + ex.getMessage());
                    }
                    throw new RuntimeException("Error opening clip: " + key, e);
                }
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                System.err.println("Error loading sound: " + key);
                return null;
            }
        });
    }
}
