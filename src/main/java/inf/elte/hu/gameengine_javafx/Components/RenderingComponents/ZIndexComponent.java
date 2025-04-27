package inf.elte.hu.gameengine_javafx.Components.RenderingComponents;

import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;

public class ZIndexComponent extends Component {
    private int z_index;

    public ZIndexComponent(int z_index) {
        this.z_index = z_index;
    }

    public int getZ_index() {
        return z_index;
    }

    public void setZ_index(int z_index) {
        this.z_index = z_index;
    }
}
