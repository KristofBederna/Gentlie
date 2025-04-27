package inf.elte.hu.gameengine_javafx.Misc.Animation;

import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;

public abstract class AnimationStateMachine {
    protected Entity entity;
    protected String lastState;

    public AnimationStateMachine(Entity entity) {
        this.entity = entity;
    }

    public abstract void setAnimationState();
}
