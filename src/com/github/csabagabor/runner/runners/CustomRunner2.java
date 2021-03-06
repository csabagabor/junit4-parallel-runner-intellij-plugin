package com.github.csabagabor.runner.runners;

import com.github.csabagabor.Resources;
import com.github.csabagabor.runner.CustomDelegatorRunner;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Location;
import com.intellij.execution.configurations.JavaCommandLine;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.junit.JUnitConfiguration;
import com.intellij.execution.junit.TestClassFilter;
import com.intellij.execution.junit.TestObject;
import com.intellij.execution.junit.TestPackage;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomRunner2 extends CustomDelegatorRunner {

    @Override
    public void doPreExecute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment env) throws ExecutionException {
        super.doPreExecute(state, env);

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
                    File file = new File(dir, Resources.JUNIT_RUNNER2_TMP_FILE);

                    PrintWriter writer = null;
                    try {
                        writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file),
                                "UTF-8"));
                    } catch (UnsupportedEncodingException | FileNotFoundException e) {
                        throw new RuntimeException("Cannot write to file");
                    }

                    try {
                        if (("pattern".equals(testObject))) {
                            Set<String> patterns = persistentData.getPatterns();
                            classNames = new ArrayList<>(patterns);
                        } else if (("class".equals(testObject))) {
                            classNames = Arrays.asList(persistentData.MAIN_CLASS_NAME);
                        } else if (("package".equals(testObject))) {
                            Set myClassNames = new HashSet();

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
                                } else if (myClassNames.iterator().next() instanceof PsiClass) {
                                    for (PsiClass psiElement : (Set<PsiClass>) myClassNames) {
                                        classNames.add(psiElement.getQualifiedName());
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
                        throw new RuntimeException("Cannot invoke method");
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
