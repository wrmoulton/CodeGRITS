package components;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;

import javax.swing.*;
import java.awt.*;

/**
 * This dialog window provides a dedicated preview interface for XML output.
 * It displays IDE tracking and eye tracking XML data with syntax highlighting.
 */
public class XMLPreviewWindow extends DialogWrapper {

    private final XMLPreviewPanel previewPanel;
    private final JTabbedPane tabbedPane;
    private final XMLPreviewPanel ideTrackingPanel;
    private final XMLPreviewPanel eyeTrackingPanel;

    public XMLPreviewWindow(Project project) {
        super(project, true, IdeModalityType.MODELESS);
        setTitle("CodeGRITS XML Preview");
        setSize(900, 600);
        setResizable(true);

        // Create tabbed interface for different XML types
        tabbedPane = new JTabbedPane();

        ideTrackingPanel = new XMLPreviewPanel();
        eyeTrackingPanel = new XMLPreviewPanel();
        previewPanel = new XMLPreviewPanel();

        tabbedPane.addTab("IDE Tracking", ideTrackingPanel);
        tabbedPane.addTab("Eye Tracking", eyeTrackingPanel);
        tabbedPane.addTab("Combined View", previewPanel);

        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return tabbedPane;
    }

    /**
     * Display IDE tracking XML in the preview.
     *
     * @param document The IDE tracking XML document.
     */
    public void displayIDETrackingXML(Document document) {
        ideTrackingPanel.displayDocument(document);
        tabbedPane.setSelectedIndex(0);
    }

    /**
     * Display eye tracking XML in the preview.
     *
     * @param document The eye tracking XML document.
     */
    public void displayEyeTrackingXML(Document document) {
        eyeTrackingPanel.displayDocument(document);
        tabbedPane.setSelectedIndex(1);
    }

    /**
     * Display XML string in the combined view.
     *
     * @param xmlString The XML string to display.
     */
    public void displayCombinedXML(String xmlString) {
        previewPanel.displayXMLString(xmlString);
        tabbedPane.setSelectedIndex(2);
    }

    /**
     * Display IDE tracking XML string.
     *
     * @param xmlString The XML string.
     */
    public void displayIDETrackingXMLString(String xmlString) {
        ideTrackingPanel.displayXMLString(xmlString);
        tabbedPane.setSelectedIndex(0);
    }

    /**
     * Display eye tracking XML string.
     *
     * @param xmlString The XML string.
     */
    public void displayEyeTrackingXMLString(String xmlString) {
        eyeTrackingPanel.displayXMLString(xmlString);
        tabbedPane.setSelectedIndex(1);
    }

    /**
     * Get the IDE tracking preview panel.
     *
     * @return The panel.
     */
    public XMLPreviewPanel getIDETrackingPanel() {
        return ideTrackingPanel;
    }

    /**
     * Get the eye tracking preview panel.
     *
     * @return The panel.
     */
    public XMLPreviewPanel getEyeTrackingPanel() {
        return eyeTrackingPanel;
    }

    /**
     * Get the combined preview panel.
     *
     * @return The panel.
     */
    public XMLPreviewPanel getCombinedPanel() {
        return previewPanel;
    }

    /**
     * Clear all preview content.
     */
    public void clearAll() {
        ideTrackingPanel.clear();
        eyeTrackingPanel.clear();
        previewPanel.clear();
    }
}
