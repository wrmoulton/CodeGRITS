package heatmap.model;

import java.awt.Rectangle;

/**
 * Represents a single zone in the heatmap grid.
 * Zones are immutable and identified by both a unique ID and grid coordinates.
 */
public class HeatmapZone {
    private final int zoneId;        // Unique identifier (0-224)
    private final int gridX;         // Column position (0-14)
    private final int gridY;         // Row position (0-14)
    private final Rectangle bounds;  // Screen coordinates of this zone

    /**
     * Creates a heatmap zone with grid coordinates and bounds.
     * The zone ID is automatically calculated as: gridY * 15 + gridX
     *
     * @param gridX  The column position in the grid (0-14)
     * @param gridY  The row position in the grid (0-14)
     * @param bounds The screen coordinate boundaries of this zone
     */
    public HeatmapZone(int gridX, int gridY, Rectangle bounds) {
        this.gridX = gridX;
        this.gridY = gridY;
        this.zoneId = gridY * 15 + gridX;
        this.bounds = new Rectangle(bounds); // Defensive copy
    }

    // Getters
    public int getZoneId() {
        return zoneId;
    }

    public int getGridX() {
        return gridX;
    }

    public int getGridY() {
        return gridY;
    }

    public Rectangle getBounds() {
        return new Rectangle(bounds); // Defensive copy
    }

    @Override
    public String toString() {
        return String.format("Zone{id=%d, grid=(%d,%d), bounds=[%d,%d,%d,%d]}",
                zoneId, gridX, gridY,
                bounds.x, bounds.y, bounds.width, bounds.height);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HeatmapZone that = (HeatmapZone) o;
        return zoneId == that.zoneId;
    }

    @Override
    public int hashCode() {
        return zoneId;
    }
}