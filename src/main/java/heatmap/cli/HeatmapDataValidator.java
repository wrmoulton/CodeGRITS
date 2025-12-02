package heatmap.cli;

import heatmap.model.*;
import heatmap.parser.*;
import heatmap.sync.TimeWindowSynchronizer;
import heatmap.sync.ZoneMapper;
import heatmap.validator.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Command-line tool for validating and testing CodeGRITS session data.
 * This tool parses session data and displays statistics about the session.
 */
public class HeatmapDataValidator {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java heatmap.cli.HeatmapDataValidator <session-folder-path>");
            System.out.println();
            System.out.println("Example:");
            System.out.println("  java heatmap.cli.HeatmapDataValidator 1763167078241");
            System.exit(1);
        }

        String sessionPath = args[0];

        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║      CodeGRITS Heatmap Data Validator v1.0                 ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Session: " + sessionPath);
        System.out.println();

        try {
            // 1. Validate session structure
            System.out.println("[1/5] Validating session structure...");
            SessionValidator validator = new SessionValidator();
            ValidationResult result = validator.validate(sessionPath);

            if (!result.isValid()) {
                System.err.println("❌ Validation failed:");
                for (String error : result.getErrors()) {
                    System.err.println("  ✗ " + error);
                }
                System.exit(1);
            }

            if (result.hasWarnings()) {
                for (String warning : result.getWarnings()) {
                    System.out.println("  ⚠ " + warning);
                }
            }
            System.out.println("✅ Session structure valid");

            // 2. Parse metadata
            System.out.println("\n[2/5] Parsing metadata...");
            XMLParser xmlParser = new XMLParser();
            SessionMetadata metadata = xmlParser.parseEnvironmentData(
                    sessionPath + "/ide_tracking.xml"
            );
            System.out.println("✅ Metadata parsed successfully");
            System.out.println("   Screen size: " + metadata.getScreenSize().width + "x" + metadata.getScreenSize().height);
            System.out.println("   Scale: " + metadata.getScaleX() + "x, " + metadata.getScaleY() + "y");
            System.out.println("   IDE: " + metadata.getIdeName() + " " + metadata.getIdeVersion());
            System.out.println("   OS: " + metadata.getOsName());
            System.out.println("   Project: " + metadata.getProjectName());

            // 3. Parse mouse events
            System.out.println("\n[3/5] Parsing mouse events...");
            List<MouseEvent> mouseEvents = xmlParser.parseMouseEvents(
                    sessionPath + "/ide_tracking.xml"
            );
            System.out.println("✅ Found " + mouseEvents.size() + " mouse events");

            if (!mouseEvents.isEmpty()) {
                System.out.println("   First event: " + mouseEvents.get(0).getId() +
                        " at t=" + mouseEvents.get(0).getTimestamp());
                System.out.println("   Last event: " + mouseEvents.get(mouseEvents.size() - 1).getId() +
                        " at t=" + mouseEvents.get(mouseEvents.size() - 1).getTimestamp());

                // Count event types
                long moveCount = mouseEvents.stream().filter(e -> e.getId().equals("mouseMoved")).count();
                long clickCount = mouseEvents.stream().filter(e -> e.getId().equals("mouseClicked")).count();
                long pressCount = mouseEvents.stream().filter(e -> e.getId().equals("mousePressed")).count();
                long dragCount = mouseEvents.stream().filter(e -> e.getId().equals("mouseDragged")).count();

                System.out.println("   Event types: " + moveCount + " moves, " + clickCount + " clicks, " +
                        pressCount + " presses, " + dragCount + " drags");
            }

            // 4. Parse frames
            System.out.println("\n[4/5] Parsing video frames...");
            FrameCSVParser csvParser = new FrameCSVParser();
            List<VideoFrame> frames = csvParser.parseFrames(
                    sessionPath + "/screen_recording/frames.csv"
            );
            System.out.println("✅ Found " + frames.size() + " video frames");
            System.out.println("   Frame rate: " + metadata.getFrameRate() + " fps");

            if (!frames.isEmpty()) {
                System.out.println("   First frame: #" + frames.get(0).getFrameNumber() +
                        " at t=" + frames.get(0).getTimestamp());
                System.out.println("   Last frame: #" + frames.get(frames.size() - 1).getFrameNumber() +
                        " at t=" + frames.get(frames.size() - 1).getTimestamp());

                // Get session duration
                long[] timestamps = csvParser.getSessionTimestamps(sessionPath + "/screen_recording/frames.csv");
                if (timestamps[0] > 0 && timestamps[1] > 0) {
                    long durationMs = timestamps[1] - timestamps[0];
                    System.out.println("   Duration: " + (durationMs / 1000.0) + " seconds");
                }
            }

            // 5. Synchronize data
            System.out.println("\n[5/5] Synchronizing mouse events to frames and assigning zones...");
            TimeWindowSynchronizer synchronizer = new TimeWindowSynchronizer();
            HeatmapSession session = synchronizer.synchronize(metadata, frames, mouseEvents);

            // Print statistics
            System.out.println("\n╔════════════════════════════════════════════════════════════╗");
            System.out.println("║                  Synchronization Results                   ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝");
            System.out.println();
            System.out.println("Total frames:                " + session.getFrameCount());
            System.out.println("Frames with mouse activity:  " + session.getActiveFrameCount() +
                    " (" + String.format("%.1f%%", (session.getActiveFrameCount() * 100.0 / session.getFrameCount())) + ")");
            System.out.println("Total mouse events:          " + session.getTotalMouseEvents());

            if (session.getActiveFrameCount() > 0) {
                double avgEvents = (double) session.getTotalMouseEvents() / session.getActiveFrameCount();
                System.out.println("Avg events per active frame: " + String.format("%.1f", avgEvents));
            }

            // Zone Analysis
            System.out.println("\n╔════════════════════════════════════════════════════════════╗");
            System.out.println("║                    Zone Analysis                           ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝");
            System.out.println();

            ZoneMapper zoneMapper = session.getZoneMapper();
            if (zoneMapper != null) {
                System.out.println("Grid configuration:");
                System.out.println("  Grid size: " + ZoneMapper.getGridSize() + "x" + ZoneMapper.getGridSize() +
                                  " (" + (ZoneMapper.getGridSize() * ZoneMapper.getGridSize()) + " total zones)");
                System.out.println("  Cell size: " + zoneMapper.getCellWidth() + "x" +
                                  zoneMapper.getCellHeight() + " pixels");
                System.out.println("  Screen: " + metadata.getScreenSize().width + "x" +
                                  metadata.getScreenSize().height);

                // Count events per zone
                Map<Integer, Integer> zoneEventCounts = new HashMap<>();
                int mappedEvents = 0;
                for (VideoFrame frame : session.getFrames()) {
                    for (MouseEvent event : frame.getMouseEvents()) {
                        if (event.hasZone()) {
                            mappedEvents++;
                            int zoneId = event.getZone().getZoneId();
                            zoneEventCounts.put(zoneId, zoneEventCounts.getOrDefault(zoneId, 0) + 1);
                        }
                    }
                }

                System.out.println("\nEvent distribution:");
                System.out.println("  Events mapped to zones: " + mappedEvents + " / " +
                                  session.getTotalMouseEvents());
                System.out.println("  Unique zones with activity: " + zoneEventCounts.size() +
                                  " / " + (ZoneMapper.getGridSize() * ZoneMapper.getGridSize()));

                // Show top 5 hottest zones
                if (!zoneEventCounts.isEmpty()) {
                    System.out.println("\nTop 5 hottest zones:");
                    zoneEventCounts.entrySet().stream()
                            .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                            .limit(5)
                            .forEach(entry -> {
                                int zoneId = entry.getKey();
                                int count = entry.getValue();
                                int gridX = zoneId % ZoneMapper.getGridSize();
                                int gridY = zoneId / ZoneMapper.getGridSize();
                                System.out.printf("  Zone %3d (grid %2d,%2d): %4d events\n",
                                                 zoneId, gridX, gridY, count);
                            });
                    
                    // Detailed count grid
                    System.out.println("\nDetailed Event Counts per Zone:");
                    printCountGrid(zoneEventCounts);
                }
            } else {
                System.out.println("⚠ Zone mapper not initialized");
            }

            // Show sample mapping
            System.out.println("\n╔════════════════════════════════════════════════════════════╗");
            System.out.println("║                  Sample Frame Mapping                      ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝");
            System.out.println();
            for (int i = 0; i < Math.min(10, session.getFrames().size()); i++) {
                VideoFrame frame = session.getFrames().get(i);
                System.out.printf("Frame %-3d (clip %d, t=%-13d): %3d mouse events\n",
                        frame.getFrameNumber(),
                        frame.getClipNumber(),
                        frame.getTimestamp(),
                        frame.getMouseEventCount());
            }

            if (session.getFrames().size() > 10) {
                System.out.println("... (" + (session.getFrames().size() - 10) + " more frames)");
            }

            System.out.println("\n✅ All data parsed and synchronized successfully!");

        } catch (SessionDataException e) {
            System.err.println("\n❌ Error: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("   Cause: " + e.getCause().getMessage());
            }
            System.exit(1);
        } catch (Exception e) {
            System.err.println("\n❌ Unexpected error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Prints a detailed grid showing the exact event count for each zone.
     * Numbers are right-aligned for easy reading.
     *
     * @param zoneEventCounts Map of zone IDs to event counts
     */
    private static void printCountGrid(Map<Integer, Integer> zoneEventCounts) {
        int gridSize = ZoneMapper.getGridSize();
        
        // Find max count to determine column width
        int maxCount = zoneEventCounts.values().stream()
                .max(Integer::compareTo)
                .orElse(0);
        int columnWidth = Math.max(3, String.valueOf(maxCount).length());
        
        // Print column header (X coordinates)
        System.out.print("     ");  // Space for row numbers
        for (int x = 0; x < gridSize; x++) {
            System.out.printf("%" + columnWidth + "d ", x);
        }
        System.out.println();
        
        // Print top border
        System.out.print("   ┌");
        for (int x = 0; x < gridSize; x++) {
            for (int i = 0; i < columnWidth; i++) {
                System.out.print("─");
            }
            if (x < gridSize - 1) {
                System.out.print("─");
            }
        }
        System.out.println("┐");

        // Print grid with counts
        for (int y = 0; y < gridSize; y++) {
            System.out.printf("%2d │", y);  // Row number
            for (int x = 0; x < gridSize; x++) {
                int zoneId = y * gridSize + x;
                int count = zoneEventCounts.getOrDefault(zoneId, 0);
                
                if (count == 0) {
                    // Print dash for empty zones
                    System.out.printf("%" + columnWidth + "s ", "-");
                } else {
                    // Print count
                    System.out.printf("%" + columnWidth + "d ", count);
                }
            }
            System.out.println("│");
        }

        // Print bottom border
        System.out.print("   └");
        for (int x = 0; x < gridSize; x++) {
            for (int i = 0; i < columnWidth; i++) {
                System.out.print("─");
            }
            if (x < gridSize - 1) {
                System.out.print("─");
            }
        }
        System.out.println("┘");
    }
}