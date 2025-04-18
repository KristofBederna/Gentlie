package inf.elte.hu.gameengine_javafx.Misc;

import java.util.List;

public class AnimationController {
    private List<AnimationFrame> frames;
    private int currentFrame = 0;
    private double elapsedTime = 0;

    public AnimationController(List<AnimationFrame> frames) {
        this.frames = frames;
    }

    public void setFrames(List<AnimationFrame> frames) {
        this.frames = frames;
        this.currentFrame = 0;
        this.elapsedTime = 0;
    }

    public String getNextFrame() {
        updateElapsedTime();
        advanceFrameIfNeeded();
        return getCurrentFrameImage();
    }

    private void updateElapsedTime() {
        elapsedTime += Time.getInstance().getDeltaTime();
    }

    private void advanceFrameIfNeeded() {
        double frameDuration = getCurrentFrameDuration();
        if (elapsedTime >= frameDuration) {
            advanceFrame();
            elapsedTime = 0;
        }
    }

    private double getCurrentFrameDuration() {
        int fps = Time.getInstance().getFPS();
        return (double) frames.get(currentFrame).getFrame().second() / fps;
    }

    private void advanceFrame() {
        currentFrame = (currentFrame + 1) % frames.size();
    }

    private String getCurrentFrameImage() {
        return frames.get(currentFrame).getImage();
    }

    public List<AnimationFrame> getFrames() {
        return frames;
    }

    public double getElapsedTime() {
        return elapsedTime;
    }
}
