package heatmap.renderer;

import heatmap.model.HeatmapSession;
import heatmap.model.VideoFrame;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Renders video with heatmap overlays by processing all clips and concatenating them.
 */
public class VideoOverlayRenderer {
    private final HeatmapSession session;
    private final String sessionPath;
    private final ClipFrameMapper frameMapper;
    private final FrameCompositor compositor;
    
    /**
     * Creates a video overlay renderer for the specified session.
     *
     * @param session     The heatmap session with frame data
     * @param sessionPath Base path to the session folder
     */
    public VideoOverlayRenderer(HeatmapSession session, String sessionPath) {
        this.session = session;
        this.sessionPath = sessionPath;
        this.frameMapper = new ClipFrameMapper(
            session.getFrames(),
            sessionPath + "/heatmaps"
        );
        this.compositor = new FrameCompositor();
    }
    
    /**
     * Renders a single merged video with heatmap overlays from all clips.
     *
     * @return Path to the output video file
     * @throws Exception If rendering fails
     */
    public String renderMergedVideo() throws Exception {
        // Get all clip numbers
        Set<Integer> clipNumbers = session.getFrames().stream()
            .map(VideoFrame::getClipNumber)
            .collect(Collectors.toSet());
        
        List<Integer> sortedClips = new ArrayList<>(clipNumbers);
        Collections.sort(sortedClips);
        
        System.out.println("\nRendering video with heatmap overlays...");
        System.out.println("Found " + sortedClips.size() + " clip(s) to process");
        System.out.println();
        
        String outputPath = sessionPath + "/screen_recording/recording_with_heatmap.mp4";
        
        // Set up output recorder for merged video
        FFmpegFrameRecorder recorder = null;
        int totalFramesProcessed = 0;
        long startTime = System.currentTimeMillis();
        
        try {
            for (int clipNum : sortedClips) {
                String inputClipPath = sessionPath + "/screen_recording/clip_" + clipNum + ".mp4";
                File inputFile = new File(inputClipPath);
                
                if (!inputFile.exists()) {
                    System.err.println("Warning: Clip file not found: " + inputClipPath);
                    continue;
                }
                
                System.out.println("Processing clip " + clipNum + "...");
                
                // Open input video
                FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputClipPath);
                grabber.start();
                
                // Initialize recorder on first clip
                if (recorder == null) {
                    recorder = new FFmpegFrameRecorder(
                        outputPath,
                        grabber.getImageWidth(),
                        grabber.getImageHeight()
                    );
                    recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
                    recorder.setFrameRate(session.getMetadata().getFrameRate());
                    recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P); // Match input format
                    recorder.setVideoBitrate(grabber.getVideoBitrate() > 0 ? 
                                            grabber.getVideoBitrate() : 2000000);
                    recorder.start();
                }
                
                // Process each frame in the clip
                int frameNum = 0;
                Frame frame;
                
                while ((frame = grabber.grab()) != null) {
                    if (frame.image == null) {
                        continue; // Skip audio or empty frames
                    }
                    
                    frameNum++;
                    totalFramesProcessed++;
                    
                    // Get global frame number
                    int globalFrame = frameMapper.getGlobalFrameNumber(clipNum, frameNum);
                    
                    if (globalFrame > 0) {
                        // Get heatmap path and composite
                        String heatmapPath = frameMapper.getHeatmapPath(globalFrame);
                        Frame compositedFrame = compositor.composite(frame, heatmapPath);
                        recorder.record(compositedFrame);
                    } else {
                        // Frame not in mapping, just write original
                        recorder.record(frame);
                    }
                    
                    // Progress indicator
                    if (totalFramesProcessed % 50 == 0) {
                        double progress = (totalFramesProcessed * 100.0) / frameMapper.getTotalFrames();
                        System.out.printf("\rProgress: %d/%d frames (%.1f%%)    ",
                            totalFramesProcessed, frameMapper.getTotalFrames(), progress);
                    }
                }
                
                grabber.stop();
                grabber.release();
                
                System.out.println("\nClip " + clipNum + " complete (" + frameNum + " frames)");
            }
            
            System.out.println("\n" + "=".repeat(60));
            System.out.println("Finalizing video...");
            
        } finally {
            if (recorder != null) {
                recorder.stop();
                recorder.release();
            }
            compositor.close();
        }
        
        long totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Video rendering complete!");
        System.out.printf("Total time: %.2f seconds\n", totalTime / 1000.0);
        System.out.printf("Processed %d frames\n", totalFramesProcessed);
        System.out.println("Output: " + outputPath);
        
        return outputPath;
    }
}