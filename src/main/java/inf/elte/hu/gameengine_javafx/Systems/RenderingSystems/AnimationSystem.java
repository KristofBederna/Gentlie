package inf.elte.hu.gameengine_javafx.Systems.RenderingSystems;


import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.RenderingComponents.AnimationComponent;
import inf.elte.hu.gameengine_javafx.Components.RenderingComponents.AnimationStateMachineComponent;
import inf.elte.hu.gameengine_javafx.Components.RenderingComponents.ImageComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;

import java.util.ArrayList;
import java.util.List;

public class AnimationSystem extends GameSystem {
    @Override
    public void start() {
        this.active = true;
    }

    @Override
    public void update() {
        var entitiesSnapshot = new ArrayList<>(EntityHub.getInstance().getEntitiesWithComponent(AnimationStateMachineComponent.class));
        entitiesSnapshot.retainAll(EntityHub.getInstance().getEntitiesWithComponent(ImageComponent.class));
        List<Entity> toRemove = new ArrayList<>();

        if (entitiesSnapshot.isEmpty()) {
            return;
        }
        for (Entity entity : entitiesSnapshot) {
            processEntity(entity, toRemove);
        }
        for (Entity entity : toRemove) {
            entity.removeComponentsByType(AnimationComponent.class);
        }
    }

    private void processEntity(Entity entity, List<Entity> toRemove) {
        if (entity == null) return;
        PositionComponent position = entity.getComponent(PositionComponent.class);
        ImageComponent img = entity.getComponent(ImageComponent.class);
        AnimationStateMachineComponent stateMachineComp = entity.getComponent(AnimationStateMachineComponent.class);

        if (stateMachineComp != null) {
            stateMachineComp.getAnimationStateMachine().setAnimationState();
        }

        AnimationComponent animation = entity.getComponent(AnimationComponent.class);

        if (position != null && img != null && animation != null) {
            String framePath = animation.getController().getNextFrame();
            img.setNextFrame(framePath);
            //Since animations need multiple frames an animation with just 1 frame counts as an Image, which the Image component can handle just fine alone
            if (animation.getController().getFrames().size() == 1) {
                toRemove.add(entity);
            }
        }
    }
}
