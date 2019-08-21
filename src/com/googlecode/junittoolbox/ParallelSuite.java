package com.googlecode.junittoolbox;

import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;


public class ParallelSuite extends Suite {

    public ParallelSuite(Class<?> klass, RunnerBuilder builder) throws InitializationError
            , ClassNotFoundException {
        super(builder, klass, new Class<?>[]{Class.forName("Test1")});
        setScheduler(new ParallelScheduler());
    }
}
