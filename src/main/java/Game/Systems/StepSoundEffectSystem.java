package Game.Systems;

import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.StateComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Misc.Sound.SoundEffect;
import inf.elte.hu.gameengine_javafx.Misc.Sound.SoundEffectStore;

import java.util.Objects;

public class StepSoundEffectSystem extends GameSystem {
    @Override
    public void start() {
        this.active = true;
    }

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

    private void handleMovingEntities(Entity entity) {
        SoundEffect soundEffect = new SoundEffect(entity, "/assets/sound/sfx/steps.wav", "steps_" + entity.getId(), 0.3f, 0.0f, 1000, true);
        if (SoundEffectStore.getInstance().contains(soundEffect))
            return;
        SoundEffectStore.getInstance().add(soundEffect);
    }
}
