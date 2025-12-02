package components;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ui.JBUI;
import org.w3c.dom.Document;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Extended XML preview panel with file browsing capabilities.
 * Allows users to browse and select XML files to preview.
 */
public class XMLFilePreviewPanel extends JPanel {

    private final XMLPreviewPanel previewPanel;
    private final JLabel filePathLabel;
    private final Project project;

    public XMLFilePreviewPanel(Project project) {
        this.project = project;
        setLayout(new BorderLayout());
        setBorder(JBUI.Borders.empty(8));

        // Top panel with file selection controls
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton browseButton = new JButton("Browse XML File");
        browseButton.addActionListener(e -> browseXMLFile());
        topPanel.add(browseButton);

        filePathLabel = new JLabel("No file selected");
        topPanel.add(filePathLabel);

        add(topPanel, BorderLayout.NORTH);

        // Preview panel
        previewPanel = new XMLPreviewPanel();
        add(previewPanel, BorderLayout.CENTER);
    }


    /**
     * Load and display an XML file.
     *
     * @param filePath The path to the XML file.
     */
    public void loadXMLFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            previewPanel.displayXMLString("Error: File not found: " + filePath);
            filePathLabel.setText("File not found");
            return;
        }

        try {
            javax.xml.parsers.DocumentBuilderFactory dbf = 
                    javax.xml.parsers.DocumentBuilderFactory.newInstance();
            javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            
            previewPanel.displayDocument(doc);
            filePathLabel.setText("Loaded: " + filePath);
        } catch (Exception e) {
            previewPanel.displayXMLString("Error loading file:\n" + e.getMessage());
            filePathLabel.setText("Error: " + e.getMessage());
        }
    }

    /**
     * Get the preview panel.
     *
     * @return The preview panel.
     */
    public XMLPreviewPanel getPreviewPanel() {
        return previewPanel;
    }
}

 /**
     * Open a file browser dialog to select an XML file.
     */
    private void browseXMLFile() {
        FileChooserDescriptor descriptor = new FileChooserDescriptor(true, false, false, false, false, false) {
            @Override
            public boolean isFileVisible(VirtualFile file, boolean showHiddenFiles) {
                return file.isDirectory() || file.getName().endsWith(".xml");
            }
        };
        descriptor.setTitle("Select XML File");
        descriptor.setDescription("Choose an XML file to preview");

        VirtualFile selected = FileChooser.chooseFile(descriptor, project, null);
        if (selected != null && !selected.isDirectory()) {
            loadXMLFile(selected.getPath());
        }
    }

    
/**
 * INTEGRATION EXAMPLES FOR XML PREVIEW FEATURE
 * 
 * This file shows how to integrate the XML preview functionality
 * with existing CodeGRITS trackers and components.
 */

// ============================================================================
// EXAMPLE 1: Integrating XMLPreviewDataHandler with IDETracker
// ============================================================================

/**
 * In IDETracker.java, add the following import:
 * import api.XMLPreviewDataHandler;
 */

// In the startTracking() method, after initializing tracking:
/*
    public void startTracking(Project project) {
        isTracking = true;
        
        // ... existing tracking setup code ...
        
        // Initialize XML preview handler
        XMLPreviewDataHandler previewHandler = XMLPreviewDataHandler.getInstance();
        previewHandler.initialize(project);
        
        // Send initial document
        iDETracking.appendChild(root);
        previewHandler.onIDETrackingUpdate(iDETracking);
    }
*/

// In methods that add elements to the tracking document:
/*
    private Element getMouseElement(EditorMouseEvent e, String eventType) {
        // ... existing code ...
        
        Element mouseElement = iDETracking.createElement("mouse");
        // ... populate element ...
        mouses.appendChild(mouseElement);
        
        // Update preview with new element
        XMLPreviewDataHandler handler = XMLPreviewDataHandler.getInstance();
        handler.onIDEElementAdded(mouseElement);
        handler.onIDETrackingUpdate(iDETracking);
        
        return mouseElement;
    }
*/

// In stopTracking():
/*
    public void stopTracking() throws TransformerException {
        isTracking = false;
        
        // ... existing cleanup code ...
        
        String filePath = dataOutputPath + "/ide_tracking.xml";
        XMLWriter.writeToXML(iDETracking, filePath);
        
        // Send final document to preview
        XMLPreviewDataHandler handler = XMLPreviewDataHandler.getInstance();
        handler.onIDETrackingUpdate(iDETracking);
        handler.reset();
    }
*/

