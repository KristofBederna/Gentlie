package Game.Components;

import inf.elte.hu.gameengine_javafx.Components.UIComponents.UIComponent;
import inf.elte.hu.gameengine_javafx.Core.ResourceHub;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class ImageViewComponent extends UIComponent<StackPane> {
    public ImageViewComponent(double x, double y, double width, double height, String path) {
        super(x, y, width, height);

        StackPane imagePane = new StackPane();
        imagePane.setLayoutX(this.x);
        imagePane.setLayoutY(this.y);
        ImageView imageView = new ImageView();
        imageView.setFitWidth(this.width);
        imageView.setFitHeight(this.height);
        imageView.setImage(ResourceHub.getInstance().getResourceManager(Image.class).get(path));
        imagePane.getChildren().add(imageView);

        this.uiElement = imagePane;
    }

    @Override
    public String getStatus() {
        return "";
    }
}
