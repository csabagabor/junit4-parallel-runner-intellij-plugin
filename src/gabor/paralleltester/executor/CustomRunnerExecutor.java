package gabor.paralleltester.executor;

import com.intellij.execution.Executor;
import gabor.paralleltester.Resources;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class CustomRunnerExecutor extends Executor {

    public static final String RUN_WITH_VISUAL_VM = "Run With Parallel Runner";
    public static final String RUN_WITH_VISUAL_VM1 = "RunWithParallelRunner";

    @NotNull
    public String getToolWindowId() {
        return getId();
    }

    public Icon getToolWindowIcon() {
        return Resources.RUN_13;
    }

    @NotNull
    public Icon getIcon() {
        return Resources.RUN;
    }

    public Icon getDisabledIcon() {
        return null;
    }

    public String getDescription() {
        return RUN_WITH_VISUAL_VM;
    }

    @NotNull
    public String getActionName() {
        return RUN_WITH_VISUAL_VM1;
    }

    @NotNull
    public String getId() {
        return RUN_WITH_VISUAL_VM;
    }

    @NotNull
    public String getStartActionText() {
        return RUN_WITH_VISUAL_VM;
    }

    public String getContextActionId() {
        // HACK: ExecutorRegistryImpl expects this to be non-null, but we don't want any context actions for every file
        return getId() + " context-action-does-not-exist";
    }

    public String getHelpId() {
        return null;
    }
}
