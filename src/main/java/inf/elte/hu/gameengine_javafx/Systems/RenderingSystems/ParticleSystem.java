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

/**
 * The ParticleSystem is responsible for managing the lifecycle of particles in the game.
 * It handles the spawning, movement, and removal of particles emitted by ParticleEmitterEntities.
 * It also applies forces such as acceleration and boosts to particles, and checks their distance from the origin.
 */
public class ParticleSystem extends GameSystem {

    /**
     * Initializes the system by marking it as active.
     */
    @Override
    public void start() {
        this.active = true;
    }

    /**
     * Updates all particle emitters by processing each one and removing dead particles.
     * Removes any emitters that are no longer active.
     */
    @Override
    public void update() {
        var emitters = EntityHub.getInstance().getEntitiesWithType(ParticleEmitterEntity.class);
        var emittersToRemove = new HashSet<ParticleEmitterEntity>();

        for (Entity entity : emitters) {
            if (entity == null) {
                continue;
            }
            processEmitter(entity, emittersToRemove);
        }

        // Remove emitters that are no longer active
        for (Entity entity : emittersToRemove) {
            if (entity == null) {
                continue;
            }
            EntityHub.getInstance().removeEntity(entity);
        }
    }

    /**
     * Processes a single emitter by spawning new particles and managing the lifecycle of its existing particles.
     * If particles exceed the max distance or reach the end of their lifecycle, they are removed.
     *
     * @param entity           The emitter entity.
     * @param emittersToRemove A set of emitters to be removed if they are no longer active.
     */
    private void processEmitter(Entity entity, HashSet<ParticleEmitterEntity> emittersToRemove) {
        ParentComponent parent = entity.getComponent(ParentComponent.class);
        DirectionComponent directionComponent = entity.getComponent(DirectionComponent.class);
        Direction direction = directionComponent.getDirection();

        if (parent == null) {
            return;
        }

        // Spawn new particles if necessary
        spawnNewParticles(entity);

        // Process existing particles
        Set<Entity> toBeRemoved = new HashSet<>();
        for (Entity particle : parent.getChildren()) {
            processParticle(entity, emittersToRemove, particle, direction, toBeRemoved);
        }

        // Remove dead particles
        removeDeadParticles(entity, toBeRemoved);
    }

    /**
     * Removes particles that are no longer active or have exceeded their lifetime or max distance.
     *
     * @param entity The emitter entity whose particles are being removed.
     * @param toBeRemoved A set of particles to be removed.
     */
    private void removeDeadParticles(Entity entity, Set<Entity> toBeRemoved) {
        entity.getComponent(ParentComponent.class).removeChildren(toBeRemoved);
        for (Entity particle : toBeRemoved) {
            particle.getComponent(ParentComponent.class).setParent(null);
            EntityHub.getInstance().removeEntity(particle);
        }
    }

    /**
     * Processes each individual particle, applying forces such as acceleration and velocity boost.
     * If the particle is beyond its allowed maximum distance or has finished its lifecycle, it is removed.
     *
     * @param entity The emitter entity.
     * @param emittersToRemove A set of emitters to be removed if they are no longer active.
     * @param particle The particle entity being processed.
     * @param direction The direction in which particles are emitted.
     * @param toBeRemoved A set of particles to be removed.
     */
    private void processParticle(Entity entity, HashSet<ParticleEmitterEntity> emittersToRemove, Entity particle, Direction direction, Set<Entity> toBeRemoved) {
        if (particle == null) {return;}
        ParticleEntity particleEntity = (ParticleEntity) particle;
        AccelerationComponent acceleration = particleEntity.getComponent(AccelerationComponent.class);
        PositionComponent position = particleEntity.getComponent(PositionComponent.class);

        if (acceleration == null || position == null) return;

        // If the particle has no acceleration, initialize it and apply a velocity boost
        if (acceleration.getAcceleration().isZero()) {
            Vector initial = initializeParticleAcceleration(direction);
            Vector boosted = applyVelocityBoost(initial, direction);

            if (boosted.isZero()) {
                toBeRemoved.add(particleEntity);
                return;
            }

            acceleration.setAcceleration(boosted);
        }

        // Check if the particle's lifecycle is complete and mark it for removal if necessary
        handleLifecycleChecks(entity, particleEntity, emittersToRemove, toBeRemoved);
    }

    /**
     * Initializes the particle's acceleration based on its direction and applies random velocity.
     *
     * @param direction The direction in which the particle is moving.
     * @return A Vector representing the particle's initial velocity.
     */
    private Vector initializeParticleAcceleration(Direction direction) {
        Random random = new Random();
        double deltaTime = Time.getInstance().getDeltaTime();
        double minSpeed = -1;
        double maxSpeed = 1;

        double dx = 0;
        double dy = 0;

        // Adjust velocity based on the direction of emission
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

    /**
     * Applies a random velocity boost to the particle's movement in a given direction.
     *
     * @param base The initial velocity vector of the particle.
     * @param direction The direction in which the particle is emitted.
     * @return A new Vector with the boosted velocity.
     */
    private Vector applyVelocityBoost(Vector base, Direction direction) {
        Random random = new Random();
        double angle = switch (direction) {
            case LEFT -> random.nextDouble(3 * Math.PI / 4, 5 * Math.PI / 4);
            case RIGHT -> random.nextDouble(-Math.PI / 4, Math.PI / 4);
            case UP -> random.nextDouble(5 * Math.PI / 4, 7 * Math.PI / 4);
            case DOWN -> random.nextDouble(Math.PI / 4, 3 * Math.PI / 4);
            default -> random.nextDouble(0, Math.PI * 2);
        };

        double boostMagnitude = random.nextDouble(0, 20);
        double boostDx = Math.cos(angle) * boostMagnitude;
        double boostDy = Math.sin(angle) * boostMagnitude;

        return new Vector(base.getDx() + boostDx, base.getDy() + boostDy);
    }

    /**
     * Checks if the particle has exceeded its maximum allowed distance or if its lifecycle is over.
     * If so, the particle is removed from the system.
     *
     * @param emitter The particle emitter entity.
     * @param particle The particle entity.
     * @param emittersToRemove A set of emitters to be removed if they are no longer active.
     * @param toBeRemoved A set of particles to be removed.
     */
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

    /**
     * Spawns new particles from the emitter if the time interval between occurrences has passed.
     *
     * @param entity The emitter entity that is spawning particles.
     */
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