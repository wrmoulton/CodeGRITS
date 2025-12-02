package components;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.util.ui.JBUI;
import org.w3c.dom.Document;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Status bar widget component that displays real-time XML tracking information.
 * Provides quick access to XML preview and tracking statistics.
 */
public class XMLStatusWidget extends JPanel {

    private final JLabel statusLabel;
    private final Project project;
    private XMLPreviewPanel previewPanel;
    private Document currentIDEDocument;
    private Document currentEyeDocument;
    private int ideElementCount = 0;
    private int eyeElementCount = 0;

    public XMLStatusWidget(Project project) {
        this.project = project;
        setLayout(new FlowLayout(FlowLayout.CENTER, JBUI.scale(5), 0));
        setBorder(JBUI.Borders.empty(0, 5));

        statusLabel = new JLabel("XML: Idle");
        statusLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        statusLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                showPreviewPopup(e);
            }
        });

        add(statusLabel);
    }

    /**
     * Update the status with current tracking information.
     *
     * @param message The status message.
     */
    public void updateStatus(String message) {
        statusLabel.setText("XML: " + message);
    }

    /**
     * Update with IDE document information.
     *
     * @param document The IDE tracking document.
     */
    public void updateIDEDocument(Document document) {
        this.currentIDEDocument = document;
        if (document != null) {
            this.ideElementCount = document.getDocumentElement().getChildNodes().getLength();
            updateStatus(String.format("IDE: %d elements", ideElementCount));
        }
    }

    /**
     * Update with eye tracking document information.
     *
     * @param document The eye tracking document.
     */
    public void updateEyeDocument(Document document) {
        this.currentEyeDocument = document;
        if (document != null) {
            this.eyeElementCount = document.getDocumentElement().getChildNodes().getLength();
            updateStatus(String.format("Eye: %d elements", eyeElementCount));
        }
    }

    /**
     * Show a popup preview when the status label is clicked.
     *
     * @param e The mouse event.
     */
    private void showPreviewPopup(MouseEvent e) {
        if (previewPanel == null) {
            previewPanel = new XMLPreviewPanel();
        }

        if (currentIDEDocument != null) {
            previewPanel.displayDocument(currentIDEDocument);
        } else {
            previewPanel.displayXMLString("No IDE tracking data yet...");
        }

        JBPopupFactory.getInstance()
                .createComponentPopupBuilder(previewPanel, null)
                .setMovable(true)
                .setResizable(true)
                .setTitle("XML Preview")
                .show(RelativePoint.fromScreen(e.getLocationOnScreen()));
    }

    /**
     * Clear all tracked data.
     */
    public void clear() {
        currentIDEDocument = null;
        currentEyeDocument = null;
        ideElementCount = 0;
        eyeElementCount = 0;
        updateStatus("Idle");
    }
}
