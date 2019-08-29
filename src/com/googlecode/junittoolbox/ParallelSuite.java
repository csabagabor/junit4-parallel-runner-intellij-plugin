package com.googlecode.junittoolbox;

import com.intellij.openapi.util.SystemInfoRt;
import com.intellij.openapi.util.io.FileUtilRt;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class ParallelSuite extends Suite {

    public ParallelSuite(Class<?> klass, RunnerBuilder builder) throws InitializationError
            , ClassNotFoundException {
        super(builder, klass, getClasses());
        setScheduler(new ParallelScheduler());
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
