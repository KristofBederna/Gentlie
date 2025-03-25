package inf.elte.hu.gameengine_javafx.Core.ResourceManagers;

import inf.elte.hu.gameengine_javafx.Core.ResourceManager;
import javafx.scene.image.Image;

import java.io.InputStream;

public class ImageResourceManager extends ResourceManager<Image> {
    public ImageResourceManager() {
        super(key -> {
            try {
                if (key.startsWith("file:")) {
                    return new Image(key);
                }

                InputStream resource = ImageResourceManager.class.getResourceAsStream(key);
                if (resource != null) {
                    return new Image(resource);
                }

                return new Image("file:" + key);
            } catch (Exception e) {
                System.err.println("Error loading image: " + key);
                return null;
            }
        });
    }
}
