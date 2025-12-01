package heatmap.model;

import java.awt.Dimension;

/**
 * Contains metadata about a CodeGRITS recording session.
 * This includes screen resolution, scaling factors, timing information, and file paths.
 */
public class SessionMetadata {
    private final String sessionPath;       // Path to session folder
    private final Dimension screenSize;     // Screen resolution from environment XML
    private final double scaleX;            // DPI scaling factor X
    private final double scaleY;            // DPI scaling factor Y
    private final int frameRate;            // Frames per second (typically 4)
    private long startTimestamp;            // Session start time (from frames.csv)
    private long endTimestamp;              // Session end time (from frames.csv)
    
    // Additional metadata
    private String ideName;
    private String ideVersion;
    private String osName;
    private String projectName;

    /**
     * Constructs SessionMetadata with the specified parameters.
     *
     * @param sessionPath Path to the session folder
     * @param screenSize  Screen dimensions
     * @param scaleX      X scaling factor
     * @param scaleY      Y scaling factor
     * @param frameRate   Frames per second
     */
    public SessionMetadata(String sessionPath, Dimension screenSize, 
                          double scaleX, double scaleY, int frameRate) {
        this.sessionPath = sessionPath;
        this.screenSize = screenSize;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.frameRate = frameRate;
        this.startTimestamp = 0;
        this.endTimestamp = 0;
    }

    // Getters
    public String getSessionPath() {
        return sessionPath;
    }

    public Dimension getScreenSize() {
        return screenSize;
    }

    public double getScaleX() {
        return scaleX;
    }

    public double getScaleY() {
        return scaleY;
    }

    public int getFrameRate() {
        return frameRate;
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    public String getIdeName() {
        return ideName;
    }

    public String getIdeVersion() {
        return ideVersion;
    }

    public String getOsName() {
        return osName;
    }

    public String getProjectName() {
        return projectName;
    }

    // Setters for timing and additional metadata
    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public void setIdeName(String ideName) {
        this.ideName = ideName;
    }

    public void setIdeVersion(String ideVersion) {
        this.ideVersion = ideVersion;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /**
     * Gets the session duration in milliseconds.
     *
     * @return Duration in milliseconds, or 0 if timestamps not set
     */
    public long getDurationMs() {
        if (startTimestamp > 0 && endTimestamp > 0) {
            return endTimestamp - startTimestamp;
        }
        return 0;
    }

    @Override
    public String toString() {
        return String.format("SessionMetadata{screen=%dx%d, scale=%.1fx/%.1fy, fps=%d, duration=%.1fs}",
                screenSize.width, screenSize.height, scaleX, scaleY, frameRate, getDurationMs() / 1000.0);
    }
}