package components.statusbar;

import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Status Bar Widget for CodeGRITS.
 * Displays the current tracking state (default = "Stopped").
 */
public class CodeGRITSStatusWidget implements StatusBarWidget, StatusBarWidget.TextPresentation {

    private static final String WIDGET_ID = "CodeGRITSStatusWidget";

    // Default state shown on startup
    private String text = "CodeGRITS: Stopped";

    @Override
    public @NotNull String ID() {
        return WIDGET_ID;
    }

    @Override
    public void install(@NotNull StatusBar statusBar) {
        // No special initialization yet
    }

    @Override
    public void dispose() {
        // Nothing to dispose yet
    }

    @Override
    public @Nullable WidgetPresentation getPresentation() {
        return this; // We implement TextPresentation ourselves
    }

    // ===== WidgetPresentation / TextPresentation =====

    @Override
    public @Nullable String getTooltipText() {
        return "CodeGRITS Tracking Status";
    }

    @Override
    public @Nullable Consumer<MouseEvent> getClickConsumer() {
        // No click action yet – can add later
        return null;
    }

    @Override
    public @NotNull String getText() {
        return text;
    }

    @Override
    public float getAlignment() {
        return Component.CENTER_ALIGNMENT;
    }

    /**
     * Allows other parts of the plugin (e.g., Start/Stop tracking)
     * to update the status text.
     */
    public void setState(@NotNull String newState) {
        this.text = "CodeGRITS: " + newState;
    }
}
