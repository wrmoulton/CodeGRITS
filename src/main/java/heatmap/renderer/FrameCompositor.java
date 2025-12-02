package heatmap.renderer;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Handles compositing of heatmap overlays onto video frames.
 */
public class FrameCompositor {
    private final Java2DFrameConverter converter;
    private BufferedImage lastHeatmap;  // Cache last heatmap for missing frames
    
    public FrameCompositor() {
        this.converter = new Java2DFrameConverter();
        this.lastHeatmap = null;
    }
    
    /**
     * Composites a heatmap overlay onto a video frame.
     *
     * @param videoFrame  The original video frame from JavaCV
     * @param heatmapPath Path to the heatmap PNG file
     * @return Composited frame ready for encoding
     * @throws IOException If heatmap cannot be read
     */
    public Frame composite(Frame videoFrame, String heatmapPath) throws IOException {
        // Convert JavaCV Frame to BufferedImage
        BufferedImage videoImage = converter.convert(videoFrame);
        
        // Load heatmap (or use last one if missing)
        BufferedImage heatmap = loadHeatmap(heatmapPath);
        
        // Work directly on the video image
        Graphics2D g2d = videoImage.createGraphics();
        
        // Overlay heatmap with alpha blending (if available)
        if (heatmap != null) {
            // Use SrcOver composite for proper alpha blending
            g2d.setComposite(AlphaComposite.SrcOver);
            
            // Scale heatmap if dimensions don't match
            if (heatmap.getWidth() != videoImage.getWidth() || 
                heatmap.getHeight() != videoImage.getHeight()) {
                g2d.drawImage(heatmap, 0, 0, videoImage.getWidth(), videoImage.getHeight(), null);
            } else {
                g2d.drawImage(heatmap, 0, 0, null);
            }
        }
        
        g2d.dispose();
        
        // Convert back to JavaCV Frame
        return converter.convert(videoImage);
    }
    
    /**
     * Loads a heatmap from file, or returns the last loaded heatmap if file doesn't exist.
     *
     * @param heatmapPath Path to heatmap PNG
     * @return BufferedImage with alpha channel, or null if no heatmap available
     */
    private BufferedImage loadHeatmap(String heatmapPath) {
        File heatmapFile = new File(heatmapPath);
        
        if (heatmapFile.exists()) {
            try {
                lastHeatmap = ImageIO.read(heatmapFile);
                return lastHeatmap;
            } catch (IOException e) {
                System.err.println("Warning: Failed to load heatmap: " + heatmapPath);
                // Fall through to use last heatmap
            }
        }
        
        // Return last heatmap (may be null for first frame)
        return lastHeatmap;
    }
    
    /**
     * Releases resources.
     */
    public void close() {
        converter.close();
        lastHeatmap = null;
    }
}