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
import javax.swing.Timer;
import java.awt.event.ActionEvent; 

/**
 * CodeGRITS Status Bar Widget that displays text + icons
 * for Active, Paused, and Stopped states.
 */
public class CodeGRITSStatusWidget implements StatusBarWidget, StatusBarWidget.MultipleTextValuesPresentation {

    private static final String WIDGET_ID = "CodeGRITSStatusWidget";

    private TrackingStatusNotifier.Status state = TrackingStatusNotifier.Status.STOPPED;
    private StatusBar statusBar;

     // --- Timer state ---
    private Timer timer;                
    private long elapsedSeconds = 0; 

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
                    switch (status) {
                        case STARTED -> startTimer();
                        case RESUMED -> resumeTimer();
                        case PAUSED  -> pauseTimer();
                        case STOPPED -> stopAndResetTimer();
                    }
                    statusBar.updateWidget(WIDGET_ID);
                });
    }

    @Override
    public @NotNull String getSelectedValue() {
        return switch (state) {
            case STARTED, RESUMED -> "CodeGRITS: Active (" + formatTime(elapsedSeconds) + ")";
            case PAUSED           -> "CodeGRITS: Paused (" + formatTime(elapsedSeconds) + ")";
            case STOPPED          -> "CodeGRITS: Stopped";
            default               -> "CodeGRITS";
        };
    }



    @Override
    public @NotNull Icon getIcon() {
        return switch (state) {
            case STARTED, RESUMED -> AllIcons.General.InspectionsOK;
            case PAUSED           -> AllIcons.Actions.Pause;
            case STOPPED          -> AllIcons.Actions.Suspend;
            default               -> AllIcons.General.InspectionsOK;
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
    // --- Timer helpers ---

    private void startTimer() {
        elapsedSeconds = 0;
        createTimer();
        timer.start();
    }

    private void resumeTimer() {
        if (timer == null) {
            createTimer();
        }
        timer.start();
    }

    private void pauseTimer() {
        if (timer != null) {
            timer.stop();
        }
    }

    private void stopAndResetTimer() {
        if (timer != null) {
            timer.stop();
        }
        elapsedSeconds = 0;
    }

    private void createTimer() {
        timer = new Timer(1000, (ActionEvent e) -> {
            elapsedSeconds++;
            if (statusBar != null) {
                statusBar.updateWidget(WIDGET_ID);
            }
        });
    }

    private String formatTime(long sec) {
        long m = sec / 60;
        long s = sec % 60;
        return String.format("%02d:%02d", m, s);
    }

    @Override
    public void dispose() {
        if (timer != null) {
            timer.stop();
        }
    }

}
