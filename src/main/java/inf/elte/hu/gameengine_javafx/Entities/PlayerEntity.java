package inf.elte.hu.gameengine_javafx.Entities;

import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.HitBoxComponents.HitBoxComponent;
import inf.elte.hu.gameengine_javafx.Components.InteractiveComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.AccelerationComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.DragComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.MassComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.VelocityComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.CentralMassComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.DimensionComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.PlayerComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.StateComponent;
import inf.elte.hu.gameengine_javafx.Components.RenderingComponents.AnimationComponent;
import inf.elte.hu.gameengine_javafx.Components.RenderingComponents.AnimationStateMachineComponent;
import inf.elte.hu.gameengine_javafx.Components.RenderingComponents.ImageComponent;
import inf.elte.hu.gameengine_javafx.Components.RenderingComponents.ZIndexComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Rectangle;
import inf.elte.hu.gameengine_javafx.Misc.AnimationFrame;
import inf.elte.hu.gameengine_javafx.Misc.AnimationStateMachine;
import inf.elte.hu.gameengine_javafx.Misc.Config;
import inf.elte.hu.gameengine_javafx.Misc.Time;

import java.util.List;
import java.util.Objects;

public class PlayerEntity extends Entity {
    public PlayerEntity(double x, double y, String state, String path, double width, double height) {
        this.getComponent(PositionComponent.class).setLocalPosition(x, y, this);
        this.addComponent(new VelocityComponent(2));
        this.addComponent(new AccelerationComponent());
        this.addComponent(new DragComponent(0.98));
        this.addComponent(new MassComponent(0.5));
        this.addComponent(new StateComponent(state));
        this.addComponent(new ImageComponent(path, width, height));
        this.addComponent(new InteractiveComponent());
        this.addComponent(new DimensionComponent(width, height));
        this.addComponent(new HitBoxComponent(new Rectangle(new Point(x, y), width, height).getPoints()));
        this.addComponent(new ZIndexComponent(3));
        this.addComponent(PlayerComponent.getInstance());
        this.addComponent(new CentralMassComponent(x + width / 2, y + height / 2));
        this.addComponent(new AnimationStateMachineComponent(new AnimationStateMachine(this) {
            @Override
            public void setAnimationState() {
                if (Objects.equals(entity.getComponent(StateComponent.class).getCurrentState(), lastState)) {
                    return;
                }

                AnimationComponent animationComponent = entity.getComponent(AnimationComponent.class);

                AnimationFrame downFrame1 = new AnimationFrame("/assets/images/Gentlie/" + "Gentlie_Down_2.png", 15 * Time.getInstance().getFPS() / 60);
                AnimationFrame downFrame2 = new AnimationFrame("/assets/images/Gentlie/" + "Gentlie_Down_3.png", 15 * Time.getInstance().getFPS() / 60);

                AnimationFrame upFrame1 = new AnimationFrame("/assets/images/Gentlie/" + "Gentlie_Up_2.png", 15 * Time.getInstance().getFPS() / 60);
                AnimationFrame upFrame2 = new AnimationFrame("/assets/images/Gentlie/" + "Gentlie_Up_3.png", 15 * Time.getInstance().getFPS() / 60);

                AnimationFrame leftFrame1 = new AnimationFrame("/assets/images/Gentlie/" + "Gentlie_Left_2.png", 15 * Time.getInstance().getFPS() / 60);
                AnimationFrame leftFrame2 = new AnimationFrame("/assets/images/Gentlie/" + "Gentlie_Left_3.png", 15 * Time.getInstance().getFPS() / 60);

                AnimationFrame rightFrame1 = new AnimationFrame("/assets/images/Gentlie/" + "Gentlie_Right_2.png", 15 * Time.getInstance().getFPS() / 60);
                AnimationFrame rightFrame2 = new AnimationFrame("/assets/images/Gentlie/" + "Gentlie_Right_3.png", 15 * Time.getInstance().getFPS() / 60);


                String currentState = entity.getComponent(StateComponent.class).getCurrentState();
                lastState = currentState;

                switch (currentState) {
                    case "up":
                        if (animationComponent != null) {
                            animationComponent.setFrames(List.of(upFrame1, upFrame2));
                        } else {
                            entity.addComponent(new AnimationComponent(List.of(upFrame1, upFrame2)));
                        }
                        break;
                    case "down":
                        if (animationComponent != null) {
                            animationComponent.setFrames(List.of(downFrame1, downFrame2));
                        } else {
                            entity.addComponent(new AnimationComponent(List.of(downFrame1, downFrame2)));
                        }
                        break;
                    case "left":
                        if (animationComponent != null) {
                            animationComponent.setFrames(List.of(leftFrame1, leftFrame2));
                        } else {
                            entity.addComponent(new AnimationComponent(List.of(leftFrame1, leftFrame2)));
                        }
                        break;
                    case "right":
                        if (animationComponent != null) {
                            animationComponent.setFrames(List.of(rightFrame1, rightFrame2));
                        } else {
                            entity.addComponent(new AnimationComponent(List.of(rightFrame1, rightFrame2)));
                        }
                        break;
                    case "idle":
                        entity.removeComponentsByType(AnimationComponent.class);
                        entity.getComponent(ImageComponent.class).setNextFrame("/assets/images/Gentlie/Gentlie_Down_1.png");
                        break;
                }
            }
        }));

        addToManager();
    }

    @Override
    public String toString() {
        return this.getComponent(PositionComponent.class).getGlobalX() + " " + this.getComponent(PositionComponent.class).getGlobalY() + " " + this.getComponent(VelocityComponent.class).getVelocity().getDx() + " " + this.getComponent(VelocityComponent.class).getVelocity().getDy();
    }
}
