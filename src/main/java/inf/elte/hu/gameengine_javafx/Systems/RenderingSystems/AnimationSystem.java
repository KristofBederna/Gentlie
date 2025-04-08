package inf.elte.hu.gameengine_javafx.Systems.RenderingSystems;


import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.RenderingComponents.AnimationComponent;
import inf.elte.hu.gameengine_javafx.Components.RenderingComponents.AnimationStateMachineComponent;
import inf.elte.hu.gameengine_javafx.Components.RenderingComponents.ImageComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;

import java.util.ArrayList;

public class AnimationSystem extends GameSystem {
    @Override
    public void start() {
        this.active = true;
    }

    @Override
    public void update() {
        var entitiesSnapshot = new ArrayList<>(EntityHub.getInstance().getEntitiesWithComponent(AnimationStateMachineComponent.class));
        entitiesSnapshot.retainAll(EntityHub.getInstance().getEntitiesWithComponent(ImageComponent.class));

        if (entitiesSnapshot.isEmpty()) {
            return;
        }
        for (Entity entity : entitiesSnapshot) {
            if (entity == null) continue;
            PositionComponent position = entity.getComponent(PositionComponent.class);
            ImageComponent img = entity.getComponent(ImageComponent.class);
            entity.getComponent(AnimationStateMachineComponent.class).getAnimationStateMachine().setAnimationState();
            AnimationComponent animation = entity.getComponent(AnimationComponent.class);

            if (position != null && img != null && animation != null) {
                img.setNextFrame(animation.getNextFrame());
            }
        }
    }
}
