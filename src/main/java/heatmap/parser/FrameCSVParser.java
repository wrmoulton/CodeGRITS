package heatmap.parser;

import com.opencsv.CSVReader;
import heatmap.model.VideoFrame;
import heatmap.validator.SessionDataException;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses frame timing data from frames.csv files.
 */
public class FrameCSVParser {

    /**
     * Parses video frames from the frames CSV file.
     *
     * @param csvFilePath Path to frames.csv
     * @return List of video frames
     * @throws SessionDataException if parsing fails
     */
    public List<VideoFrame> parseFrames(String csvFilePath) throws SessionDataException {
        List<VideoFrame> frames = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
            List<String[]> rows = reader.readAll();

            if (rows.isEmpty()) {
                throw new SessionDataException("frames.csv is empty");
            }

            // Skip header row
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);

                if (row.length < 3) {
                    System.err.println("Warning: Skipping malformed row at index " + i);
                    continue;
                }

                String timestampStr = row[0];
                String frameNumberStr = row[1];
                String clipNumberStr = row[2];

                // Skip special markers (Start, Pause, Resume, Stop)
                if (frameNumberStr.equals("Start") || frameNumberStr.equals("Pause") ||
                    frameNumberStr.equals("Resume") || frameNumberStr.equals("Stop")) {
                    continue;
                }

                try {
                    long timestamp = Long.parseLong(timestampStr);
                    int frameNumber = Integer.parseInt(frameNumberStr);
                    int clipNumber = Integer.parseInt(clipNumberStr);

                    VideoFrame frame = new VideoFrame(timestamp, frameNumber, clipNumber);
                    frames.add(frame);
                } catch (NumberFormatException e) {
                    System.err.println("Warning: Skipping invalid frame data at row " + i);
                }
            }

        } catch (Exception e) {
            throw new SessionDataException("Failed to parse frames.csv: " + e.getMessage(), e);
        }

        if (frames.isEmpty()) {
            throw new SessionDataException("No valid frames found in frames.csv");
        }

        return frames;
    }

    /**
     * Gets the start and end timestamps from the CSV file.
     * This reads the special markers (Start, Stop) to determine session duration.
     *
     * @param csvFilePath Path to frames.csv
     * @return Array of [startTimestamp, endTimestamp]
     * @throws SessionDataException if parsing fails
     */
    public long[] getSessionTimestamps(String csvFilePath) throws SessionDataException {
        long startTimestamp = 0;
        long endTimestamp = 0;

        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
            List<String[]> rows = reader.readAll();

            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                if (row.length < 3) continue;

                String timestampStr = row[0];
                String marker = row[1];

                if (marker.equals("Start")) {
                    startTimestamp = Long.parseLong(timestampStr);
                } else if (marker.equals("Stop")) {
                    endTimestamp = Long.parseLong(timestampStr);
                }
            }

        } catch (Exception e) {
            throw new SessionDataException("Failed to read session timestamps: " + e.getMessage(), e);
        }

        return new long[]{startTimestamp, endTimestamp};
    }
}