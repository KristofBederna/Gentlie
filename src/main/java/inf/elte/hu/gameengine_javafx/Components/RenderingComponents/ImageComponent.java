package inf.elte.hu.gameengine_javafx.Components.RenderingComponents;

import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;
import inf.elte.hu.gameengine_javafx.Misc.Config;

public class ImageComponent extends Component {
    private String imagePath;
    private double width = -1, height = -1;

    public ImageComponent(String path) {
        this.imagePath = path;
    }

    public ImageComponent(String path, double width, double height) {
        this.imagePath = path;
        this.width = width;
        this.height = height;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setNextFrame(String nextFramePath) {
        this.imagePath = nextFramePath;
    }

    public double getHeight() {
        return height;
    }

    public double getWidth() {
        return width;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    @Override
    public String getStatus() {
        return (this.getClass().getSimpleName() + ": Image Path: " + imagePath + ", Width: " + width + ", Height: " + height);
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
        this.height = Config.tileSize;
        this.width = Config.tileSize;
    }
}
