package inf.elte.hu.gameengine_javafx.Components.PropertyComponents;

import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;

public class StateComponent extends Component {
    private String currentState;

    public StateComponent(String state) {
        currentState = state;
    }

    public String getCurrentState() {
        return currentState;
    }

    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }

    @Override
    public String getStatus() {
        return (this.getClass().getSimpleName() + ": " + currentState);
    }
}
