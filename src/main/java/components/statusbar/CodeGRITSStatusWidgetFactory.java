package components.statusbar;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.StatusBarWidgetFactory;
import org.jetbrains.annotations.NotNull;

/**
 * Factory that registers and creates the CodeGRITS Status Bar Widget.
 */
public class CodeGRITSStatusWidgetFactory implements StatusBarWidgetFactory {

    @Override
    public @NotNull String getId() {
        return "CodeGRITSStatusWidget"; // MUST remain stable forever
    }

    @Override
    public @NotNull String getDisplayName() {
        return "CodeGRITS Tracking Status";
    }

    @Override
    public boolean isAvailable(@NotNull Project project) {
        return true; // Always available
    }

    @Override
    public boolean isEnabledByDefault() {
        return true; // <-- THIS FIXES YOUR BUG
    }

    @Override
    public @NotNull StatusBarWidget createWidget(@NotNull Project project) {
        return new CodeGRITSStatusWidget();
    }

    @Override
    public void disposeWidget(@NotNull StatusBarWidget widget) {
        widget.dispose();
    }

    @Override
    public boolean canBeEnabledOn(@NotNull StatusBar statusBar) {
        return true;
    }
}
