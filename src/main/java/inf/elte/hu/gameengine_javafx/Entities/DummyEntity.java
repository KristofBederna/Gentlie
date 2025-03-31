package inf.elte.hu.gameengine_javafx.Entities;

import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.HitBoxComponents.HitBoxComponent;
import inf.elte.hu.gameengine_javafx.Components.PathfindingComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.AccelerationComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.DragComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.MassComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.VelocityComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.*;
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

public class DummyEntity extends Entity {

    private String lastState;

    public DummyEntity(int x, int y, String state, String path, double width, double height) {
        this.getComponent(PositionComponent.class).setLocalPosition(x, y, this);
        this.addComponent(new VelocityComponent(0.5));
        this.addComponent(new StateComponent(state));
        this.addComponent(new ImageComponent(path, width, height));
        this.addComponent(new DimensionComponent(width, height));
        this.addComponent(new AccelerationComponent());
        this.addComponent(new DragComponent(0.9999));
        this.addComponent(new MassComponent(0.5));
        this.addComponent(new HitBoxComponent(new Rectangle(new Point(x, y), width, height).getPoints()));
        this.addComponent(new ZIndexComponent(2));
        this.addComponent(new CentralMassComponent((double) x + width / 2, (double) y + height / 2));
        this.addComponent(new PathfindingComponent(new Point(this.getComponent(CentralMassComponent.class).getCentralX(), this.getComponent(CentralMassComponent.class).getCentralY()), new Point(x + (Config.scaledTileSize * 21) + 50, y + (Config.scaledTileSize * 7) + 50)));
        this.addComponent(new AnimationStateMachineComponent(new AnimationStateMachine(this) {
            @Override
            public void setAnimationState() {
                if (Objects.equals(entity.getComponent(StateComponent.class).getCurrentState(), lastState)) {
                    return;
                }

                AnimationComponent animationComponent = entity.getComponent(AnimationComponent.class);

                AnimationFrame downFrame1 = new AnimationFrame("/assets/images/" + "PlayerDown_1.png", 15 * Time.getInstance().getFPS() / 60);
                AnimationFrame downFrame2 = new AnimationFrame("/assets/images/" + "PlayerDown_2.png", 15 * Time.getInstance().getFPS() / 60);

                AnimationFrame upFrame1 = new AnimationFrame("/assets/images/" + "PlayerUp_1.png", 15 * Time.getInstance().getFPS() / 60);
                AnimationFrame upFrame2 = new AnimationFrame("/assets/images/" + "PlayerUp_2.png", 15 * Time.getInstance().getFPS() / 60);

                AnimationFrame leftFrame1 = new AnimationFrame("/assets/images/" + "PlayerLeft_1.png", 15 * Time.getInstance().getFPS() / 60);
                AnimationFrame leftFrame2 = new AnimationFrame("/assets/images/" + "PlayerLeft_2.png", 15 * Time.getInstance().getFPS() / 60);

                AnimationFrame rightFrame1 = new AnimationFrame("/assets/images/" + "PlayerRight_1.png", 15 * Time.getInstance().getFPS() / 60);
                AnimationFrame rightFrame2 = new AnimationFrame("/assets/images/" + "PlayerRight_2.png", 15 * Time.getInstance().getFPS() / 60);


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
                        entity.getComponent(ImageComponent.class).setNextFrame("/assets/images/PlayerIdle.png");
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

    public void setAnimationState() {
        if (Objects.equals(this.getComponent(StateComponent.class).getCurrentState(), lastState)) {
            return;
        }

        AnimationComponent animationComponent = this.getComponent(AnimationComponent.class);

        AnimationFrame downFrame1 = new AnimationFrame("/assets/images/" + "PlayerDown_1.png", 15 * Time.getInstance().getFPS() / 60);
        AnimationFrame downFrame2 = new AnimationFrame("/assets/images/" + "PlayerDown_2.png", 15 * Time.getInstance().getFPS() / 60);

        AnimationFrame upFrame1 = new AnimationFrame("/assets/images/" + "PlayerUp_1.png", 15 * Time.getInstance().getFPS() / 60);
        AnimationFrame upFrame2 = new AnimationFrame("/assets/images/" + "PlayerUp_2.png", 15 * Time.getInstance().getFPS() / 60);

        AnimationFrame leftFrame1 = new AnimationFrame("/assets/images/" + "PlayerLeft_1.png", 15 * Time.getInstance().getFPS() / 60);
        AnimationFrame leftFrame2 = new AnimationFrame("/assets/images/" + "PlayerLeft_2.png", 15 * Time.getInstance().getFPS() / 60);

        AnimationFrame rightFrame1 = new AnimationFrame("/assets/images/" + "PlayerRight_1.png", 15 * Time.getInstance().getFPS() / 60);
        AnimationFrame rightFrame2 = new AnimationFrame("/assets/images/" + "PlayerRight_2.png", 15 * Time.getInstance().getFPS() / 60);


        String currentState = this.getComponent(StateComponent.class).getCurrentState();
        lastState = currentState;

        switch (currentState) {
            case "up":
                if (animationComponent != null) {
                    animationComponent.setFrames(List.of(upFrame1, upFrame2));
                } else {
                    this.addComponent(new AnimationComponent(List.of(upFrame1, upFrame2)));
                }
                break;
            case "down":
                if (animationComponent != null) {
                    animationComponent.setFrames(List.of(downFrame1, downFrame2));
                } else {
                    this.addComponent(new AnimationComponent(List.of(downFrame1, downFrame2)));
                }
                break;
            case "left":
                if (animationComponent != null) {
                    animationComponent.setFrames(List.of(leftFrame1, leftFrame2));
                } else {
                    this.addComponent(new AnimationComponent(List.of(leftFrame1, leftFrame2)));
                }
                break;
            case "right":
                if (animationComponent != null) {
                    animationComponent.setFrames(List.of(rightFrame1, rightFrame2));
                } else {
                    this.addComponent(new AnimationComponent(List.of(rightFrame1, rightFrame2)));
                }
                break;
            case "idle":
                this.removeComponentsByType(AnimationComponent.class);
                this.getComponent(ImageComponent.class).setNextFrame("/assets/images/PlayerIdle.png");
                break;
        }
    }
}
