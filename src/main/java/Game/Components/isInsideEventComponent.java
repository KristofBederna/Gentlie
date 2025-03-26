package Game.Components;

import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;

public class isInsideEventComponent extends Component {
    boolean isInside = false;
    Entity entity;

    public void setInside(boolean inside, Entity entity) {
        isInside = inside;
        this.entity = entity;
    }

    public boolean isInside() {
        return isInside;
    }

    public Entity getEntityInside() {
        return entity;
    }

    @Override
    public String getStatus() {
        return "";
    }
}
