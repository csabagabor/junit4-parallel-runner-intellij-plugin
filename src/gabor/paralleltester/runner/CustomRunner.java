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
import com.intellij.execution.Location;
import com.intellij.execution.configurations.JavaCommandLine;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.impl.DefaultJavaProgramRunner;
import com.intellij.execution.junit.JUnitConfiguration;
import com.intellij.execution.junit.TestClassFilter;
import com.intellij.execution.junit.TestObject;
import com.intellij.execution.junit.TestPackage;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.RunConfigurationWithSuppressedDefaultRunAction;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import gabor.paralleltester.Resources;
import gabor.paralleltester.executor.CustomRunnerExecutor;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public class CustomRunner extends DefaultJavaProgramRunner {
    private static final Logger log = Logger.getInstance(CustomRunner.class.getName());
    private int runnerID = 1;
    private String originalMainClass;
    private List<String> originalList;
    private static int nr = 0;

    @NotNull
    public String getRunnerId() {
        return CustomRunnerExecutor.RUN_WITH_VISUAL_VM;
    }

    @Override
    protected RunContentDescriptor doExecute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment env) throws ExecutionException {
        nr++;
        JavaParameters javaParameters = ((JavaCommandLine) state).getJavaParameters();

        javaParameters.getClassPath().addFirst(PathManager.getPluginsPath());
        javaParameters.getClassPath().addFirst(PathManager.getJarPathForClass(ParallelSuite.class));

        originalMainClass = javaParameters.getMainClass();
        originalList = new ArrayList<>(javaParameters.getProgramParametersList().getList());

        RunContentDescriptor runContentDescriptor = null;

        javaParameters.setMainClass(Resources.PARALLEL_STARTER);
        runContentDescriptor = doCustomExecute(state, env);
//        revertParams(originalMainClass, originalList, javaParameters);
//        doPreExecute2(state, env);
//        runContentDescriptor = doCustomExecute(state, env);

        return runContentDescriptor;
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return executorId.equals(CustomRunnerExecutor.RUN_WITH_VISUAL_VM) &&
                profile instanceof JUnitConfiguration &&
                !(profile instanceof RunConfigurationWithSuppressedDefaultRunAction);
    }

    private void revertParams(String originalMainClass, List<String> originalList,
                              JavaParameters javaParameters) {
        javaParameters.setMainClass(originalMainClass);

        javaParameters.getProgramParametersList().clearAll();

        for (String param : originalList) {
            javaParameters.getProgramParametersList().add(param);
        }
    }

    private RunContentDescriptor doCustomExecute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment env) throws ExecutionException {
        RunContentDescriptor runContentDescriptor = super.doExecute(state, env);
        doPostExecute(state, env, runContentDescriptor);

        return runContentDescriptor;
    }

    private void doPostExecute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment env,
                               RunContentDescriptor runContentDescriptor) throws ExecutionException {
        final ProcessHandler processHandler = runContentDescriptor.getProcessHandler();

        if (processHandler != null) {
            processHandler.addProcessListener(new ProcessAdapter() {
                public void processTerminated(@NotNull ProcessEvent event) {
                    if (event == null) {
                        return;
                    }

                    if (event.getExitCode() < 0) {
                        try {
                            runNextRunner(state, env);
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    private void runNextRunner(RunProfileState state, ExecutionEnvironment env) throws ExecutionException {
        JavaParameters javaParameters = ((JavaCommandLine) state).getJavaParameters();

        runnerID++;
        if (runnerID == 2) {
            revertParams(originalMainClass, originalList, javaParameters);
            doPreExecute2(state, env);
            doCustomExecute(state, env);
        } else if (runnerID == 3) {
            runnerID = 1;
            revertParams(originalMainClass, originalList, javaParameters);
            doCustomExecute(state, env);
        }
    }

    private void doPreExecute2(@NotNull RunProfileState state, @NotNull ExecutionEnvironment env) throws
            ExecutionException {
        if (state instanceof JavaCommandLine) {
            JavaParameters javaParameters = ((JavaCommandLine) state).getJavaParameters();

            ParametersList programParametersList = javaParameters.getProgramParametersList();
            List<String> list = new ArrayList<>(javaParameters.getProgramParametersList().getList());

            if (state instanceof TestObject) {
                JUnitConfiguration.Data persistentData = ((TestObject) state).getConfiguration().getPersistentData();
                String testObject = persistentData.TEST_OBJECT;

                if (!"method".equals(testObject)) {
                    List<String> classNames = new ArrayList<>();

                    File dir = new File(FileUtilRt.getTempDirectory());
                    File file = new File(dir, "jun_par_tes.tmp");

                    PrintWriter writer = null;
                    try {
                        writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file),
                                "UTF-8"));
                    } catch (UnsupportedEncodingException | FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    try {
                        if (("pattern".equals(testObject))) {
                            Set<String> patterns = persistentData.getPatterns();
                            classNames = new ArrayList<>(patterns);
                        } else if (("class".equals(testObject))) {
                            classNames = Arrays.asList(persistentData.MAIN_CLASS_NAME);
                        } else if (("package".equals(testObject))) {
                            Set myClassNames = new TreeSet();

                            Module module = ((TestObject) state).getConfiguration().getConfigurationModule().getModule();

                            Method method = TestPackage.class.getDeclaredMethod("getClassFilter",
                                    JUnitConfiguration.Data.class);
                            method.setAccessible(true);
                            com.intellij.execution.junit.TestClassFilter classFilter =
                                    (TestClassFilter) method.invoke(state, persistentData);


                            Method method2 = TestPackage.class.getDeclaredMethod("searchTests",
                                    Module.class, com.intellij.execution.junit.TestClassFilter.class, Set.class);
                            method2.setAccessible(true);
                            method2.invoke(state, module, classFilter, myClassNames);
                            if (myClassNames.size() > 0) {
                                if (myClassNames.iterator().next() instanceof Location) {
                                    for (Location myClassName : (Set<Location>) myClassNames) {
                                        PsiElement psiElement = myClassName.getPsiElement();
                                        if (psiElement instanceof PsiClass) {
                                            classNames.add(((PsiClass) psiElement).getQualifiedName());
                                        }
                                    }
                                } else {
                                    for (String myClassName : (Set<String>) myClassNames) {
                                        classNames.add(myClassName);
                                    }
                                }
                            }
                        }

                        for (int i = 0; i < classNames.size(); ++i) {
                            writer.println(classNames.get(i));
                        }

                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    } finally {
                        if (writer != null) {
                            writer.close();
                        }
                    }

                    programParametersList.clearAll();

                    List<String> finalParams = new ArrayList<>();

                    for (String s : list) {
                        if (!s.contains("@")) {
                            finalParams.add(s);
                        }
                    }

                    if (classNames.size() == 1) {
                        finalParams.remove(classNames.get(0));
                    }

                    finalParams.add(Resources.RUNNABLE_CLASS);

                    for (String finalParam : finalParams) {
                        programParametersList.add(finalParam);
                    }
                }
            }
        }
    }
}
