package inf.elte.hu.gameengine_javafx.Components;

import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;

public class TileValueComponent extends Component {
    private int tileValue;

    public TileValueComponent(int tileValue) {
        this.tileValue = tileValue;
    }

    public int getTileValue() {
        return tileValue;
    }

    public void setTileValue(int tileValue) {
        this.tileValue = tileValue;
    }
}
