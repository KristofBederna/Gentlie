package inf.elte.hu.gameengine_javafx.Systems.RenderingSystems;

import inf.elte.hu.gameengine_javafx.Components.Default.ParentComponent;
import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.MaxDistanceFromOriginComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.AccelerationComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.DirectionComponent;
import inf.elte.hu.gameengine_javafx.Components.TimeComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Entities.ParticleEmitterEntity;
import inf.elte.hu.gameengine_javafx.Entities.ParticleEntity;
import inf.elte.hu.gameengine_javafx.Maths.Vector;
import inf.elte.hu.gameengine_javafx.Misc.Direction;
import inf.elte.hu.gameengine_javafx.Misc.Time;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class ParticleSystem extends GameSystem {
    @Override
    public void start() {
        this.active = true;
    }

    @Override
    public void update() {
        var emitters = EntityHub.getInstance().getEntitiesWithType(ParticleEmitterEntity.class);
        var emittersToRemove = new HashSet<ParticleEmitterEntity>();

        for (Entity entity : emitters) {
            processEmitter(entity, emittersToRemove);
        }
        for (Entity entity : emittersToRemove) {
            EntityHub.getInstance().removeEntity(entity);
        }
    }

    private void processEmitter(Entity entity, HashSet<ParticleEmitterEntity> emittersToRemove) {
        ParentComponent parent = entity.getComponent(ParentComponent.class);
        DirectionComponent directionComponent = entity.getComponent(DirectionComponent.class);
        Direction direction = directionComponent.getDirection();

        if (parent == null) {
            return;
        }

        spawnNewParticles(entity);

        Set<Entity> toBeRemoved = new HashSet<>();
        for (Entity particle : parent.getChildren()) {
            processParticle(entity, emittersToRemove, particle, direction, toBeRemoved);
        }

        removeDeadParticles(entity, toBeRemoved);
    }

    private void removeDeadParticles(Entity entity, Set<Entity> toBeRemoved) {
        entity.getComponent(ParentComponent.class).removeChildren(toBeRemoved);
        for (Entity particle : toBeRemoved) {
            particle.getComponent(ParentComponent.class).setParent(null);
            EntityHub.getInstance().removeEntity(particle);
        }
    }

    private void processParticle(Entity entity, HashSet<ParticleEmitterEntity> emittersToRemove, Entity particle, Direction direction, Set<Entity> toBeRemoved) {
        ParticleEntity particleEntity = (ParticleEntity) particle;
        AccelerationComponent acceleration = particleEntity.getComponent(AccelerationComponent.class);
        PositionComponent position = particleEntity.getComponent(PositionComponent.class);

        if (acceleration == null || position == null) return;

        if (acceleration.getAcceleration().isZero()) {
            Vector initial = initializeParticleAcceleration(direction);
            Vector boosted = applyVelocityBoost(initial, direction);

            if (boosted.isZero()) {
                toBeRemoved.add(particleEntity);
                return;
            }

            acceleration.setAcceleration(boosted);
        }

        handleLifecycleChecks(entity, particleEntity, emittersToRemove, toBeRemoved);
    }

    private Vector initializeParticleAcceleration(Direction direction) {
        Random random = new Random();
        double deltaTime = Time.getInstance().getDeltaTime();
        double minSpeed = -1;
        double maxSpeed = 1;

        double dx = 0;
        double dy = 0;

        switch (direction) {
            case UP -> {
                dx = random.nextDouble(minSpeed, maxSpeed);
                dy = minSpeed;
            }
            case DOWN -> {
                dx = random.nextDouble(minSpeed, maxSpeed);
                dy = maxSpeed;
            }
            case LEFT -> {
                dx = minSpeed;
                dy = random.nextDouble(minSpeed, maxSpeed);
            }
            case RIGHT -> {
                dx = maxSpeed;
                dy = random.nextDouble(minSpeed, maxSpeed);
            }
        }

        return new Vector(dx * deltaTime, dy * deltaTime);
    }

    private Vector applyVelocityBoost(Vector base, Direction direction) {
        Random random = new Random();
        double angle = switch (direction) {
            case LEFT ->
                // 135° to 225° (3π/4 to 5π/4)
                    random.nextDouble(3 * Math.PI / 4, 5 * Math.PI / 4);
            case RIGHT ->
                // -45° to 45° (-π/4 to π/4)
                    random.nextDouble(-Math.PI / 4, Math.PI / 4);
            case UP ->
                // 225° to 315° (5π/4 to 7π/4)
                    random.nextDouble(5 * Math.PI / 4, 7 * Math.PI / 4);
            case DOWN ->
                // 45° to 135° (π/4 to 3π/4)
                    random.nextDouble(Math.PI / 4, 3 * Math.PI / 4);
            default -> random.nextDouble(0, Math.PI * 2);
        };


        double boostMagnitude = random.nextDouble(0, 20);
        double boostDx = Math.cos(angle) * boostMagnitude;
        double boostDy = Math.sin(angle) * boostMagnitude;

        return new Vector(base.getDx() + boostDx, base.getDy() + boostDy);
    }

    private void handleLifecycleChecks(Entity emitter, ParticleEntity particle, Set<ParticleEmitterEntity> emittersToRemove, Set<Entity> toBeRemoved) {
        MaxDistanceFromOriginComponent distComp = particle.getComponent(MaxDistanceFromOriginComponent.class);
        if (distComp != null && distComp.isOverMaxDistance(particle)) {
            toBeRemoved.add(particle);
        }

        TimeComponent timeComp = emitter.getComponent(TimeComponent.class);
        if (timeComp != null && timeComp.getTimeBetweenOccurrences() == Integer.MAX_VALUE) {
            if (timeComp.getLastOccurrence() <= System.currentTimeMillis() - 1000) {
                emittersToRemove.add((ParticleEmitterEntity) emitter);
            }
        }
    }

    private void spawnNewParticles(Entity entity) {
        if (System.currentTimeMillis() >= entity.getComponent(TimeComponent.class).getLastOccurrence() + entity.getComponent(TimeComponent.class).getTimeBetweenOccurrences()) {
            ((ParticleEmitterEntity) entity).createParticles(
                    ((ParticleEmitterEntity) entity).getMockParticle(),
                    ((ParticleEmitterEntity) entity).getAmount(),
                    entity.getComponent(ParentComponent.class)
            );

            entity.getComponent(TimeComponent.class).setLastOccurrence();
        }

    }
}
