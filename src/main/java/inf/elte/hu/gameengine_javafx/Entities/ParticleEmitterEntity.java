package inf.elte.hu.gameengine_javafx.Entities;

import inf.elte.hu.gameengine_javafx.Components.Default.ParentComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.DirectionComponent;
import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.TimeComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Misc.Direction;

public class ParticleEmitterEntity extends Entity {
    private int amount;
    ParticleEntity mock;

    public ParticleEmitterEntity(double x, double y, ParticleEntity particleEntity, Direction direction, int amount, long timeBetweenSpawns) {
        createParticles(particleEntity, amount, this.getComponent(ParentComponent.class));
        addComponent(new DirectionComponent(direction));
        this.getComponent(PositionComponent.class).setLocalPosition(x, y, this);
        addComponent(new TimeComponent(timeBetweenSpawns));
        this.amount = amount;
        mock = particleEntity;

        addToManager();
    }

    public ParticleEmitterEntity(double x, double y, ParticleEntity particleEntity, Direction direction, int amount) {
        createParticles(particleEntity, amount, this.getComponent(ParentComponent.class));
        addComponent(new DirectionComponent(direction));
        this.getComponent(PositionComponent.class).setLocalPosition(x, y, this);
        addComponent(new TimeComponent(Integer.MAX_VALUE));
        this.amount = amount;
        mock = particleEntity;

        addToManager();
    }

    public void createParticles(ParticleEntity particleEntity, int amount, ParentComponent parent) {
        for (int i = 0; i < amount; i++) {
            ParticleEntity copy = ParticleEntity.hardCopySelf(particleEntity);
            if (copy == null) {
                continue;
            }
            copy.getComponent(ParentComponent.class).setParent(this);
            parent.addChild(copy);
            copy.getComponent(PositionComponent.class).setLocalPosition(0, 0, copy);
            copy.addToManager();
        }
    }

    public int getAmount() {
        return amount;
    }

    public ParticleEntity getMockParticle() {
        return mock;
    }
}
