package com.googlecode.junittoolbox;

import com.intellij.junit4.JUnit4TestRunnerUtil;
import com.github.csabagabor.Resources;
import org.junit.runner.Description;
import org.junit.runner.Request;
import org.junit.runner.Runner;
import org.junit.runners.ParentRunner;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class ParallelSuite2 extends Suite {
    private Class<?>[] classes;

    public ParallelSuite2(Class<?> klass, RunnerBuilder builder) throws InitializationError {
        super(builder, klass, getClasses());

        if (this.getChildren().size() == 1) {
            Runner child = this.getChildren().get(0);
            ((ParentRunner) child).setScheduler(new ParallelScheduler());
        } else {
            setScheduler(new ParallelScheduler());
        }
    }

    private static Class<?>[] getClasses() {
        List<Class> result = new ArrayList<>();
        File dir = new File(calcCanonicalTempPath());
        File file = new File(dir, Resources.JUNIT_RUNNER3_TMP_FILE);

        BufferedReader reader = null;
        try {

            reader = new BufferedReader(new FileReader(file.getAbsolutePath()));
            String tests = reader.readLine();

            if (tests == null) {
                System.exit(-3);
            } else {
                if (tests.contains("idea_junit") || tests.contains(".tmp")) {
                    tests = "@" + tests;
                }

                Request request = JUnit4TestRunnerUtil.buildRequest(new String[]{tests}, null, true);
                Runner runner = request.getRunner();

                ArrayList<Description> children = runner.getDescription().getChildren();
                if (children.size() > 0) {
                    ArrayList<Description> grandchildren = children.get(0).getChildren();

                    if (grandchildren.size() > 0 || runner.getDescription().getTestClass() == null) {
                        //there are multiple test classes
                        for (Description child : children) {
                            result.add(child.getTestClass());
                        }
                    } else {
                        //only one test class
                        result.add(runner.getDescription().getTestClass());
                    }
                }

                Class[] classes = new Class[result.size()];
                for (int i = 0; i < result.size(); ++i) {
                    classes[i] = result.get(i);
                }

                return classes;
            }

        } catch (IOException e) {
            System.exit(-4);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                System.exit(-4);
            }
        }

        return null;
    }

    private static String calcCanonicalTempPath() {
        final File file = new File(System.getProperty("java.io.tmpdir"));
        try {
            final String canonical = file.getCanonicalPath();
            if (!isWindows() || !canonical.contains(" ")) {
                return canonical;
            }
        } catch (IOException ignore) {
        }
        return file.getAbsolutePath();
    }

    private static boolean isWindows() {
        String OS_NAME = System.getProperty("os.name");
        String _OS_NAME = OS_NAME.toLowerCase(Locale.ENGLISH);
        return _OS_NAME.startsWith("windows");
    }
}
