package inf.elte.hu.gameengine_javafx.Components.RenderingComponents;

import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;
import inf.elte.hu.gameengine_javafx.Misc.AnimationController;
import inf.elte.hu.gameengine_javafx.Misc.AnimationFrame;

import java.util.List;

public class AnimationComponent extends Component {
    private AnimationController controller;

    public AnimationComponent(List<AnimationFrame> frames) {
        this.controller = new AnimationController(frames);
    }

    public AnimationController getController() {
        return controller;
    }

    public void setFrames(List<AnimationFrame> frames) {
        this.controller.setFrames(frames);
    }

    @Override
    public String getStatus() {
        return "";
    }
}
