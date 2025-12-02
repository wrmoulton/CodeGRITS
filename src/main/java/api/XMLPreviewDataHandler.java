package api;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.WindowManager;
import components.XMLStatusWidget;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Handler for XML preview updates during tracking.
 * Manages real-time display of XML data in the IDE.
 */
public class XMLPreviewDataHandler {

    private static XMLPreviewDataHandler instance;
    private XMLStatusWidget statusWidget;
    private Document currentIDEDocument;
    private Document currentEyeDocument;

    private XMLPreviewDataHandler() {
    }

    /**
     * Get the singleton instance.
     *
     * @return The instance.
     */
    public static synchronized XMLPreviewDataHandler getInstance() {
        if (instance == null) {
            instance = new XMLPreviewDataHandler();
        }
        return instance;
    }

    /**
     * Initialize the handler with a project.
     *
     * @param project The current project.
     */
    public void initialize(Project project) {
        // Try to get the existing status widget, or create a new one if needed
        try {
            statusWidget = new XMLStatusWidget(project);
        } catch (Exception e) {
            // Widget may not be available in all contexts
        }
    }

    /**
     * Handle IDE tracking document updates.
     *
     * @param document The updated IDE tracking document.
     */
    public void onIDETrackingUpdate(Document document) {
        this.currentIDEDocument = document;
        if (statusWidget != null) {
            statusWidget.updateIDEDocument(document);
        }
    }

    /**
     * Handle eye tracking document updates.
     *
     * @param document The updated eye tracking document.
     */
    public void onEyeTrackingUpdate(Document document) {
        this.currentEyeDocument = document;
        if (statusWidget != null) {
            statusWidget.updateEyeDocument(document);
        }
    }

    /**
     * Handle IDE tracking element updates.
     *
     * @param element The element that was added to tracking data.
     */
    public void onIDEElementAdded(Element element) {
        if (statusWidget != null) {
            String elementName = element.getTagName();
            statusWidget.updateStatus("Recording " + elementName);
        }
    }

    /**
     * Get the current IDE tracking document.
     *
     * @return The document, or null if not available.
     */
    public Document getCurrentIDEDocument() {
        return currentIDEDocument;
    }

    /**
     * Get the current eye tracking document.
     *
     * @return The document, or null if not available.
     */
    public Document getCurrentEyeDocument() {
        return currentEyeDocument;
    }

    /**
     * Reset the handler state.
     */
    public void reset() {
        currentIDEDocument = null;
        currentEyeDocument = null;
        if (statusWidget != null) {
            statusWidget.clear();
        }
    }

    /**
     * Set the status widget.
     *
     * @param widget The widget to set.
     */
    public void setStatusWidget(XMLStatusWidget widget) {
        this.statusWidget = widget;
    }
}
