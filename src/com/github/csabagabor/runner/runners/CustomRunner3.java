package com.github.csabagabor.runner.runners;

import com.github.csabagabor.Resources;
import com.github.csabagabor.runner.CustomDelegatorRunner;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.JavaTestFrameworkRunnableState;
import com.intellij.execution.configurations.JavaCommandLine;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.junit.TestPackage;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.util.io.FileUtilRt;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomRunner3 extends CustomDelegatorRunner {

    @Override
    public void doPreExecute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment env) throws ExecutionException {
        super.doPreExecute(state, env);

        if (state instanceof JavaCommandLine) {
            JavaParameters javaParameters = ((JavaCommandLine) state).getJavaParameters();

            ParametersList programParametersList = javaParameters.getProgramParametersList();
            List<String> list = new ArrayList<>(javaParameters.getProgramParametersList().getList());

            String tests = null;
            String testClass = null;

            if (state instanceof TestPackage) {
                Field field = null;
                try {
                    field = JavaTestFrameworkRunnableState.class.getDeclaredField("myTempFile");
                    field.setAccessible(true);
                    Object value = field.get(state);
                    tests = ((File) value).getAbsolutePath();
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException("Cannot get field");
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Cannot access field");
                }
            }

            if (tests == null) {
                tests = processParameters(list);
                testClass = tests;
            }

            //single method is run - no need to parallelize
            if (tests.contains(",")) {
                return;
            }

            File dir = new File(FileUtilRt.getTempDirectory());
            File file = new File(dir, "jun_par_run.tmp");

            PrintWriter writer = null;
            try {
                writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file),
                        "UTF-8"));
                writer.println(tests);
            } catch (UnsupportedEncodingException | FileNotFoundException e) {
                throw new RuntimeException("Cannot write to file");
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

            if (testClass != null) {
                finalParams.remove(testClass);
            }

            finalParams.add(Resources.RUNNABLE_CLASS2);

            for (String finalParam : finalParams) {
                programParametersList.add(finalParam);
            }
        }
    }

    static String processParameters(List<String> args) {
        String arg;
        List<String> excludeArgList = Arrays.asList("-junit3", "-junit4", "-junit5");
        List<String> excludeStartWithArgList = Arrays.asList("@name", "@w@", "@@@", "@@", "@", "-socket");

        for (int i = 0; i < args.size(); ++i) {
            arg = args.get(i);
            if (!arg.startsWith("-ideVersion")) {

                boolean startsWithExcluded = false;

                for (String exclude : excludeStartWithArgList) {
                    if (arg.startsWith(exclude)) {
                        startsWithExcluded = true;
                    }
                }

                if (!startsWithExcluded && !excludeArgList.contains(arg)) {
                    return arg;
                }
            }
        }

        return null;
    }
}
