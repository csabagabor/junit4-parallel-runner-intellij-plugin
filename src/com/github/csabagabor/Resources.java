package com.github.csabagabor;

public abstract class Resources {

    public static final String RUNNABLE_CLASS = "com.googlecode.junittoolbox.ParallelSuiteClasses";

    public static final String RUNNABLE_CLASS2 = "com.googlecode.junittoolbox.ParallelClasses";

    public static final String PARALLEL_STARTER = "com.github.csabagabor.patches.EntryPointStarter";

    public static final String JUNIT_PARALLEL_RUNNER_NAME = "ParallelRunner";

    public static final String JUNIT_RUNNER2_TMP_FILE = "jun_par_tes.tmp";

    public static final String JUNIT_RUNNER3_TMP_FILE = "jun_par_run.tmp";

    public static final String SAVE_SETTINGS_RUNNER_ID = "junit.parallel.runner.runnerid";

    public static boolean checkJUnit4() {
        try {
            Class.forName("org.junit.Test");
            return true;
        } catch (Throwable e) {
            return false;
        }
    }
}
