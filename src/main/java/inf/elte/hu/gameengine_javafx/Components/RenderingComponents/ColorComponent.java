package inf.elte.hu.gameengine_javafx.Components.RenderingComponents;

import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;
import javafx.scene.paint.Color;

public class ColorComponent extends Component {
    private Color color;
    private Color stroke;

    public ColorComponent(Color color, Color stroke) {
        this.color = color;
        this.stroke = stroke;
    }

    public ColorComponent(Color color) {
        this.color = color;
        this.stroke = Color.TRANSPARENT;
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

    public Color getStroke() {
        return stroke;
    }

    public void setStroke(Color stroke) {
        this.stroke = stroke;
    }
}
