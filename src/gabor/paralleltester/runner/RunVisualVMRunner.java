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

import com.googlecode.junittoolbox.C;
import com.googlecode.junittoolbox.ParallelScheduler;
import com.googlecode.junittoolbox.ParallelSuite;
import com.googlecode.junittoolbox.util.MultiException;
import com.googlecode.junittoolbox.util.TigerThrower;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.*;
import com.intellij.execution.impl.DefaultJavaProgramRunner;
import com.intellij.execution.junit.TestObject;
import com.intellij.execution.junit.TestsPattern;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.RunConfigurationWithSuppressedDefaultRunAction;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.compiler.ex.CompilerPathsEx;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.ModuleManager;
import gabor.paralleltester.executor.RunVisualVMExecutor;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


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
        super.execute(env, callback);
    }

    @Override
    protected RunContentDescriptor doExecute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment env) throws ExecutionException {
        Class<?> testClass = null;
        try {
            testClass = Class.forName("com.intellij.execution.junit.TestClass");

            if (state instanceof TestsPattern) {
                Set<String> patterns = ((TestsPattern) state).getConfiguration().getPersistentData().getPatterns();

                patterns.clear();
                patterns.add("gabor.paralleltester.runner.A");
                ((TestsPattern) state).getConfiguration().getPersistentData().setPatterns((LinkedHashSet<String>)
                        patterns);

            } else if (testClass.isInstance(state)) {
                Object obj = testClass.cast(state);
                String mainClassName = ((TestObject) (testClass.cast(state))).
                        getConfiguration().getPersistentData()
                        .MAIN_CLASS_NAME = "com.googlecode.junittoolbox.C";

                // setMainClass("gabor.paralleltester.runner.A");
            }


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        //copy files

        //String jarPathForClass = PathManager.getJarPathForClass(ParallelScheduler.class) + "/com/googlecode/junittoolbox/ParallelScheduler.class";
        String jarPathForClass = PathManager.getJarPathForClass(ParallelScheduler.class);
        //String jarPathForClass2 = PathManager.getJarPathForClass(ParallelSuite.class) + "/com/googlecode/junittoolbox/ParallelSuite.class";
        //String jarPathForClass3 = PathManager.getJarPathForClass(MultiException.class) + "/com/googlecode/junittoolbox/util/MultiException.class";
        //String jarPathForClass4 = PathManager.getJarPathForClass(TigerThrower.class) + "/com/googlecode/junittoolbox/util/TigerThrower.class";
        //String jarPathForClass5 = PathManager.getJarPathForClass(C.class) + "/com/googlecode/junittoolbox/C.class";


        String[] outputPaths = CompilerPathsEx.getOutputPaths(ModuleManager.getInstance(env.getProject()).getModules());

        try {
            File file = new File(jarPathForClass + "/com");
            //File file1 = new File(outputPaths[0] + "/com/googlecode/junittoolbox/ParallelScheduler.class");
            File file1 = new File(outputPaths[0] + "/com");
            //File file1 = new File("C:\\Users\\csaba.gabor\\Documents\\GitHub\\ParallelJUnitTester-intellij-plugin\\out\\artifacts\\intellij_parallel_test_plugin_jar\\t2.txt");
            FileUtils.copyDirectory(file, file1);

            // Files.copy(file.toPath(), file1.toPath());
            //Files.copy(new File(jarPathForClass2).toPath(), new File(outputPaths[0] + "/com/googlecode/junittoolbox/ParallelSuite.class").toPath());
            //Files.copy(new File(jarPathForClass3).toPath(), new File(outputPaths[0] + "/com/googlecode/junittoolbox/util/MultiException.class").toPath());
            // Files.copy(new File(jarPathForClass4).toPath(), new File(outputPaths[0] + "/com/googlecode/junittoolbox/util/TigerThrower.class").toPath());
            //Files.copy(new File(jarPathForClass5).toPath(), new File(outputPaths[0] + "/com/googlecode/junittoolbox/C.class").toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        RunContentDescriptor runContentDescriptor = super.doExecute(state, env);

        final ProcessHandler processHandler = runContentDescriptor.getProcessHandler();

        if (processHandler != null) {

            processHandler.addProcessListener(new ProcessAdapter() {
                public void processTerminated(@NotNull ProcessEvent event) {
                    if (event == null) {
                        return;
                    }

                    System.out.println("Exit code" + event.getExitCode());
                }
            });
        }

        return runContentDescriptor;
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return executorId.equals(RunVisualVMExecutor.RUN_WITH_VISUAL_VM) &&
                profile instanceof ModuleRunProfile &&
                !(profile instanceof RunConfigurationWithSuppressedDefaultRunAction);
    }

    @Override
    public void patch(JavaParameters javaParameters, RunnerSettings settings, RunProfile runProfile, boolean beforeExecution) throws ExecutionException {
        String user = "csaba.gabor";
        ParametersList programParametersList = javaParameters.getProgramParametersList();

        List<String> list = new ArrayList<>(javaParameters.getProgramParametersList().getList());

        programParametersList.clearAll();

        for (String s : list) {
            if (!s.contains("@")) {
                programParametersList.add(s);
            }
        }
//        String user = "admin";

        //javaParameters.getClassPath().addFirst("C:\\Users\\admin\\Documents\\GitHub\\ParallelJunitTester-intellij-plugin\\out\\artifacts\\intellij_parallel_test_plugin_jar\\intellij-parallel-test-plugin.jar");
        //javaParameters.getClassPath().addFirst("C:\\Users\\" + user + "\\Documents\\GitHub\\ParallelJUnitTester-intellij-plugin\\out\\artifacts\\intellij_parallel_test_plugin_jar\\intellij-parallel-test-plugin.jar");

//        javaParameters.getClassPath().addFirst(PathManager.getPluginsPath());
//        javaParameters.getClassPath().addFirst(PathManager.getJarPathForClass(A.class));
//        javaParameters.setMainClass("gabor.paralleltester.runner.MyStarter2");


        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter("C:\\Users\\" + user + "\\Documents\\GitHub\\ParallelJunitTester-intellij-plugin\\out\\artifacts\\intellij_parallel_test_plugin_jar\\t.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter printWriter = new PrintWriter(fileWriter);

        printWriter.println("patch");

        for (String s : javaParameters.getClassPath().getPathList()) {
            printWriter.println("path:" + s);
        }
        printWriter.close();

        super.patch(javaParameters, settings, runProfile, beforeExecution);
    }
}
