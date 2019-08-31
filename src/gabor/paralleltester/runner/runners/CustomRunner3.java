package gabor.paralleltester.runner.runners;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.JavaTestFrameworkRunnableState;
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
import gabor.paralleltester.Resources;
import gabor.paralleltester.runner.CustomDelegatorRunner;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class CustomRunner3 implements CustomDelegatorRunner {

    @Override
    public void doPreExecute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment env) throws ExecutionException {
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