// ============================================================================
// EXAMPLE 2: Integrating XMLPreviewDataHandler with EyeTracker
// ============================================================================

/**
 * In EyeTracker.java, add the following import:
 * import api.XMLPreviewDataHandler;
 */

// In the stopTracking() method:
/*
    public void stopTracking() throws TransformerException {
        // ... existing code ...
        
        XMLWriter.writeToXML(eyeTracking, dataOutputPath + "/eye_tracking.xml");
        
        // Send to preview
        XMLPreviewDataHandler handler = XMLPreviewDataHandler.getInstance();
        handler.onEyeTrackingUpdate(eyeTracking);
    }
*/

// ============================================================================
// EXAMPLE 3: Opening Preview from an Action
// ============================================================================

/**
 * In XMLPreviewAction.java or any other action:
 */
/*
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;

        // Option 1: Show stored documents from real-time handler
        XMLPreviewDataHandler handler = XMLPreviewDataHandler.getInstance();
        Document ideDoc = handler.getCurrentIDEDocument();
        
        // Option 2: Create new preview window
        XMLPreviewWindow window = new XMLPreviewWindow(project);
        
        if (ideDoc != null) {
            window.displayIDETrackingXML(ideDoc);
        }
        
        window.show();
    }
*/

// ============================================================================
// EXAMPLE 4: Loading XML Files in Preview
// ============================================================================

/**
 * Simple method to load and display an XML file:
 */
/*
    private void displayXMLFile(String filePath) {
        // Validate and parse the file
        if (!XMLValidator.validateXMLFile(filePath)) {
            String error = XMLValidator.getValidationErrorMessage(filePath);
            JOptionPane.showMessageDialog(null, "Invalid XML: " + error);
            return;
        }
        
        // Parse the document
        Document doc = XMLValidator.parseXMLFile(filePath);
        if (doc == null) {
            JOptionPane.showMessageDialog(null, "Failed to parse XML file");
            return;
        }
        
        // Display in preview
        XMLPreviewWindow window = new XMLPreviewWindow(project);
        
        // Determine file type and display accordingly
        if (filePath.contains("ide_tracking")) {
            window.displayIDETrackingXML(doc);
        } else if (filePath.contains("eye_tracking")) {
            window.displayEyeTrackingXML(doc);
        } else {
            window.displayCombinedXML(XMLValidator.documentToFormattedString(doc));
        }
        
        window.show();
    }
*/

// ============================================================================
// EXAMPLE 5: Real-Time Preview during Tracking Session
// ============================================================================

/**
 * System to show live preview updates during tracking:
 */
/*
    // In a toolbar or status widget
    private void showRealTimePreview() {
        XMLPreviewDataHandler handler = XMLPreviewDataHandler.getInstance();
        
        // Periodically get current documents
        Timer timer = new Timer(1000, e -> {  // Update every second
            Document ideDoc = handler.getCurrentIDEDocument();
            
            if (ideDoc != null && previewPanel != null) {
                previewPanel.displayDocument(ideDoc);
            }
        });
        
        timer.start();
    }
*/

// ============================================================================
// EXAMPLE 6: Batch Processing XML Files
// ============================================================================

/**
 * Process multiple XML files from a tracking session:
 */
/*
    private void processBatchXML(String sessionDirectory) {
        File dir = new File(sessionDirectory);
        File[] xmlFiles = dir.listFiles((d, name) -> name.endsWith(".xml"));
        
        if (xmlFiles == null) return;
        
        XMLPreviewWindow window = new XMLPreviewWindow(project);
        
        for (File xmlFile : xmlFiles) {
            // Validate each file
            if (!XMLValidator.validateXMLFile(xmlFile.getPath())) {
                System.err.println("Invalid XML: " + xmlFile.getName());
                continue;
            }
            
            // Parse and process
            Document doc = XMLValidator.parseXMLFile(xmlFile.getPath());
            int lineCount = XMLValidator.getLineCount(doc);
            
            // Display in preview
            if (xmlFile.getName().contains("ide_tracking")) {
                window.displayIDETrackingXML(doc);
            } else if (xmlFile.getName().contains("eye_tracking")) {
                window.displayEyeTrackingXML(doc);
            }
            
            System.out.println(String.format("Loaded %s (%d lines)", 
                xmlFile.getName(), lineCount));
        }
        
        window.show();
    }
*/

// ============================================================================
// EXAMPLE 7: Custom XML Visualization
// ============================================================================

