package com.github.csabagabor.runner;

import com.github.csabagabor.executor.CustomDebuggerExecutor;
import com.github.csabagabor.helper.UIHelper;
import com.github.csabagabor.runner.factory.CustomDelegatorFactory;
import com.googlecode.junittoolbox.ParallelSuite;
import com.intellij.debugger.impl.GenericDebuggerRunner;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.JavaCommandLine;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.RunConfigurationWithSuppressedDefaultDebugAction;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.junit.JUnitConfiguration;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;

public class CustomDebuggerRunner extends GenericDebuggerRunner {
    private static final Logger log = Logger.getInstance(CustomDebuggerRunner.class.getName());
    private CustomDelegatorRunner delegatorRunner;

    @NotNull
    public String getRunnerId() {
        return CustomDebuggerExecutor.WITH_PARALLEL_RUNNER;
    }

    @Override
    protected RunContentDescriptor doExecute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment env) throws ExecutionException {
        JavaParameters javaParameters = ((JavaCommandLine) state).getJavaParameters();

        if (javaParameters.getProgramParametersList().hasParameter("-junit5") ||
                javaParameters.getProgramParametersList().hasParameter("-junit3")) {
            UIHelper.showErrorMessage("Plugin only works with JUnit4", env.getProject());
            return null;
        }

        javaParameters.getClassPath().addFirst(PathManager.getPluginsPath());
        javaParameters.getClassPath().addFirst(PathManager.getJarPathForClass(ParallelSuite.class));

        delegatorRunner = CustomDelegatorFactory.getRunner();
        delegatorRunner.doPreExecute(state, env);
        RunContentDescriptor runContentDescriptor = super.doExecute(state, env);
        delegatorRunner.doPostExecute(state, env, runContentDescriptor, new Runnable() {
            @Override
            public void run() {
                CustomDelegatorFactory.runNextRunner(state, env, false);
            }
        });

        return runContentDescriptor;
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return executorId.equals(CustomDebuggerExecutor.WITH_PARALLEL_RUNNER) &&
                profile instanceof JUnitConfiguration &&
                !(profile instanceof RunConfigurationWithSuppressedDefaultDebugAction);
    }
}
