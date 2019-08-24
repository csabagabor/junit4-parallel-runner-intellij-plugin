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

package gabor.paralleltester.runner;

import com.googlecode.junittoolbox.ParallelSuite;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.ModuleRunProfile;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.impl.DefaultJavaProgramRunner;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.RunConfigurationWithSuppressedDefaultRunAction;
import com.intellij.openapi.diagnostic.Logger;
import gabor.paralleltester.executor.RunVisualVMExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.internal.builders.AllDefaultPossibilitiesBuilder;
import org.junit.runners.model.InitializationError;


public class RunVisualVMRunner extends DefaultJavaProgramRunner {
    private static final Logger log = Logger.getInstance(RunVisualVMRunner.class.getName());

    @NotNull
    public String getRunnerId() {
        return RunVisualVMExecutor.RUN_WITH_VISUAL_VM;
    }

    @Override
    public void execute(@NotNull final ExecutionEnvironment env, @Nullable final Callback callback)

            throws ExecutionException {
        log.info("execute");


        try {
            System.out.println("entered");
            Class<? extends RunVisualVMRunner> aClass = this.getClass();
            AllDefaultPossibilitiesBuilder allDefaultPossibilitiesBuilder =
                    new AllDefaultPossibilitiesBuilder(true);
            System.out.println("entered2");
            ParallelSuite parallelSuite = new ParallelSuite(aClass, allDefaultPossibilitiesBuilder);
            System.out.println(parallelSuite);
                    
        } catch (InitializationError initializationError) {
            initializationError.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        super.execute(env, callback);
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return executorId.equals(RunVisualVMExecutor.RUN_WITH_VISUAL_VM) &&
                profile instanceof ModuleRunProfile &&
                !(profile instanceof RunConfigurationWithSuppressedDefaultRunAction);
    }
}
