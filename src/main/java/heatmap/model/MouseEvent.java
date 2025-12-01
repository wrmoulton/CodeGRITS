package heatmap.model;

/**
 * Represents a single mouse event captured during a CodeGRITS session.
 * Contains the event type, timestamp, screen coordinates, and associated file path.
 */
public class MouseEvent {
    private final String id;           // mousePressed, mouseReleased, mouseClicked, mouseMoved, mouseDragged
    private final long timestamp;      // Unix milliseconds
    private final String filePath;     // IDE file being edited (can be null)
    private final int screenX;         // Screen X coordinate
    private final int screenY;         // Screen Y coordinate
    private int frameNumber;           // Mapped frame number (set during synchronization)

    /**
     * Constructs a MouseEvent with the specified parameters.
     *
     * @param id        The type of mouse event (e.g., "mouseMoved", "mouseClicked")
     * @param timestamp The timestamp in Unix milliseconds
     * @param filePath  The file path being edited (can be null)
     * @param screenX   The X coordinate on screen
     * @param screenY   The Y coordinate on screen
     */
    public MouseEvent(String id, long timestamp, String filePath, int screenX, int screenY) {
        this.id = id;
        this.timestamp = timestamp;
        this.filePath = filePath;
        this.screenX = screenX;
        this.screenY = screenY;
        this.frameNumber = -1; // Not yet mapped
    }

    // Getters
    public String getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getFilePath() {
        return filePath;
    }

    public int getScreenX() {
        return screenX;
    }

    public int getScreenY() {
        return screenY;
    }

    public int getFrameNumber() {
        return frameNumber;
    }

    // Setter for frame mapping
    public void setFrameNumber(int frameNumber) {
        this.frameNumber = frameNumber;
    }

    @Override
    public String toString() {
        return String.format("MouseEvent{id='%s', timestamp=%d, pos=(%d,%d), frame=%d}",
                id, timestamp, screenX, screenY, frameNumber);
    }
}