package heatmap.sync;

import heatmap.model.HeatmapSession;
import heatmap.model.MouseEvent;
import heatmap.model.SessionMetadata;
import heatmap.model.VideoFrame;
import heatmap.model.HeatmapZone;

import java.util.List;

/**
 * Synchronizes mouse events to video frames using sequential time windows.
 * Each frame captures all mouse events from its timestamp until the next frame's timestamp.
 * Also assigns heatmap zones to all mouse events.
 */
public class TimeWindowSynchronizer {

    /**
     * Synchronizes mouse events to frames using sequential time windows
     * and assigns zones to each mouse event.
     *
     * @param metadata    Session metadata containing screen size
     * @param frames      List of video frames (must be sorted by timestamp)
     * @param mouseEvents List of mouse events (should be sorted by timestamp)
     * @return Complete heatmap session with synchronized and zone-mapped data
     */
    public HeatmapSession synchronize(SessionMetadata metadata,
                                     List<VideoFrame> frames,
                                     List<MouseEvent> mouseEvents) {
        HeatmapSession session = new HeatmapSession(metadata);

        // Create zone mapper from screen dimensions
        ZoneMapper zoneMapper = new ZoneMapper(metadata.getScreenSize());
        session.setZoneMapper(zoneMapper);

        // Add all frames to session
        for (VideoFrame frame : frames) {
            session.addFrame(frame);
        }

        // Map mouse events to frames using sequential windows
        mapEventsToFrames(frames, mouseEvents);

        // Assign zones to all mouse events
        assignZonesToEvents(mouseEvents, zoneMapper);

        // Set session timestamps
        if (!frames.isEmpty()) {
            metadata.setStartTimestamp(frames.get(0).getTimestamp());
            metadata.setEndTimestamp(frames.get(frames.size() - 1).getTimestamp());
        }

        return session;
    }

    /**
     * Maps mouse events to frames using sequential iteration.
     * Each frame captures events from its timestamp until the next frame's timestamp.
     * Events before the first frame are assigned to the first frame.
     * Events after the last frame are assigned to the last frame.
     *
     * @param frames      List of video frames
     * @param mouseEvents List of mouse events
     */
    private void mapEventsToFrames(List<VideoFrame> frames,
                                   List<MouseEvent> mouseEvents) {
        if (frames.isEmpty() || mouseEvents.isEmpty()) {
            return;
        }

        int frameIndex = 0;
        VideoFrame currentFrame = frames.get(0);
        VideoFrame nextFrame = frames.size() > 1 ? frames.get(1) : null;

        for (MouseEvent event : mouseEvents) {
            long eventTime = event.getTimestamp();

            // Handle events before first frame
            if (eventTime < currentFrame.getTimestamp()) {
                currentFrame.addMouseEvent(event);
                event.setFrameNumber(currentFrame.getFrameNumber());
                continue;
            }

            // Find correct frame window
            while (nextFrame != null && eventTime >= nextFrame.getTimestamp()) {
                // Move to next frame window
                frameIndex++;
                currentFrame = nextFrame;
                nextFrame = frameIndex + 1 < frames.size() ?
                           frames.get(frameIndex + 1) : null;
            }

            // Assign event to current frame
            currentFrame.addMouseEvent(event);
            event.setFrameNumber(currentFrame.getFrameNumber());
        }
    }

    /**
     * Assigns zones to all mouse events based on their screen coordinates.
     * Events with out-of-bounds coordinates will not have a zone assigned.
     *
     * @param mouseEvents List of mouse events
     * @param zoneMapper  Zone mapper for coordinate-to-zone conversion
     */
    private void assignZonesToEvents(List<MouseEvent> mouseEvents,
                                     ZoneMapper zoneMapper) {
        int unmappedCount = 0;

        for (MouseEvent event : mouseEvents) {
            HeatmapZone zone = zoneMapper.getZoneForCoordinates(
                event.getScreenX(),
                event.getScreenY()
            );

            if (zone != null) {
                event.setZone(zone);
            } else {
                unmappedCount++;
            }
        }

        if (unmappedCount > 0) {
            System.err.println("Warning: " + unmappedCount +
                             " mouse events had out-of-bounds coordinates");
        }
    }
}