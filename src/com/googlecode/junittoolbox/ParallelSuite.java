package com.googlecode.junittoolbox;

import com.intellij.openapi.util.SystemInfoRt;
import com.intellij.openapi.util.io.FileUtilRt;
import org.junit.runner.Runner;
import org.junit.runners.ParentRunner;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;
import org.junit.runners.model.RunnerScheduler;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class ParallelSuite extends Suite {
    private Class<?>[] classes;

    public ParallelSuite(Class<?> klass, RunnerBuilder builder) throws InitializationError {
        this(klass, builder, null);
    }

    private ParallelSuite(Class<?> klass, RunnerBuilder builder, Class<?>[] classes) throws InitializationError {
        super(builder, klass, classes = getClasses());

        int nrThread = getNumberThreads();

        //3 classes, 12 logical processors
        if (classes.length <= nrThread / 4) {
            List<Runner> children = this.getChildren();
            for (Runner child : children) {
                ((ParentRunner) child).setScheduler(new ParallelScheduler(nrThread));
            }
        } else {
            setScheduler(new ParallelScheduler(nrThread));
        }
    }

    private int getNumberThreads() {
        int numThreads;
        try {
            String configuredNumThreads = System.getProperty("maxParallelTestThreads");
            numThreads = Math.max(2, Integer.parseInt(configuredNumThreads));
        } catch (Exception ignored) {
            Runtime runtime = Runtime.getRuntime();
            numThreads = Math.max(2, runtime.availableProcessors());
        }
        return numThreads;
    }

    private static Class<?>[] getClasses() {
        List<String> result = new ArrayList<>();
        File dir = new File(calcCanonicalTempPath());
        File file = new File(dir, "jun_par_tes.tmp");

        BufferedReader reader = null;
        try {

            reader = new BufferedReader(new FileReader(file.getAbsolutePath()));
            String className = null;
            while ((className = reader.readLine()) != null) {
                className = className.replace("\n", "")
                        .replace("\r", "")
                        .replaceAll(" ", "");

                if (!className.isEmpty()) {
                    result.add(className);
                    System.out.println("className:" + className);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Class[] classes = new Class[result.size()];
        for (int i = 0; i < result.size(); ++i) {
            classes[i] = loadTestClass(result.get(i));
        }

        return classes;
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

    private static Class loadTestClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            System.exit(-3);
        }

        return null;
    }
}
