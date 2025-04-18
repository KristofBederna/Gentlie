package inf.elte.hu.gameengine_javafx.Components.WorldComponents;

import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;

import java.util.ArrayList;
import java.util.List;

public class MapMeshComponent extends Component {
    private List<List<Point>> mapCoordinates;

    public MapMeshComponent(List<List<Point>> mapCoordinates) {
        this.mapCoordinates = mapCoordinates;
    }

    public MapMeshComponent() {
        this.mapCoordinates = new ArrayList<>();
    }

    public List<List<Point>> getMapCoordinates() {
        return mapCoordinates;
    }

    public void setMapCoordinates(List<List<Point>> mapCoordinates) {
        this.mapCoordinates = mapCoordinates;
    }

    public Point getMapCoordinate(int x, int y) {
        return mapCoordinates.get(y).get(x);
    }

    public void setMapCoordinate(int x, int y, Point mapCoordinate) {
        mapCoordinates.get(x).set(y, mapCoordinate);
    }

    public void addRow(List<Point> meshRow) {
        mapCoordinates.add(meshRow);
    }

    @Override
    public String getStatus() {
        return "";
    }

    public void addToRow(int rowNum, List<Point> meshRow) {
        if (mapCoordinates.size() <= rowNum) {
            addRow(meshRow);
            return;
        }
        List<Point> oldRow = mapCoordinates.get(rowNum);
        oldRow.addAll(meshRow);
        mapCoordinates.set(rowNum, oldRow);
    }
}
