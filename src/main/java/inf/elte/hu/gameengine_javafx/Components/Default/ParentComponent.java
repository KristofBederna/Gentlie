package inf.elte.hu.gameengine_javafx.Components.Default;

import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;

import java.util.HashSet;
import java.util.Set;

public class ParentComponent extends Component {
    private Entity parent;
    private final Set<Entity> children;

    public ParentComponent() {
        this.parent = null;
        this.children = new HashSet<>();
    }

    public ParentComponent(Entity parent) {
        this.parent = parent;
        this.children = new HashSet<>();
    }

    public ParentComponent(Entity parent, Set<Entity> children) {
        this.parent = parent;
        this.children = children != null ? new HashSet<>(children) : new HashSet<>();
    }

    public ParentComponent(Entity parent, Entity child) {
        this.parent = parent;
        this.children = new HashSet<>();
        if (child != null) {
            addChild(child);
        }
    }

    public void setParent(Entity parent) {
        this.parent = parent;
    }

    public Entity getParent() {
        return parent;
    }

    public void removeParent() {
        this.parent = null;
    }

    public void setChildren(Set<Entity> children) {
        this.children.clear();
        if (children != null) {
            this.children.addAll(children);
        }
    }

    public Set<Entity> getChildren() {
        return children;
    }

    public void addChild(Entity child) {
        if (child != null) {
            children.add(child);
        }
    }

    public void addChildren(Set<Entity> children) {
        if (children != null) {
            this.children.addAll(children);
        }
    }

    public void removeChild(Entity child) {
        children.remove(child);
    }

    public void removeChildren(Set<Entity> children) {
        if (children != null) {
            this.children.removeAll(children);
        }
    }

    public void removeAllChildren() {
        this.children.clear();
    }
}
