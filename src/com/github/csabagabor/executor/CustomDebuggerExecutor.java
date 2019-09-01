package com.github.csabagabor.executor;

import com.intellij.execution.executors.DefaultDebugExecutor;
import com.github.csabagabor.ResourcesPlugin;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class CustomDebuggerExecutor extends DefaultDebugExecutor {
    @NonNls
    public static final String WITH_PARALLEL_RUNNER = "Debug With Parallel Runner";

    public static final String DEBUG_WITH_PARALLEL_RUNNER = "DebugWithParallelRunner";

    @NotNull
    public String getToolWindowId() {
        return getId();
    }

    public Icon getToolWindowIcon() {
        return ResourcesPlugin.DEBUG_13;
    }

    @NotNull
    public Icon getIcon() {
        return ResourcesPlugin.DEBUG;
    }

    public Icon getDisabledIcon() {
        return null;
    }

    public String getDescription() {
        return WITH_PARALLEL_RUNNER;
    }

    @NotNull
    public String getActionName() {
        return DEBUG_WITH_PARALLEL_RUNNER;
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
