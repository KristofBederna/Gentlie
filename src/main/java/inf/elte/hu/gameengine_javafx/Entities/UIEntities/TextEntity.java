package inf.elte.hu.gameengine_javafx.Entities.UIEntities;

import inf.elte.hu.gameengine_javafx.Components.UIComponents.TextComponent;
import javafx.scene.text.Text;

public class TextEntity extends UIEntity<TextComponent> {
    public TextEntity(double x, double y, double width, double height, String text) {
        this.uiComponent = new TextComponent(x, y, width, height, text);
        addComponent(uiComponent);

        addToManager();
        this.addToUI();
    }

    public Text getTextNode() {
        return (Text) this.getComponent(TextComponent.class).getUIElement().getChildren().getFirst();
    }
}
