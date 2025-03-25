package inf.elte.hu.gameengine_javafx.Components.RenderingComponents;

import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;
import javafx.scene.paint.Color;

public class ColorComponent extends Component {
    private Color color;

    public ColorComponent(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public String getStatus() {
        return "";
    }
}
