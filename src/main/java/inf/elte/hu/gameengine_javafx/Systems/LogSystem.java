package inf.elte.hu.gameengine_javafx.Systems;

import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Core.ResourceHub;
import inf.elte.hu.gameengine_javafx.Core.ResourceManager;
import inf.elte.hu.gameengine_javafx.Core.SystemHub;
import inf.elte.hu.gameengine_javafx.Entities.LoggerEntity;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.util.Map;

public class LogSystem extends GameSystem {
    @Override
    public void start() {
        this.active = true;
    }

    @Override
    public void update() {
        TextArea statusTextArea = null;

        for (Entity e : EntityHub.getInstance().getAllEntities()) {
            if (e.getClass() == LoggerEntity.class) {
                LoggerEntity info = (LoggerEntity) e;
                statusTextArea = (TextArea) info.getTextArea();

                if (statusTextArea == null) {
                    System.out.println("Error: TextArea is null for DebugInfoEntity");
                    return;
                }
                break;
            }
        }

        if (statusTextArea == null) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Entities status:\n");

        for (Entity entity : EntityHub.getInstance().getAllEntities()) {
            sb.append(entity.getClass().getSimpleName()).append("\n");
            sb.append(EntityHub.getInstance().getEntityManager(entity.getClass()).getLastAccessed(entity.getId())).append('\n');
            for (Component component : entity.getAllComponents().values()) {
                sb.append(component.getStatus()).append("\n");
            }
        }

        sb.append("\nSystems currently running:\n");

        for (GameSystem system : SystemHub.getInstance().getAllSystemsInPriorityOrder()) {
            sb.append(system.getClass().getSimpleName()).append("\n");
        }

        sb.append("\nResources currently loaded:\n");

        for (Map.Entry<Class<?>, ResourceManager<?>> entry : ResourceHub.getInstance().getAllResourceManagers().entrySet()) {
            Class<?> resourceType = entry.getKey();
            ResourceManager<?> manager = entry.getValue();

            sb.append(resourceType.getSimpleName()).append(":\n");

            for (String resourceKey : manager.getResources().keySet()) {
                sb.append("  - ").append(resourceKey).append("\n");
                sb.append("  - ").append(manager.getLastAccessed(resourceKey)).append("\n");
            }
        }

        TextArea finalStatusTextArea = statusTextArea;
        Platform.runLater(() -> finalStatusTextArea.setText(sb.toString()));
    }
}
