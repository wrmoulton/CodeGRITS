package heatmap.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single frame in the recorded video.
 * Contains the frame timing information and associated mouse events.
 */
public class VideoFrame {
    private final long timestamp;           // Unix milliseconds
    private final int frameNumber;          // Frame number within clip
    private final int clipNumber;           // Which video clip this frame belongs to
    private final List<MouseEvent> mouseEvents; // Mouse events occurring during this frame

    /**
     * Constructs a VideoFrame with the specified parameters.
     *
     * @param timestamp   The timestamp in Unix milliseconds
     * @param frameNumber The frame number within its clip
     * @param clipNumber  The clip number
     */
    public VideoFrame(long timestamp, int frameNumber, int clipNumber) {
        this.timestamp = timestamp;
        this.frameNumber = frameNumber;
        this.clipNumber = clipNumber;
        this.mouseEvents = new ArrayList<>();
    }

    /**
     * Adds a mouse event to this frame.
     *
     * @param event The mouse event to add
     */
    public void addMouseEvent(MouseEvent event) {
        mouseEvents.add(event);
    }

    // Getters
    public long getTimestamp() {
        return timestamp;
    }

    public int getFrameNumber() {
        return frameNumber;
    }

    public int getClipNumber() {
        return clipNumber;
    }

    public List<MouseEvent> getMouseEvents() {
        return new ArrayList<>(mouseEvents); // Return defensive copy
    }

    public int getMouseEventCount() {
        return mouseEvents.size();
    }

    @Override
    public String toString() {
        return String.format("VideoFrame{frame=%d, clip=%d, timestamp=%d, mouseEvents=%d}",
                frameNumber, clipNumber, timestamp, mouseEvents.size());
    }
}