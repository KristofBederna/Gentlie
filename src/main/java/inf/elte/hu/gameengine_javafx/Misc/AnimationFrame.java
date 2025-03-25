package inf.elte.hu.gameengine_javafx.Misc;

public class AnimationFrame {
    private Tuple<String, Integer> frame;

    public AnimationFrame(Tuple<String, Integer> frame) {
        this.frame = frame;
    }

    public AnimationFrame(String frame, Integer duration) {
        this.frame = new Tuple<>(frame, duration);
    }

    public Tuple<String, Integer> getFrame() {
        return frame;
    }

    public void setFrame(Tuple<String, Integer> frame) {
        this.frame = frame;
    }

    public Integer getDuration() {
        return this.frame.second();
    }

    public String getImage() {
        return this.frame.first();
    }
}
