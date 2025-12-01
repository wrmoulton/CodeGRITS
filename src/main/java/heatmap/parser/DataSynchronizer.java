package heatmap.parser;

import heatmap.model.HeatmapSession;
import heatmap.model.MouseEvent;
import heatmap.model.SessionMetadata;
import heatmap.model.VideoFrame;

import java.util.List;

/**
 * Synchronizes mouse events with video frames based on timestamps.
 * Maps each mouse event to its corresponding video frame.
 */
public class DataSynchronizer {

    /**
     * Synchronizes mouse events to video frames and creates a complete session object.
     *
     * @param metadata    Session metadata
     * @param frames      List of video frames
     * @param mouseEvents List of mouse events
     * @return Complete heatmap session with synchronized data
     */
    public HeatmapSession synchronize(SessionMetadata metadata, 
                                     List<VideoFrame> frames, 
                                     List<MouseEvent> mouseEvents) {
        HeatmapSession session = new HeatmapSession(metadata);

        // Add all frames to session
        for (VideoFrame frame : frames) {
            session.addFrame(frame);
        }

        // Map mouse events to frames
        int frameIndex = 0;
        for (MouseEvent mouseEvent : mouseEvents) {
            // Find the frame this mouse event belongs to
            // A mouse event belongs to a frame if its timestamp is closest to that frame
            VideoFrame targetFrame = findClosestFrame(frames, mouseEvent.getTimestamp(), frameIndex);
            
            if (targetFrame != null) {
                targetFrame.addMouseEvent(mouseEvent);
                mouseEvent.setFrameNumber(targetFrame.getFrameNumber());
                
                // Optimize: start next search from current position
                frameIndex = frames.indexOf(targetFrame);
            }
        }

        // Set session timestamps
        if (!frames.isEmpty()) {
            metadata.setStartTimestamp(frames.get(0).getTimestamp());
            metadata.setEndTimestamp(frames.get(frames.size() - 1).getTimestamp());
        }

        return session;
    }

    /**
     * Finds the closest frame to a given timestamp.
     * Uses a simple linear search with optimization to start from a hint index.
     *
     * @param frames        List of frames (assumed to be sorted by timestamp)
     * @param timestamp     Target timestamp
     * @param startIndex    Index to start searching from (optimization)
     * @return The closest frame, or null if frames is empty
     */
    private VideoFrame findClosestFrame(List<VideoFrame> frames, long timestamp, int startIndex) {
        if (frames.isEmpty()) {
            return null;
        }

        // Ensure startIndex is valid
        startIndex = Math.max(0, Math.min(startIndex, frames.size() - 1));

        VideoFrame closestFrame = frames.get(startIndex);
        long minDifference = Math.abs(timestamp - closestFrame.getTimestamp());

        // Search forward from startIndex
        for (int i = startIndex; i < frames.size(); i++) {
            VideoFrame frame = frames.get(i);
            long difference = Math.abs(timestamp - frame.getTimestamp());

            if (difference < minDifference) {
                minDifference = difference;
                closestFrame = frame;
            } else if (frame.getTimestamp() > timestamp) {
                // Timestamps are sorted, so we've passed the target
                break;
            }
        }

        // Also check backwards in case startIndex was ahead
        for (int i = startIndex - 1; i >= 0; i--) {
            VideoFrame frame = frames.get(i);
            long difference = Math.abs(timestamp - frame.getTimestamp());

            if (difference < minDifference) {
                minDifference = difference;
                closestFrame = frame;
            } else if (frame.getTimestamp() < timestamp - minDifference) {
                // Too far back
                break;
            }
        }

        return closestFrame;
    }
}