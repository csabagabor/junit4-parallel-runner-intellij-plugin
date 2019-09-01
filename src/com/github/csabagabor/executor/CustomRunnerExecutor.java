package com.github.csabagabor.executor;

import com.github.csabagabor.ResourcesPlugin;
import com.intellij.execution.Executor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class CustomRunnerExecutor extends Executor {

    public static final String WITH_PARALLEL_RUNNER = "Run with Parallel Runner";

    public static final String RUN_WITH_PARALLEL_RUNNER = "RunwithParallelRunner";

    @NotNull
    public String getToolWindowId() {
        return getId();
    }

    public Icon getToolWindowIcon() {
        return ResourcesPlugin.RUN_13;
    }

    @NotNull
    public Icon getIcon() {
        return ResourcesPlugin.RUN;
    }

    public Icon getDisabledIcon() {
        return null;
    }

    public String getDescription() {
        return WITH_PARALLEL_RUNNER;
    }

    @NotNull
    public String getActionName() {
        return RUN_WITH_PARALLEL_RUNNER;
    }

    @NotNull
    public String getId() {
        return WITH_PARALLEL_RUNNER;
    }

    @NotNull
    public String getStartActionText() {
        return WITH_PARALLEL_RUNNER;
    }

    public String getContextActionId() {
        return getId() + " context-action-does-not-exist";
    }

    public String getHelpId() {
        return null;
    }
}
