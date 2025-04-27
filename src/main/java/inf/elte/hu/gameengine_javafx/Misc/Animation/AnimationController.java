package inf.elte.hu.gameengine_javafx.Misc.Animation;

import inf.elte.hu.gameengine_javafx.Misc.Time;

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

    /**
     * Runs checks if enough time had passed for a frame to be switched.
     *
     * @return The image needed for the next frame.
     */
    public String getNextFrame() {
        updateElapsedTime();
        advanceFrameIfNeeded();
        return getCurrentFrameImage();
    }

    /**
     * Updates the elapsed time by adding deltaTime to itself.
     */
    private void updateElapsedTime() {
        elapsedTime += Time.getInstance().getDeltaTime();
    }

    /**
     * If enough time passed the frame gets advanced.
     */
    private void advanceFrameIfNeeded() {
        double frameDuration = getCurrentFrameDuration();
        if (elapsedTime >= frameDuration) {
            advanceFrame();
            elapsedTime = 0;
        }
    }

    /**
     * @return How many frames the current image needs to be shown.
     */
    private double getCurrentFrameDuration() {
        int fps = Time.getInstance().getFPS();
        return (double) frames.get(currentFrame).getFrame().second() / fps;
    }

    /**
     * Swaps the frame out with the next frame in the list. When at the end, jump to the beginning.
     */
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
