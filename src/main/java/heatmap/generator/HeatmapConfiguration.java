package heatmap.generator;

/**
 * Configuration constants for heatmap generation.
 */
public class HeatmapConfiguration {
    // Time window for event aggregation (10 seconds in milliseconds)
    public static final long TIME_WINDOW_MS = 10000;
    
    // Intensity calculation method
    public enum ScalingMethod {
        LINEAR,      // intensity = count / maxCount
        LOGARITHMIC  // intensity = log(count + 1) / log(maxCount + 1)
    }
    
    public static final ScalingMethod DEFAULT_SCALING = ScalingMethod.LOGARITHMIC;
    
    // Color gradient stops (intensity -> RGBA)
    // All colors have transparency (alpha < 255)
    public static final double[][] COLOR_STOPS = {
        {0.0,  0,   255, 0,   51},   // 0%: Transparent green (20% alpha)
        {0.33, 128, 255, 0,   77},   // 33%: Yellow-green (30% alpha)
        {0.66, 255, 165, 0,   102},  // 66%: Orange (40% alpha)
        {1.0,  255, 0,   0,   128}   // 100%: Red (50% alpha)
    };
    
    // Image format
    public static final String IMAGE_FORMAT = "PNG";
    public static final String OUTPUT_DIR_NAME = "heatmaps";
    public static final String FILE_NAME_PATTERN = "frame_%05d.png";
    
    private HeatmapConfiguration() {
        // Utility class
    }
}