package heatmap.generator;

import heatmap.model.HeatmapZone;
import heatmap.model.MouseEvent;
import heatmap.model.VideoFrame;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Calculates heat intensity for zones based on mouse event counts.
 */
public class ZoneHeatCalculator {
    private final HeatmapConfiguration.ScalingMethod scalingMethod;
    
    /**
     * Creates a heat calculator with the specified scaling method.
     *
     * @param scalingMethod The intensity scaling method
     */
    public ZoneHeatCalculator(HeatmapConfiguration.ScalingMethod scalingMethod) {
        this.scalingMethod = scalingMethod;
    }
    
    /**
     * Calculates normalized heat intensities for all zones based on mouse events
     * within the time window.
     *
     * @param eventsInWindow All mouse events within the time window
     * @return Map of zone ID to normalized intensity (0.0 to 1.0)
     */
    public Map<Integer, Double> calculateZoneIntensities(List<MouseEvent> eventsInWindow) {
        // Count events per zone
        Map<Integer, Integer> zoneCounts = new HashMap<>();
        
        for (MouseEvent event : eventsInWindow) {
            if (event.hasZone()) {
                int zoneId = event.getZone().getZoneId();
                zoneCounts.put(zoneId, zoneCounts.getOrDefault(zoneId, 0) + 1);
            }
        }
        
        if (zoneCounts.isEmpty()) {
            return new HashMap<>();
        }
        
        // Find maximum count for normalization
        int maxCount = zoneCounts.values().stream()
                .max(Integer::compareTo)
                .orElse(1);
        
        // Calculate normalized intensities
        Map<Integer, Double> intensities = new HashMap<>();
        
        for (Map.Entry<Integer, Integer> entry : zoneCounts.entrySet()) {
            int zoneId = entry.getKey();
            int count = entry.getValue();
            double intensity = calculateIntensity(count, maxCount);
            intensities.put(zoneId, intensity);
        }
        
        return intensities;
    }
    
    /**
     * Calculates normalized intensity for a given count.
     *
     * @param count    The event count
     * @param maxCount The maximum count across all zones
     * @return Normalized intensity (0.0 to 1.0)
     */
    private double calculateIntensity(int count, int maxCount) {
        if (maxCount == 0) {
            return 0.0;
        }
        
        switch (scalingMethod) {
            case LINEAR:
                return (double) count / maxCount;
                
            case LOGARITHMIC:
                // Logarithmic scaling for better distribution
                return Math.log1p(count) / Math.log1p(maxCount);
                
            default:
                return (double) count / maxCount;
        }
    }
}