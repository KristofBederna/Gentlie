package inf.elte.hu.gameengine_javafx.Systems.RenderingSystems;

import inf.elte.hu.gameengine_javafx.Components.Default.ParentComponent;
import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.MaxDistanceFromOriginComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.AccelerationComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.DragComponent;
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
    protected void update() {
        var emitters = EntityHub.getInstance().getEntitiesWithType(ParticleEmitterEntity.class);
        var emittersToRemove = new HashSet<ParticleEmitterEntity>();

        for (Entity entity : emitters) {
            ParentComponent parent = entity.getComponent(ParentComponent.class);
            DirectionComponent directionComponent = entity.getComponent(DirectionComponent.class);
            Direction direction = directionComponent.getDirection();

            if (parent == null) {
                continue;
            }

            if (System.currentTimeMillis() >= entity.getComponent(TimeComponent.class).getLastOccurrence() + entity.getComponent(TimeComponent.class).getTimeBetweenOccurrences()) {
                ((ParticleEmitterEntity) entity).createParticles(((ParticleEmitterEntity) entity).getMockParticle(), ((ParticleEmitterEntity) entity).getAmount(), entity.getComponent(ParentComponent.class));
                entity.getComponent(TimeComponent.class).setLastOccurrence();
            }

            Set<Entity> toBeRemoved = new HashSet<>();
            for (Entity particle : parent.getChildren()) {
                ParticleEntity particleEntity = (ParticleEntity) particle;
                AccelerationComponent accelerationComponent = particleEntity.getComponent(AccelerationComponent.class);
                PositionComponent position = particleEntity.getComponent(PositionComponent.class);

                if (accelerationComponent == null || position == null) {
                    continue;
                }

                if (accelerationComponent.getAcceleration().getDx() == 0 && accelerationComponent.getAcceleration().getDy() == 0) {
                    Random random = new Random();

                    double minSpeed = -1;
                    double maxSpeed = 1;

                    double angle = random.nextDouble(-Math.PI / 4, Math.PI / 4);

                    double dx = 0;
                    double dy = switch (direction) {
                        case UP -> {
                            dx = random.nextDouble(minSpeed, maxSpeed) * Time.getInstance().getDeltaTime();
                            yield minSpeed * Time.getInstance().getDeltaTime();
                        }
                        case DOWN -> {
                            dx = random.nextDouble(minSpeed, maxSpeed) * Time.getInstance().getDeltaTime();
                            yield maxSpeed * Time.getInstance().getDeltaTime();
                        }
                        case LEFT -> {
                            angle = random.nextDouble(3 * Math.PI / 4, 5 * Math.PI / 4);
                            dx = minSpeed * Time.getInstance().getDeltaTime();
                            yield random.nextDouble(minSpeed, maxSpeed) * Time.getInstance().getDeltaTime();
                        }
                        case RIGHT -> {
                            dx = maxSpeed * Time.getInstance().getDeltaTime();
                            yield random.nextDouble(minSpeed, maxSpeed) * Time.getInstance().getDeltaTime();
                        }
                        case ALL -> {
                            double baseDx = random.nextDouble(minSpeed, maxSpeed) * Time.getInstance().getDeltaTime();
                            double baseDy = random.nextDouble(minSpeed, maxSpeed) * Time.getInstance().getDeltaTime();

                            double circleAngle = random.nextDouble(0, 2 * Math.PI);
                            double magnitude = random.nextDouble(0, 20) * Time.getInstance().getDeltaTime();

                            double angleDx = Math.cos(circleAngle) * magnitude;
                            double angleDy = Math.sin(circleAngle) * magnitude;

                            particle.getComponent(DragComponent.class).setDrag(random.nextDouble(0.01, 1));

                            dx = baseDx + angleDx;
                            yield  baseDy + angleDy;
                        }
                    };

                    dx += Math.cos(angle) * random.nextDouble(0, 20) * Time.getInstance().getDeltaTime();
                    dy += Math.sin(angle) * random.nextDouble(0, 20) * Time.getInstance().getDeltaTime();

                    accelerationComponent.setAcceleration(new Vector(dx, dy));
                }

                if (particleEntity.getComponent(MaxDistanceFromOriginComponent.class).isOverMaxDistance(particleEntity)) {
                    toBeRemoved.add(particleEntity);
                } else if (particleEntity.getComponent(AccelerationComponent.class).getAcceleration().getDx() == 0 && particleEntity.getComponent(AccelerationComponent.class).getAcceleration().getDy() == 0) {
                    toBeRemoved.add(particleEntity);
                }

                if (entity.getComponent(TimeComponent.class).getTimeBetweenOccurrences() == Integer.MAX_VALUE) {
                    if (entity.getComponent(TimeComponent.class).getLastOccurrence() <= System.currentTimeMillis() - 1000) {
                        emittersToRemove.add((ParticleEmitterEntity) entity);
                    }
                }
            }

            entity.getComponent(ParentComponent.class).removeChildren(toBeRemoved);
            for (Entity particle : toBeRemoved) {
                particle.getComponent(ParentComponent.class).setParent(null);
                EntityHub.getInstance().getEntityManager(ParticleEntity.class).unload(particle.getId());
            }
        }
        for (Entity entity : emitters) {
            EntityHub.getInstance().getEntityManager(ParticleEntity.class).unload(entity.getId());
        }
    }
}
