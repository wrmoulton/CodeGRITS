package heatmap.cli;

import heatmap.generator.HeatmapGenerator;
import heatmap.model.*;
import heatmap.parser.*;
import heatmap.sync.TimeWindowSynchronizer;
import heatmap.validator.*;

import java.util.List;

/**
 * Command-line tool for generating heatmap overlay images.
 */
public class GenerateHeatmaps {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java heatmap.cli.GenerateHeatmaps <session-folder-path>");
            System.out.println();
            System.out.println("Example:");
            System.out.println("  java heatmap.cli.GenerateHeatmaps 1763167078241");
            System.exit(1);
        }

        String sessionPath = args[0];

        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║         CodeGRITS Heatmap Generator v1.0                   ║");
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
            System.out.println("✅ Session structure valid");

            // 2. Parse metadata
            System.out.println("\n[2/5] Parsing metadata...");
            XMLParser xmlParser = new XMLParser();
            SessionMetadata metadata = xmlParser.parseEnvironmentData(
                    sessionPath + "/ide_tracking.xml"
            );
            System.out.println("✅ Metadata parsed");
            System.out.println("   Screen size: " + metadata.getScreenSize().width + "x" + 
                              metadata.getScreenSize().height);

            // 3. Parse mouse events
            System.out.println("\n[3/5] Parsing mouse events...");
            List<MouseEvent> mouseEvents = xmlParser.parseMouseEvents(
                    sessionPath + "/ide_tracking.xml"
            );
            System.out.println("✅ Found " + mouseEvents.size() + " mouse events");

            // 4. Parse frames
            System.out.println("\n[4/5] Parsing video frames...");
            FrameCSVParser csvParser = new FrameCSVParser();
            List<VideoFrame> frames = csvParser.parseFrames(
                    sessionPath + "/screen_recording/frames.csv"
            );
            System.out.println("✅ Found " + frames.size() + " video frames");

            // 5. Synchronize data
            System.out.println("\n[5/5] Synchronizing data...");
            TimeWindowSynchronizer synchronizer = new TimeWindowSynchronizer();
            HeatmapSession session = synchronizer.synchronize(metadata, frames, mouseEvents);
            System.out.println("✅ Synchronized " + session.getTotalMouseEvents() + " events to " + 
                              session.getFrameCount() + " frames");

            // 6. Generate heatmaps
            System.out.println("\n" + "=".repeat(60));
            System.out.println("GENERATING HEATMAPS");
            System.out.println("=".repeat(60) + "\n");

            HeatmapGenerator generator = new HeatmapGenerator(session, sessionPath);
            int processed = generator.generateAll();

            System.out.println("\n✅ Successfully generated " + processed + " heatmap images!");

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
}