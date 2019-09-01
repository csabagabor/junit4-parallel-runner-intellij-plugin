/*
 * This file is part of VisualVM for IDEA
 *
 * Copyright (c) 2008, Esko Luontola. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above copyright notice,
 *       this list of conditions and the following disclaimer in the documentation
 *       and/or other materials provided with the distribution.
 *
 *     * Neither the name of the copyright holder nor the names of its contributors
 *       may be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.github.csabagabor.runner;

import com.github.csabagabor.executor.CustomRunnerExecutor;
import com.github.csabagabor.helper.UIHelper;
import com.github.csabagabor.runner.factory.CustomDelegatorFactory;
import com.googlecode.junittoolbox.ParallelSuite;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.JavaCommandLine;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.impl.DefaultJavaProgramRunner;
import com.intellij.execution.junit.JUnitConfiguration;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.RunConfigurationWithSuppressedDefaultRunAction;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;


public class CustomRunner extends DefaultJavaProgramRunner {
    private static final Logger log = Logger.getInstance(CustomRunner.class.getName());
    private CustomDelegatorRunner delegatorRunner;

    @NotNull
    public String getRunnerId() {
        return CustomRunnerExecutor.WITH_PARALLEL_RUNNER;
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

        RunContentDescriptor runContentDescriptor = null;

        try {
            delegatorRunner = CustomDelegatorFactory.getRunner();
            delegatorRunner.doPreExecute(state, env);
            runContentDescriptor = super.doExecute(state, env);
            delegatorRunner.doPostExecute(state, env, runContentDescriptor, new Runnable() {
                @Override
                public void run() {
                    CustomDelegatorFactory.runNextRunner(state, env, true);
                }
            });

        } catch (Exception other) {
            CustomDelegatorFactory.runNextRunner(state, env, true);
        }

        return runContentDescriptor;
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return executorId.equals(CustomRunnerExecutor.WITH_PARALLEL_RUNNER) &&
                profile instanceof JUnitConfiguration &&
                !(profile instanceof RunConfigurationWithSuppressedDefaultRunAction);
    }
}
