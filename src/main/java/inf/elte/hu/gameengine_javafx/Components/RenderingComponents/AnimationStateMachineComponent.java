package inf.elte.hu.gameengine_javafx.Components.RenderingComponents;

import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;
import inf.elte.hu.gameengine_javafx.Misc.Animation.AnimationStateMachine;

public class AnimationStateMachineComponent extends Component {
    AnimationStateMachine animationStateMachine;

    public AnimationStateMachineComponent(AnimationStateMachine animationStateMachine) {
        this.animationStateMachine = animationStateMachine;
    }

    public AnimationStateMachine getAnimationStateMachine() {
        return animationStateMachine;
    }

    public void setAnimationStateMachine(AnimationStateMachine animationStateMachine) {
        this.animationStateMachine = animationStateMachine;
    }
}
