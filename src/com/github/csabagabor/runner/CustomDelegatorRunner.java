package com.github.csabagabor.runner;

import com.github.csabagabor.helper.UIHelper;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import org.jetbrains.annotations.NotNull;

public interface CustomDelegatorRunner {

    default void doPreExecute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment env) throws ExecutionException {
    }

    default void doPostExecute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment env,
                               RunContentDescriptor runContentDescriptor, Runnable runInCaseOfError) {
        final ProcessHandler processHandler = runContentDescriptor.getProcessHandler();

        if (processHandler != null) {
            processHandler.addProcessListener(new ProcessAdapter() {
                public void processTerminated(@NotNull ProcessEvent event) {
                    if (event == null) {
                        return;
                    }

                    if (event.getExitCode() == -8) {
                        UIHelper.showErrorMessage("Plugin only works with JUnit4", env.getProject());
                        return;
                    }

                    //error code is -1 if a test fails
                    if (event.getExitCode() < -2) {
                        runInCaseOfError.run();
                    }
                }
            });
        }
    }
}
