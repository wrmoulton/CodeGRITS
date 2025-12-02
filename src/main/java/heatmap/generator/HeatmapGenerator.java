package heatmap.generator;

import heatmap.model.HeatmapSession;
import heatmap.model.MouseEvent;
import heatmap.model.VideoFrame;
import heatmap.sync.ZoneMapper;

import javax.imageio.ImageIO;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Generates heatmap overlay images for all frames in a session.
 * Uses a rolling time window to aggregate mouse events.
 */
public class HeatmapGenerator {
    private final HeatmapSession session;
    private final ZoneHeatCalculator calculator;
    private final HeatmapRenderer renderer;
    private final File outputDir;
    
    /**
     * Creates a heatmap generator for the specified session.
     *
     * @param session    The heatmap session with synchronized data
     * @param outputPath Base output directory path
     * @throws IOException If output directory cannot be created
     */
    public HeatmapGenerator(HeatmapSession session, String outputPath) throws IOException {
        this.session = session;
        this.calculator = new ZoneHeatCalculator(HeatmapConfiguration.DEFAULT_SCALING);
        
        Dimension screenSize = session.getMetadata().getScreenSize();
        this.renderer = new HeatmapRenderer(
            screenSize.width,
            screenSize.height,
            session.getZoneMapper()
        );
        
        // Create output directory
        this.outputDir = new File(outputPath, HeatmapConfiguration.OUTPUT_DIR_NAME);
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw new IOException("Failed to create output directory: " + outputDir.getAbsolutePath());
        }
    }
    
    /**
     * Generates heatmap images for all frames in the session.
     *
     * @return Number of frames processed
     * @throws IOException If image writing fails
     */
    public int generateAll() throws IOException {
        List<VideoFrame> frames = session.getFrames();
        int totalFrames = frames.size();
        
        System.out.println("Generating heatmaps for " + totalFrames + " frames...");
        System.out.println("Output directory: " + outputDir.getAbsolutePath());
        System.out.println("Time window: " + (HeatmapConfiguration.TIME_WINDOW_MS / 1000) + " seconds");
        System.out.println();
        
        int processed = 0;
        long startTime = System.currentTimeMillis();
        
        for (VideoFrame frame : frames) {
            generateFrameHeatmap(frame);
            processed++;
            
            // Progress indicator
            if (processed % 50 == 0 || processed == totalFrames) {
                double progress = (processed * 100.0) / totalFrames;
                long elapsed = System.currentTimeMillis() - startTime;
                double framesPerSec = processed / (elapsed / 1000.0);
                
                System.out.printf("\rProgress: %d/%d (%.1f%%) - %.1f frames/sec",
                    processed, totalFrames, progress, framesPerSec);
            }
        }
        
        System.out.println("\n\nGeneration complete!");
        long totalTime = System.currentTimeMillis() - startTime;
        System.out.printf("Total time: %.2f seconds\n", totalTime / 1000.0);
        System.out.printf("Average: %.1f frames/second\n", processed / (totalTime / 1000.0));
        
        return processed;
    }
    
    /**
     * Generates a heatmap for a single frame.
     *
     * @param frame The video frame to process
     * @throws IOException If image writing fails
     */
    private void generateFrameHeatmap(VideoFrame frame) throws IOException {
        // Collect events within time window
        List<MouseEvent> eventsInWindow = collectEventsInWindow(frame);
        
        // Calculate zone intensities
        Map<Integer, Double> intensities = calculator.calculateZoneIntensities(eventsInWindow);
        
        // Render heatmap image
        BufferedImage heatmap = renderer.render(intensities);
        
        // Save to file
        String fileName = String.format(HeatmapConfiguration.FILE_NAME_PATTERN, frame.getFrameNumber());
        File outputFile = new File(outputDir, fileName);
        ImageIO.write(heatmap, HeatmapConfiguration.IMAGE_FORMAT, outputFile);
    }
    
    /**
     * Collects all mouse events within the time window for a frame.
     * Window: [frame.timestamp - TIME_WINDOW_MS, frame.timestamp]
     *
     * @param targetFrame The frame to collect events for
     * @return List of mouse events within the time window
     */
    private List<MouseEvent> collectEventsInWindow(VideoFrame targetFrame) {
        long frameTime = targetFrame.getTimestamp();
        long windowStart = frameTime - HeatmapConfiguration.TIME_WINDOW_MS;
        
        List<MouseEvent> eventsInWindow = new ArrayList<>();
        List<VideoFrame> allFrames = session.getFrames();
        
        // Scan backward through frames to collect events in window
        for (int i = allFrames.indexOf(targetFrame); i >= 0; i--) {
            VideoFrame frame = allFrames.get(i);
            
            // Stop if we've gone past the window
            if (frame.getTimestamp() < windowStart) {
                break;
            }
            
            // Collect events from this frame that are in the window
            for (MouseEvent event : frame.getMouseEvents()) {
                if (event.getTimestamp() >= windowStart && event.getTimestamp() <= frameTime) {
                    eventsInWindow.add(event);
                }
            }
        }
        
        return eventsInWindow;
    }
    
    /**
     * Gets statistics about the generated heatmaps.
     *
     * @return Summary string with generation statistics
     */
    public String getStatistics() {
        return String.format("Output directory: %s\nFrames: %d\nWindow: %d seconds",
            outputDir.getAbsolutePath(),
            session.getFrameCount(),
            HeatmapConfiguration.TIME_WINDOW_MS / 1000);
    }
}