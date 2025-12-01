package components.statusbar;

import api.TrackingStatusNotifier;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.MouseEvent;

/**
 * CodeGRITS Status Bar Widget that displays text + icons
 * for Active, Paused, and Stopped states.
 */
public class CodeGRITSStatusWidget implements StatusBarWidget, StatusBarWidget.MultipleTextValuesPresentation {

    private static final String WIDGET_ID = "CodeGRITSStatusWidget";

    private TrackingStatusNotifier.Status state = TrackingStatusNotifier.Status.STOPPED;
    private StatusBar statusBar;

    @Override
    public @NotNull String ID() {
        return WIDGET_ID;
    }

    @Override
    public void install(@NotNull StatusBar statusBar) {
        this.statusBar = statusBar;

        Project project = statusBar.getProject();
        if (project == null) return;

        project.getMessageBus().connect().subscribe(
                TrackingStatusNotifier.TOPIC,
                status -> {
                    this.state = status;
                    statusBar.updateWidget(WIDGET_ID);
                });
    }

    @Override
    public void dispose() {}

    // --- Presentation ---

    @Override
    public @NotNull String getSelectedValue() {
        return switch (state) {
            case STARTED, RESUMED -> "CodeGRITS: Active";
            case PAUSED          -> "CodeGRITS: Paused";
            case STOPPED         -> "CodeGRITS: Stopped";
            default              -> "CodeGRITS";       // fallback, never null
        };
    }



    @Override
    public @NotNull Icon getIcon() {
        return switch (state) {
            case STARTED, RESUMED -> AllIcons.General.InspectionsOK;
            case PAUSED           -> AllIcons.Actions.Pause;
            case STOPPED          -> AllIcons.Actions.Suspend;
            default               -> AllIcons.General.InspectionsOK; // fallback, never null
        };
    }



    @Override
    public @Nullable String getTooltipText() {
        return getSelectedValue();
    }

    @Override
    public @Nullable Consumer<MouseEvent> getClickConsumer() {
        return null;
    }

    // REQUIRED BY MultipleTextValuesPresentation
    @Override
    public @Nullable ListPopup getPopupStep() {
        return null; // we don't show a popup
    }
    
    @Override
    public @Nullable WidgetPresentation getPresentation() {
        return this; // NEVER return null
    }

}
