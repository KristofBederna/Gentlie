package Game.Components;

import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;

public class AttackTimeOutComponent extends Component {
    private final long duration;
    private final long startTime;

    public AttackTimeOutComponent(long duration) {
        this.duration = duration;
        startTime = System.currentTimeMillis();
    }

    public long getDuration() {
        return duration;
    }

    public long getStartTime() {
        return startTime;
    }
}
