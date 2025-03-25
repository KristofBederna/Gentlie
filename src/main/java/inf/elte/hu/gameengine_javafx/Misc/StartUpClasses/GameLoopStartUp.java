package inf.elte.hu.gameengine_javafx.Misc.StartUpClasses;

import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.SystemHub;
import inf.elte.hu.gameengine_javafx.Misc.GameLoop;

/**
 * Initializes and manages the game loop.
 * <br>
 * This class starts the game loop and ensures that all {@link GameSystem} instances are updated in priority order.
 */
public class GameLoopStartUp {
    private static GameLoop gameLoop;

    /**
     * Constructs a new {@code GameLoopStartUp} instance.
     * <br>
     * This initializes and starts the game loop, executing all registered game systems based on their priority.
     */
    public GameLoopStartUp() {
        gameLoop = new GameLoop() {
            @Override
            public void update() {
                var systems = SystemHub.getInstance().getAllSystemsInPriorityOrder();
                for (GameSystem system : systems) {
                    if (!system.getIsActive()) {
                        if (system.getIsAborting()) {
                            continue;
                        }
                        system.start();
                    } else {
                        system.run();
                    }
                }
            }
        };
        gameLoop.startLoop();
    }

    /**
     * Stops the game loop.
     * <br>
     * This method halts the execution of the game loop when called.
     */
    public static void stopGameLoop() {
        gameLoop.stopLoop();
    }
}
