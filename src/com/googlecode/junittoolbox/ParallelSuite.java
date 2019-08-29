package com.googlecode.junittoolbox;

import com.intellij.openapi.util.io.FileUtilRt;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class ParallelSuite extends Suite {

    public ParallelSuite(Class<?> klass, RunnerBuilder builder) throws InitializationError
            , ClassNotFoundException {
        super(builder, klass, getClasses());
        setScheduler(new ParallelScheduler());
    }

    private static Class<?>[] getClasses() {
        List<Class<?>> classes = new ArrayList<>();
        //com.intellij.junit4.JUnit4IdeaTestRunner
        File dir = new File(FileUtilRt.getTempDirectory());

        File file = new File(dir, "jun_par_tes.tmp");

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file.getName()));

            String className = reader.readLine();
            if (className != null) {
                classes.add(Class.forName(className));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return (Class<?>[]) classes.toArray();
    }
}
