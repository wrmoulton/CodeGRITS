package heatmap.validator;

import java.io.File;

/**
 * Validates that a CodeGRITS session folder contains all required files
 * and has proper structure for heatmap generation.
 */
public class SessionValidator {

    /**
     * Validates a session folder.
     *
     * @param sessionPath Path to the session folder
     * @return ValidationResult containing any errors or warnings
     */
    public ValidationResult validate(String sessionPath) {
        ValidationResult result = new ValidationResult();

        // Check if session folder exists
        File sessionDir = new File(sessionPath);
        if (!sessionDir.exists()) {
            result.addError("Session folder does not exist: " + sessionPath);
            return result;
        }

        if (!sessionDir.isDirectory()) {
            result.addError("Session path is not a directory: " + sessionPath);
            return result;
        }

        // Check for ide_tracking.xml
        File ideTrackingFile = new File(sessionDir, "ide_tracking.xml");
        if (!ideTrackingFile.exists()) {
            result.addError("Missing ide_tracking.xml file");
        } else if (!ideTrackingFile.canRead()) {
            result.addError("Cannot read ide_tracking.xml file");
        }

        // Check for screen_recording folder
        File screenRecordingDir = new File(sessionDir, "screen_recording");
        if (!screenRecordingDir.exists()) {
            result.addError("Missing screen_recording folder");
            return result; // Can't continue without this
        }

        if (!screenRecordingDir.isDirectory()) {
            result.addError("screen_recording is not a directory");
            return result;
        }

        // Check for frames.csv
        File framesFile = new File(screenRecordingDir, "frames.csv");
        if (!framesFile.exists()) {
            result.addError("Missing frames.csv file in screen_recording folder");
        } else if (!framesFile.canRead()) {
            result.addError("Cannot read frames.csv file");
        }

        // Check for at least one video clip
        File[] mp4Files = screenRecordingDir.listFiles((dir, name) -> 
            name.startsWith("clip_") && name.endsWith(".mp4"));
        
        if (mp4Files == null || mp4Files.length == 0) {
            result.addWarning("No video clips (clip_*.mp4) found in screen_recording folder");
        }

        // Check for archives folder (optional, but common)
        File archivesDir = new File(sessionDir, "archives");
        if (!archivesDir.exists()) {
            result.addWarning("Archives folder not found (optional)");
        }

        return result;
    }

    /**
     * Validates and throws an exception if validation fails.
     *
     * @param sessionPath Path to the session folder
     * @throws SessionDataException if validation fails
     */
    public void validateOrThrow(String sessionPath) throws SessionDataException {
        ValidationResult result = validate(sessionPath);
        if (!result.isValid()) {
            StringBuilder message = new StringBuilder("Session validation failed:\n");
            for (String error : result.getErrors()) {
                message.append("  - ").append(error).append("\n");
            }
            throw new SessionDataException(message.toString());
        }
    }
}