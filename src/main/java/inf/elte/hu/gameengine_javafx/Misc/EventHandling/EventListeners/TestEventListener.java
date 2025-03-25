package inf.elte.hu.gameengine_javafx.Misc.EventHandling.EventListeners;

import inf.elte.hu.gameengine_javafx.Misc.EventHandling.EventListener;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.Events.TestEvent;

public class TestEventListener implements EventListener<TestEvent> {
    @Override
    public void onEvent(TestEvent event) {
        System.out.println("TestEvent: " + event);
    }
}
