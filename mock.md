
This Pull Request introduces three key features to improve the workflow for researchers conducting usability studies. These changes focus on real-time data visualization and session monitoring to reduce data loss and context switching.

**Heatmap Overlay**
**User Story:** As a researcher using CodeGRITS, I want to view a heatmap of the user's cursor locations via injection into the recorded MP4 so that I can analyze user patterns directly via video.
**What it does:** Implements a visual heatmap layer over the video recording area.
**Implementation:** Tracks cursor coordinates during the session and renders a frequency-based gradient (heatmap) directly on the UI canvas.
**Benefit:** Allows researchers to identify areas of high user focus (mouse dwell time) immediately without needing post-processing tools.

**Tracking Status & Timer**
**User Story:** As a researcher using CodeGRITS, I want a clear and always visible status indicator in the IDE status bar that shows whether tracking is active, paused, or stopped, along with an elapsed time timer and an optional session summary popup. 
**What it does:** Adds a dedicated status bar component containing a recording state indicator (Active/Paused/Stopped) and a session duration timer.
**Implementation:** Utilized a background thread to update the UI timer without blocking the main application thread.
Benefit: Prevents data loss by clearly signaling when recording is active and helps researchers manage session length.


**In-IDE XML Preview**
**User Story:** As a researcher using CodeGRITS, I want to view the generated XML output in a real-time preview panel directly in the IDE, so I can immediately validate that the tracking data is being collected and formatted correctly without stopping the experiment or manually searching for the file on my disk.
**What it does:** Creates a docked panel that displays the XML log data as it is being generated.
**Implementation:** Hooks into the data-collection event stream to mirror its output to a text pane in real time.
Benefit: Enables immediate validation of data integrity. Researchers no longer need to navigate the file system and open external text editors to ensure data is being captured correctly.
