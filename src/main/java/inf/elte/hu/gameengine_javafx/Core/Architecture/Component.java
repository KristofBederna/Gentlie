package inf.elte.hu.gameengine_javafx.Core.Architecture;

/**
 * Base class for every Component.
 * <br>
 * Components are attached to an Entity object, their responsibility is to hold data.
 */
public abstract class Component {
    /**
     * @return The string representation of the component's data values.
     */
    public abstract String getStatus();
}

