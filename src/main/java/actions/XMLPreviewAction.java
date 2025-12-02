package actions;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import components.XMLPreviewWindow;
import org.jetbrains.annotations.NotNull;
import utils.XMLValidator;

import java.io.File;

/**
 * Action to open the XML Preview window for viewing CodeGRITS output data.
 */
public class XMLPreviewAction extends AnAction {

    private XMLPreviewWindow previewWindow;

    /**
     * Perform the XML preview action.
     * Shows a dialog allowing users to preview XML output files.
     *
     * @param e The action event.
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }

        // Create or show existing preview window
        if (previewWindow == null) {
            previewWindow = new XMLPreviewWindow(project);
        }

        previewWindow.show();

        // For now, show a placeholder message
        Notification notification = new Notification("CodeGRITS Notification Group", "XML Preview",
                "XML Preview window opened. Navigate to your data output folder to view XML files.",
                NotificationType.INFORMATION);
        notification.notify(project);
    }

    /**
     * Get the preview window instance.
     *
     * @return The preview window, or null if not created yet.
     */
    public XMLPreviewWindow getPreviewWindow() {
        return previewWindow;
    }

    /**
     * Close the preview window.
     */
    public void closePreviewWindow() {
        if (previewWindow != null) {
            previewWindow.close(CLOSE_OK);
            previewWindow = null;
        }
    }
}
