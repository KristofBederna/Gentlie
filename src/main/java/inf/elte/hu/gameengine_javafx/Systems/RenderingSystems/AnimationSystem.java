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

/**
 * The AnimationSystem is responsible for updating the animation state of entities within the game.
 * It checks if entities have the necessary components (such as animation state machine, image, and position)
 * and updates the animation frames accordingly. If an animation consists of a single frame, the entity is treated
 * as a static image, and the animation component is removed.
 */
public class AnimationSystem extends GameSystem {

    /**
     * Initializes the system by setting the active status to true.
     */
    @Override
    public void start() {
        this.active = true;
    }

    /**
     * Updates the animation state of all entities that have both an AnimationStateMachineComponent and an ImageComponent.
     * For entities with only one animation frame, the animation component is removed as it becomes a static image.
     */
    @Override
    public void update() {
        var entitiesSnapshot = new ArrayList<>(EntityHub.getInstance().getEntitiesWithComponent(AnimationStateMachineComponent.class));
        entitiesSnapshot.retainAll(EntityHub.getInstance().getEntitiesWithComponent(ImageComponent.class));
        List<Entity> toRemove = new ArrayList<>();

        if (entitiesSnapshot.isEmpty()) {
            return;
        }
        for (Entity entity : entitiesSnapshot) {
            if (entity == null) {
                continue;
            }
            processEntity(entity, toRemove);
        }
        for (Entity entity : toRemove) {
            if (entity == null) {
                continue;
            }
            entity.removeComponentsByType(AnimationComponent.class);
        }
    }

    /**
     * Processes the animation update for a specific entity.
     * This includes updating the entity's animation state and changing its image frame if the animation has multiple frames.
     * If the animation consists of only one frame, the entity is marked for removal of its AnimationComponent.
     *
     * @param entity   The entity to process.
     * @param toRemove A list to store entities that should have their AnimationComponent removed.
     */
    private void processEntity(Entity entity, List<Entity> toRemove) {
        if (entity == null) return;

        // Get the components related to animation
        PositionComponent position = entity.getComponent(PositionComponent.class);
        ImageComponent img = entity.getComponent(ImageComponent.class);
        AnimationStateMachineComponent stateMachineComp = entity.getComponent(AnimationStateMachineComponent.class);

        // Update the animation state if the state machine component exists
        if (stateMachineComp != null) {
            stateMachineComp.getAnimationStateMachine().setAnimationState();
        }

        AnimationComponent animation = entity.getComponent(AnimationComponent.class);

        // If position, image, and animation components exist, update the frame for the entity
        if (position != null && img != null && animation != null) {
            String framePath = animation.getController().getNextFrame();
            img.setNextFrame(framePath);

            // If the animation has only one frame, treat it as a static image and remove the animation component
            if (animation.getController().getFrames().size() == 1) {
                toRemove.add(entity);
            }
        }
    }
}
