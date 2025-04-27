package inf.elte.hu.gameengine_javafx.Components.WorldComponents;

import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;

public class WorldDimensionComponent extends Component {
    private double worldWidth, worldHeight;

    public WorldDimensionComponent(double worldWidth, double worldHeight) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    public WorldDimensionComponent() {
    }

    public double getWorldWidth() {
        return worldWidth;
    }

    public double getWorldHeight() {
        return worldHeight;
    }

    public void setWorldWidth(double worldWidth) {
        this.worldWidth = worldWidth;
    }

    public void setWorldHeight(double worldHeight) {
        this.worldHeight = worldHeight;
    }
}
