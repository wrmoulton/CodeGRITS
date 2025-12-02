package heatmap.sync;

import heatmap.model.HeatmapZone;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * Maps screen coordinates to heatmap zones using a 15x15 adaptive grid.
 * The grid adapts to any screen resolution by calculating cell sizes dynamically.
 */
public class ZoneMapper {
    private static final int GRID_SIZE = 15;  // 15x15 grid = 225 zones

    private final int cellWidth;
    private final int cellHeight;
    private final int screenWidth;
    private final int screenHeight;
    private final List<HeatmapZone> allZones;  // Pre-computed 225 zones

    /**
     * Creates a zone mapper for the given screen dimensions.
     * Cell dimensions are calculated by rounding up: ceil(screenDimension / 15)
     *
     * @param screenSize The screen dimensions
     */
    public ZoneMapper(Dimension screenSize) {
        this.screenWidth = screenSize.width;
        this.screenHeight = screenSize.height;

        // Calculate cell dimensions (round up to ensure full coverage)
        this.cellWidth = (int) Math.ceil((double) screenWidth / GRID_SIZE);
        this.cellHeight = (int) Math.ceil((double) screenHeight / GRID_SIZE);

        // Pre-generate all 225 zones
        this.allZones = generateAllZones();
    }

    /**
     * Gets the zone for the given screen coordinates.
     *
     * @param screenX The X coordinate on screen
     * @param screenY The Y coordinate on screen
     * @return The zone containing these coordinates, or null if out of bounds
     */
    public HeatmapZone getZoneForCoordinates(int screenX, int screenY) {
        // Bounds check
        if (screenX < 0 || screenX >= screenWidth ||
            screenY < 0 || screenY >= screenHeight) {
            return null;
        }

        // Calculate grid position
        int gridX = Math.min(screenX / cellWidth, GRID_SIZE - 1);
        int gridY = Math.min(screenY / cellHeight, GRID_SIZE - 1);

        // Return pre-computed zone
        int zoneId = gridY * GRID_SIZE + gridX;
        return allZones.get(zoneId);
    }

    /**
     * Pre-generates all 225 zones with their boundaries.
     *
     * @return List of all zones in the grid
     */
    private List<HeatmapZone> generateAllZones() {
        List<HeatmapZone> zones = new ArrayList<>(GRID_SIZE * GRID_SIZE);

        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                int x1 = x * cellWidth;
                int y1 = y * cellHeight;
                int x2 = Math.min(x1 + cellWidth, screenWidth);
                int y2 = Math.min(y1 + cellHeight, screenHeight);

                Rectangle bounds = new Rectangle(x1, y1, x2 - x1, y2 - y1);
                zones.add(new HeatmapZone(x, y, bounds));
            }
        }

        return zones;
    }

    /**
     * Gets all zones in the grid.
     *
     * @return Defensive copy of all 225 zones
     */
    public List<HeatmapZone> getAllZones() {
        return new ArrayList<>(allZones);
    }

    public int getCellWidth() {
        return cellWidth;
    }

    public int getCellHeight() {
        return cellHeight;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public static int getGridSize() {
        return GRID_SIZE;
    }

    @Override
    public String toString() {
        return String.format("ZoneMapper{grid=%dx%d, cells=%dx%d, screen=%dx%d}",
                GRID_SIZE, GRID_SIZE, cellWidth, cellHeight, screenWidth, screenHeight);
    }
}