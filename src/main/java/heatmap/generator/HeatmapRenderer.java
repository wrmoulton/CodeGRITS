package heatmap.generator;

import heatmap.model.HeatmapZone;
import heatmap.sync.ZoneMapper;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;

/**
 * Renders heatmap images from zone intensities.
 */
public class HeatmapRenderer {
    private final int width;
    private final int height;
    private final ZoneMapper zoneMapper;
    
    /**
     * Creates a heatmap renderer with the specified dimensions.
     *
     * @param width      Image width (screen width)
     * @param height     Image height (screen height)
     * @param zoneMapper Zone mapper for coordinate mapping
     */
    public HeatmapRenderer(int width, int height, ZoneMapper zoneMapper) {
        this.width = width;
        this.height = height;
        this.zoneMapper = zoneMapper;
    }
    
    /**
     * Renders a heatmap image from zone intensities.
     * All zones are rendered with color, even those with 0 intensity.
     *
     * @param zoneIntensities Map of zone ID to intensity (0.0 to 1.0)
     * @return BufferedImage with ARGB color model
     */
    public BufferedImage render(Map<Integer, Double> zoneIntensities) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        // Enable anti-aliasing for smoother rendering
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Start with fully transparent background
        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, width, height);
        g2d.setComposite(AlphaComposite.SrcOver);
        
        // Render ALL zones with color (even 0 intensity zones)
        for (HeatmapZone zone : zoneMapper.getAllZones()) {
            int zoneId = zone.getZoneId();
            double intensity = zoneIntensities.getOrDefault(zoneId, 0.0);
            
            // Always render color, even for 0 intensity
            Color color = getColorForIntensity(intensity);
            g2d.setColor(color);
            
            Rectangle bounds = zone.getBounds();
            g2d.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        }
        
        g2d.dispose();
        return image;
    }
    
    /**
     * Maps intensity to color using gradient interpolation.
     * Color scheme: Green (low) → Yellow → Orange → Red (high)
     * All colors have transparency.
     *
     * @param intensity Normalized intensity (0.0 to 1.0)
     * @return Color with alpha channel
     */
    private Color getColorForIntensity(double intensity) {
        // Clamp intensity to valid range
        intensity = Math.max(0.0, Math.min(1.0, intensity));
        
        // Find surrounding color stops
        double[][] stops = HeatmapConfiguration.COLOR_STOPS;
        
        for (int i = 0; i < stops.length - 1; i++) {
            double lower = stops[i][0];
            double upper = stops[i + 1][0];
            
            if (intensity >= lower && intensity <= upper) {
                // Interpolate between these two stops
                double t = (intensity - lower) / (upper - lower);
                
                int r = (int) lerp(stops[i][1], stops[i + 1][1], t);
                int g = (int) lerp(stops[i][2], stops[i + 1][2], t);
                int b = (int) lerp(stops[i][3], stops[i + 1][3], t);
                int a = (int) lerp(stops[i][4], stops[i + 1][4], t);
                
                return new Color(r, g, b, a);
            }
        }
        
        // Fallback to last color stop
        double[] last = stops[stops.length - 1];
        return new Color((int) last[1], (int) last[2], (int) last[3], (int) last[4]);
    }
    
    /**
     * Linear interpolation between two values.
     *
     * @param a Start value
     * @param b End value
     * @param t Interpolation factor (0.0 to 1.0)
     * @return Interpolated value
     */
    private double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }
}