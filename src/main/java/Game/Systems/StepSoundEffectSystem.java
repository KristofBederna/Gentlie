package Game.Systems;

import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.StateComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Misc.Sound.SoundEffect;
import inf.elte.hu.gameengine_javafx.Misc.Sound.SoundEffectStore;

import java.util.Objects;

/**
 * System that handles the sound effect of entities' footsteps while moving.
 */
public class StepSoundEffectSystem extends GameSystem {

    /**
     * Starts the StepSoundEffectSystem and sets it as active.
     */
    @Override
    public void start() {
        this.active = true;
    }

    /**
     * Updates the system by checking for entities that have a StateComponent.
     * If an entity is moving, its corresponding step sound is played.
     * If the entity is idle, the step sound is removed.
     */
    @Override
    protected void update() {
        var entities = EntityHub.getInstance().getEntitiesWithComponent(StateComponent.class);
        for (var entity : entities) {
            if (entity == null) {
                continue;
            }
            handleEntity(entity);
        }
    }

    /**
     * Handles the step sound effect for a given entity.
     * Plays or removes the sound effect depending on the entity's state.
     *
     * @param entity the entity for which the sound effect will be handled
     */
    private void handleEntity(Entity entity) {
        if (entity == null) {
            return;
        }
        if (!Objects.equals(entity.getComponent(StateComponent.class).getCurrentState(), "idle")) {
            handleMovingEntities(entity);
        } else {
            SoundEffectStore.getInstance().remove("steps_" + entity.getId());
        }
    }

    /**
     * Handles the creation and addition of step sound effects for moving entities.
     * @param entity the moving entity for which the sound effect will be created
     */
    private void handleMovingEntities(Entity entity) {
        SoundEffect soundEffect = new SoundEffect(entity, "/assets/sound/sfx/steps.wav", "steps_" + entity.getId(), 0.3f, 0.0f, 1000, true);
        if (SoundEffectStore.getInstance().contains(soundEffect))
            return;
        SoundEffectStore.getInstance().add(soundEffect);
    }
}
