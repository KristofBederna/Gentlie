package Game.Entities;

import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.HitBoxComponents.HitBoxComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.CentralMassComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.DimensionComponent;
import inf.elte.hu.gameengine_javafx.Components.RenderingComponents.AnimationComponent;
import inf.elte.hu.gameengine_javafx.Components.RenderingComponents.AnimationStateMachineComponent;
import inf.elte.hu.gameengine_javafx.Components.RenderingComponents.ImageComponent;
import inf.elte.hu.gameengine_javafx.Components.RenderingComponents.ZIndexComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Rectangle;
import inf.elte.hu.gameengine_javafx.Misc.AnimationFrame;
import inf.elte.hu.gameengine_javafx.Misc.AnimationStateMachine;
import inf.elte.hu.gameengine_javafx.Misc.Time;

import java.util.List;

public class CampfireEntity extends Entity {
    public CampfireEntity(double x, double y, String path, double width, double height) {
        this.getComponent(PositionComponent.class).setLocalPosition(x, y, this);
        this.addComponent(new ImageComponent(path, width, height));
        this.addComponent(new DimensionComponent(width, height));
        this.addComponent(new HitBoxComponent(new Rectangle(new Point(x, y), width, height).getPoints()));
        this.addComponent(new ZIndexComponent(3));
        this.addComponent(new CentralMassComponent(x + width / 2, y + height / 2));
        this.addComponent(new AnimationStateMachineComponent(new AnimationStateMachine(this) {
            @Override
            public void setAnimationState() {
                AnimationComponent animationComponent = entity.getComponent(AnimationComponent.class);

                AnimationFrame frame1 = new AnimationFrame("/assets/images/Campfire/" + "Campfire_1.png", 15 * Time.getInstance().getFPS() / 60);
                AnimationFrame frame2 = new AnimationFrame("/assets/images/Campfire/" + "Campfire_2.png", 15 * Time.getInstance().getFPS() / 60);
                AnimationFrame frame3 = new AnimationFrame("/assets/images/Campfire/" + "Campfire_3.png", 15 * Time.getInstance().getFPS() / 60);

                if (animationComponent == null) {
                    entity.addComponent(new AnimationComponent(List.of(frame1, frame2, frame3)));
                }
            }
        }));

        addToManager();
    }
}
