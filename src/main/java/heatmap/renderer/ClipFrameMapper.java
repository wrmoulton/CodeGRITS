package heatmap.renderer;

import heatmap.parser.FrameCSVParser;
import heatmap.model.VideoFrame;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Maps clip-local frame numbers to global frame numbers and heatmap file paths.
 */
public class ClipFrameMapper {
    private final Map<String, Integer> clipFrameToGlobal;  // "clipNum_frameNum" -> globalFrame
    private final String heatmapDir;
    private int totalFrames;
    
    /**
     * Creates a frame mapper from frames.csv data.
     *
     * @param frames     List of video frames parsed from frames.csv
     * @param heatmapDir Directory containing heatmap PNG files
     */
    public ClipFrameMapper(List<VideoFrame> frames, String heatmapDir) {
        this.heatmapDir = heatmapDir;
        this.clipFrameToGlobal = new HashMap<>();
        this.totalFrames = frames.size();
        
        // Build mapping from (clip, frame) to global frame number
        for (int globalFrame = 0; globalFrame < frames.size(); globalFrame++) {
            VideoFrame frame = frames.get(globalFrame);
            String key = makeKey(frame.getClipNumber(), frame.getFrameNumber());
            clipFrameToGlobal.put(key, globalFrame + 1); // Global frames are 1-indexed
        }
    }
    
    /**
     * Gets the global frame number for a clip-local frame.
     *
     * @param clipNumber  The clip number
     * @param frameNumber The frame number within the clip
     * @return Global frame number, or -1 if not found
     */
    public int getGlobalFrameNumber(int clipNumber, int frameNumber) {
        String key = makeKey(clipNumber, frameNumber);
        return clipFrameToGlobal.getOrDefault(key, -1);
    }
    
    /**
     * Gets the heatmap file path for a global frame number.
     *
     * @param globalFrameNumber The global frame number (1-indexed)
     * @return Path to the heatmap PNG file
     */
    public String getHeatmapPath(int globalFrameNumber) {
        return String.format("%s/frame_%05d.png", heatmapDir, globalFrameNumber);
    }
    
    /**
     * Gets the total number of frames across all clips.
     *
     * @return Total frame count
     */
    public int getTotalFrames() {
        return totalFrames;
    }
    
    private String makeKey(int clipNumber, int frameNumber) {
        return clipNumber + "_" + frameNumber;
    }
}