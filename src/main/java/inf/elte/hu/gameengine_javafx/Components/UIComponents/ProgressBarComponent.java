package inf.elte.hu.gameengine_javafx.Components.UIComponents;

import javafx.scene.control.ProgressBar;

public class ProgressBarComponent extends UIComponent<ProgressBar> {

    public ProgressBarComponent(double x, double y, double width, double height, double initialValue) {
        super(x, y, width, height);
        this.uiElement = new ProgressBar(initialValue);
        uiElement.setLayoutX(this.x);
        uiElement.setLayoutY(this.y);
        uiElement.setPrefSize(this.width, this.height);
    }

    public void setProgress(double progress) {
        uiElement.setProgress(progress);
    }

    @Override
    public String getStatus() {
        return "";
    }
}
