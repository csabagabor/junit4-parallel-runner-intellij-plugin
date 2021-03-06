package com.github.csabagabor.runner.runners;

import com.github.csabagabor.Resources;
import com.github.csabagabor.runner.CustomDelegatorRunner;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.JavaCommandLine;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import org.jetbrains.annotations.NotNull;

public class CustomRunner1 extends CustomDelegatorRunner {

    @Override
    public void doPreExecute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment env) throws ExecutionException {
        super.doPreExecute(state, env);
        JavaParameters javaParameters = ((JavaCommandLine) state).getJavaParameters();
        javaParameters.setMainClass(Resources.PARALLEL_STARTER);
    }
}
