package inf.elte.hu.gameengine_javafx.Misc.StartUpClasses;

import inf.elte.hu.gameengine_javafx.Core.ResourceHub;
import inf.elte.hu.gameengine_javafx.Core.ResourceManagers.ClipResourceManager;
import inf.elte.hu.gameengine_javafx.Core.ResourceManagers.ImageResourceManager;
import javafx.scene.image.Image;

import javax.sound.sampled.Clip;

public class ResourceStartUp {
    public ResourceStartUp() {
        ResourceHub.getInstance();
        startUpResourceManagers();
    }

    public void startUpResourceManagers() {
        //Define resourceManagers here
        ResourceHub.getInstance().addResourceManager(Image.class, new ImageResourceManager());
        ResourceHub.getInstance().addResourceManager(Clip.class, new ClipResourceManager());
    }
}
