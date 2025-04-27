package inf.elte.hu.gameengine_javafx.Components.WorldComponents;

import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;
import inf.elte.hu.gameengine_javafx.Entities.TileEntity;
import inf.elte.hu.gameengine_javafx.Entities.WorldEntity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Misc.MapClasses.Chunk;
import inf.elte.hu.gameengine_javafx.Misc.MapClasses.World;
import inf.elte.hu.gameengine_javafx.Misc.Tuple;

public class WorldDataComponent extends Component {
    private World world = new World();

    public WorldDataComponent() {
    }

    public WorldDataComponent(World world) {
        this.world = world;
    }

    public World getMapData() {
        return world;
    }


    public void setMapData(int x, int y, Chunk tileEntities) {
        this.world.clear();
        this.world.addChunk(x, y, tileEntities);
    }

    public void updateChunk(Tuple<Integer, Integer> chunkKey, Chunk newChunk) {
        WorldEntity.getInstance().getComponent(WorldDataComponent.class).getMapData().getSavedChunks().put(chunkKey, newChunk);
        WorldEntity.getInstance().getComponent(WorldDataComponent.class).getMapData().getWorld().put(chunkKey, newChunk);
    }

    public TileEntity getElement(Point point) {
        return world.getElementAt(point);
    }

    public TileEntity getElementSaved(Point point) {
        return world.getElementAtSaved(point);
    }


    public void clear() {
        world.clear();
    }
}
