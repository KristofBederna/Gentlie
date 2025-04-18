package inf.elte.hu.gameengine_javafx.Entities.UIEntities;

import inf.elte.hu.gameengine_javafx.Components.UIComponents.UIComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Misc.Layers.uiRoot;
import javafx.application.Platform;
import javafx.scene.layout.Region;

import java.util.Objects;

public abstract class UIEntity<T extends UIComponent<?>> extends Entity {
    protected T uiComponent;

    public T getUIComponent() {
        return uiComponent;
    }

    public void addToUI() {
        Platform.runLater(() -> {
            if (uiRoot.getInstance().getChildren().contains(uiComponent.getNode())) {
                return;
            }
            uiRoot.getInstance().getChildren().add(uiComponent.getNode());
        });
    }

    public void removeFromUI() {
        Platform.runLater(() -> {
            uiRoot.getInstance().getChildren().remove(uiComponent.getNode());
        });
    }

    public void applyStyle(String style) {
        T component = getUIComponent();
        if (component != null && component.getNode() != null) {
            component.getNode().setStyle(style);
        }
    }

    public void applyStyleFromFile(String cssFilePath) {
        T component = getUIComponent();
        if (component != null && component.getNode() != null) {
            if (component.getNode() instanceof Region) {
                ((Region) component.getNode()).getStylesheets().add(Objects.requireNonNull(getClass().getResource(cssFilePath)).toExternalForm());
            }
        }
    }

    public void addStyleClass(String className) {
        T component = getUIComponent();
        if (component != null && component.getNode() != null) {
            component.getNode().getStyleClass().add(className);
        }
    }
}
