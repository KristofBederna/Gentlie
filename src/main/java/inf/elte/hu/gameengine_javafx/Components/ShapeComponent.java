package inf.elte.hu.gameengine_javafx.Components;

import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Shape;

public class ShapeComponent<T extends Shape> extends Component {
    private T shape;
    private Class type;

    public ShapeComponent(T shape) {
        this.shape = shape;
        this.type = shape.getClass();
    }

    public T getShape() {
        return shape;
    }

    public void setShape(T shape) {
        this.shape = shape;
    }

    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }

    @Override
    public String getStatus() {
        return "";
    }
}
