package inf.elte.hu.gameengine_javafx.Misc.StartUpClasses;

import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Core.EntityManager;
import inf.elte.hu.gameengine_javafx.Entities.LoggerEntity;

public class LoggerStartUp {
    LoggerEntity loggerEntity = new LoggerEntity();

    public LoggerStartUp() {
        EntityManager<LoggerEntity> loggerEntityManager = new EntityManager<>();
        loggerEntityManager.register(loggerEntity);
        EntityHub.getInstance().addEntityManager(LoggerEntity.class, loggerEntityManager);
    }

    public LoggerEntity getLoggerEntity() {
        return loggerEntity;
    }
}
