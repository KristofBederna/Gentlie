package Game.Systems;

import Game.Entities.PolarBearSpawner;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Core.Architecture.GameSystem;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;

public class PolarBearSpawnerSystem extends GameSystem {
    @Override
    public void start() {
        this.active = true;
    }

    @Override
    protected void update() {
        var polarBearSpawners = EntityHub.getInstance().getEntitiesWithType(PolarBearSpawner.class);

        for (Entity polarBearSpawner : polarBearSpawners) {
            ((PolarBearSpawner) polarBearSpawner).spawn();
        }
    }
}