/**
 * Creating a custom preview with analysis:
 */
/*
    private String createAnalysisView(Document ideDoc, Document eyeDoc) {
        StringBuilder analysis = new StringBuilder();
        
        analysis.append("=== CodeGRITS Session Analysis ===\n\n");
        
        if (ideDoc != null) {
            int ideElements = XMLValidator.getLineCount(ideDoc);
            analysis.append(String.format("IDE Tracking Elements: %d\n", ideElements));
            analysis.append(String.format("Data Size: %.2f KB\n", 
                XMLValidator.documentToFormattedString(ideDoc).length() / 1024.0));
        }
        
        if (eyeDoc != null) {
            int eyeElements = XMLValidator.getLineCount(eyeDoc);
            analysis.append(String.format("Eye Tracking Elements: %d\n", eyeElements));
            analysis.append(String.format("Data Size: %.2f KB\n", 
                XMLValidator.documentToFormattedString(eyeDoc).length() / 1024.0));
        }
        
        return analysis.toString();
    }
*/

// ============================================================================
// EXAMPLE 8: Error Handling and Validation
// ============================================================================

/**
 * Comprehensive error handling for XML operations:
 */
/*
    private boolean loadAndValidateXML(String filePath) {
        // Step 1: Check file exists
        File file = new File(filePath);
        if (!file.exists()) {
            logError("File not found: " + filePath);
            return false;
        }
        
        // Step 2: Validate XML structure
        if (!XMLValidator.validateXMLFile(filePath)) {
            String error = XMLValidator.getValidationErrorMessage(filePath);
            logError("XML Validation failed: " + error);
            return false;
        }
        
        // Step 3: Parse document
        Document doc = XMLValidator.parseXMLFile(filePath);
        if (doc == null) {
            logError("Failed to parse document");
            return false;
        }
        
        // Step 4: Display with error handling
        try {
            XMLPreviewPanel panel = new XMLPreviewPanel();
            panel.displayDocument(doc);
            return true;
        } catch (Exception e) {
            logError("Display error: " + e.getMessage());
            return false;
        }
    }
*/

// ============================================================================
// EXAMPLE 9: Integration with RealtimeDataImpl
// ============================================================================

/**
 * In RealtimeDataImpl.java, add XML preview support:
 */
/*
    import api.XMLPreviewDataHandler;
    
    public class RealtimeDataImpl implements RealtimeData {
        // ... existing code ...
        
        private final XMLPreviewDataHandler previewHandler = 
            XMLPreviewDataHandler.getInstance();
        
        @Override
        public void onIDETrackerUpdate(Element element) {
            // Existing real-time data transmission
            // ... code ...
            
            // Also update preview if in tracking state
            if (isInTrackingState()) {
                previewHandler.onIDEElementAdded(element);
            }
        }
    }
*/

// ============================================================================
// EXAMPLE 10: Creating a Menu Action for XML Analysis
// ============================================================================

/**
 * New action for analyzing XML output (XMLAnalysisAction.java):
 */
/*
    package actions;
    
    import com.intellij.openapi.actionSystem.AnAction;
    import com.intellij.openapi.actionSystem.AnActionEvent;
    import components.XMLPreviewWindow;
    import utils.XMLValidator;
    import org.jetbrains.annotations.NotNull;
    import org.w3c.dom.Document;
    
    public class XMLAnalysisAction extends AnAction {
        
        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            // Get last tracking session data
            File sessionDir = getLastSessionDirectory();
            
            if (sessionDir == null) {
                showNotification(e, "No session data found");
                return;
            }
            
            // Load all XML files
            XMLPreviewWindow window = new XMLPreviewWindow(e.getProject());
            
            File ideXML = new File(sessionDir, "ide_tracking.xml");
            File eyeXML = new File(sessionDir, "eye_tracking.xml");
            
            if (ideXML.exists()) {
                Document doc = XMLValidator.parseXMLFile(ideXML.getPath());
                if (doc != null) window.displayIDETrackingXML(doc);
            }
            
            if (eyeXML.exists()) {
                Document doc = XMLValidator.parseXMLFile(eyeXML.getPath());
                if (doc != null) window.displayEyeTrackingXML(doc);
            }
            
            window.show();
        }
        
        private File getLastSessionDirectory() {
            // Implementation to get last session directory
            return null;
        }
        
        private void showNotification(AnActionEvent e, String message) {
            // Show notification to user
        }
    }
*/


