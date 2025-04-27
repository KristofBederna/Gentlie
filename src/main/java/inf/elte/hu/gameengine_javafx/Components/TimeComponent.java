package inf.elte.hu.gameengine_javafx.Components;

import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;

public class TimeComponent extends Component {
    private long timeBetweenOccurrences;
    private long lastOccurrence;

    public TimeComponent(long timeBetweenOccurrences) {
        this.timeBetweenOccurrences = timeBetweenOccurrences;
        this.lastOccurrence = System.currentTimeMillis() - timeBetweenOccurrences;
    }

    public long getTimeBetweenOccurrences() {
        return timeBetweenOccurrences;
    }

    public void setTimeBetweenOccurrences(long timeBetweenOccurrences) {
        this.timeBetweenOccurrences = timeBetweenOccurrences;
    }

    public long getLastOccurrence() {
        return lastOccurrence;
    }

    public void setLastOccurrence() {
        this.lastOccurrence = System.currentTimeMillis();
    }

    public void setLastOccurrence(long lastOccurrence) {
        this.lastOccurrence = lastOccurrence;
    }
}
