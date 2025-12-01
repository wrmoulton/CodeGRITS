package heatmap.model;

import heatmap.sync.ZoneMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main data container for a heatmap generation session.
 * Contains all parsed and synchronized data including metadata, frames, and mouse events.
 */
public class HeatmapSession {
    private final SessionMetadata metadata;
    private final List<VideoFrame> frames;
    private final Map<Integer, VideoFrame> frameMap; // For O(1) frame lookup by frame number
    private ZoneMapper zoneMapper;

    /**
     * Constructs a HeatmapSession with the specified metadata.
     *
     * @param metadata The session metadata
     */
    public HeatmapSession(SessionMetadata metadata) {
        this.metadata = metadata;
        this.frames = new ArrayList<>();
        this.frameMap = new HashMap<>();
        this.zoneMapper = null;
    }

    /**
     * Adds a frame to the session.
     *
     * @param frame The video frame to add
     */
    public void addFrame(VideoFrame frame) {
        frames.add(frame);
        frameMap.put(frame.getFrameNumber(), frame);
    }

    /**
     * Gets a frame by its frame number.
     *
     * @param frameNumber The frame number
     * @return The video frame, or null if not found
     */
    public VideoFrame getFrame(int frameNumber) {
        return frameMap.get(frameNumber);
    }

    /**
     * Gets the total number of mouse events across all frames.
     *
     * @return Total mouse event count
     */
    public int getTotalMouseEvents() {
        return frames.stream()
                .mapToInt(VideoFrame::getMouseEventCount)
                .sum();
    }

    /**
     * Gets the number of frames that have at least one mouse event.
     *
     * @return Count of active frames
     */
    public int getActiveFrameCount() {
        return (int) frames.stream()
                .filter(f -> f.getMouseEventCount() > 0)
                .count();
    }

    // Getters
    public SessionMetadata getMetadata() {
        return metadata;
    }

    public List<VideoFrame> getFrames() {
        return new ArrayList<>(frames); // Return defensive copy
    }

    public int getFrameCount() {
        return frames.size();
    }

    /**
     * Sets the zone mapper for this session.
     *
     * @param zoneMapper The zone mapper
     */
    public void setZoneMapper(ZoneMapper zoneMapper) {
        this.zoneMapper = zoneMapper;
    }

    /**
     * Gets the zone mapper for this session.
     *
     * @return The zone mapper, or null if not set
     */
    public ZoneMapper getZoneMapper() {
        return zoneMapper;
    }

    @Override
    public String toString() {
        return String.format("HeatmapSession{frames=%d, activeFrames=%d, totalMouseEvents=%d, %s}",
                getFrameCount(), getActiveFrameCount(), getTotalMouseEvents(), metadata);
    }
}