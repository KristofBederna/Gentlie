package inf.elte.hu.gameengine_javafx.Misc.EventHandling.Events;

import inf.elte.hu.gameengine_javafx.Misc.EventHandling.Event;

public class ResolutionChangeEvent implements Event {
    private double width, height;

    public ResolutionChangeEvent(double width, double height) {
        this.height = height;
        this.width = width;
    }

    public double getWidth() {
        return width;
    }
    public double getHeight() {
        return height;
    }
}
