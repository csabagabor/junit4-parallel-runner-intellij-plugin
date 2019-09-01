package com.github.csabagabor.runner;

import com.github.csabagabor.exceptions.BadJUnitVersionException;
import com.github.csabagabor.executor.CustomDebuggerExecutor;
import com.github.csabagabor.runner.factory.CustomDelegatorFactory;
import com.intellij.debugger.impl.GenericDebuggerRunner;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.RunConfigurationWithSuppressedDefaultDebugAction;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.junit.JUnitConfiguration;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import org.jetbrains.annotations.NotNull;

public class CustomDebuggerRunner extends GenericDebuggerRunner {
    private CustomDelegatorRunner delegatorRunner;

    @NotNull
    public String getRunnerId() {
        return CustomDebuggerExecutor.WITH_PARALLEL_RUNNER;
    }

    @Override
    protected RunContentDescriptor doExecute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment env) throws ExecutionException {

        RunContentDescriptor runContentDescriptor = null;
        try {
            delegatorRunner = CustomDelegatorFactory.getRunner();
            delegatorRunner.doPreExecute(state, env);
            runContentDescriptor = super.doExecute(state, env);
            delegatorRunner.doPostExecute(state, env, runContentDescriptor, new Runnable() {
                @Override
                public void run() {
                    CustomDelegatorFactory.runNextRunner(state, env, false);
                }
            });
        } catch (BadJUnitVersionException e) {
            return null;
        } catch (Exception other) {
            CustomDelegatorFactory.runNextRunner(state, env, true);
        }

        return runContentDescriptor;
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return executorId.equals(CustomDebuggerExecutor.WITH_PARALLEL_RUNNER) &&
                profile instanceof JUnitConfiguration &&
                !(profile instanceof RunConfigurationWithSuppressedDefaultDebugAction);
    }
}
