package inf.elte.hu.gameengine_javafx.Components.UIComponents;

import javafx.scene.control.ProgressBar;

public class ProgressBarComponent extends UIComponent<ProgressBar> {

    public ProgressBarComponent(double x, double y, double width, double height, double initialValue) {
        super(x, y, width, height);
        this.uiElement = new ProgressBar(initialValue);
        uiElement.setLayoutX(x);
        uiElement.setLayoutY(y);
        uiElement.setPrefSize(width, height);
    }

    public void setProgress(double progress) {
        uiElement.setProgress(progress);
    }
}
