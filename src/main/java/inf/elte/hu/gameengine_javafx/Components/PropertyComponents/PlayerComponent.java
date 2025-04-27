package inf.elte.hu.gameengine_javafx.Components.PropertyComponents;

import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;

public class PlayerComponent extends Component {
    private static PlayerComponent instance;

    private PlayerComponent() {
        instance = this;
    }

    public static PlayerComponent getInstance() {
        if (instance == null) {
            instance = new PlayerComponent();
        }
        return instance;
    }
}
