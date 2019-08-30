package gabor.paralleltester;

import com.intellij.openapi.util.IconLoader;
import com.intellij.rt.ant.execution.SegmentedOutputStream;

import javax.swing.*;
import java.util.ArrayList;

public class Resources {

    public static final Icon RUN_13 = IconLoader.getIcon("run13.png");

    public static final Icon RUN = IconLoader.getIcon("run13.png");


    public static final Icon DEBUG_13 = IconLoader.getIcon("debug13.png");

    public static final Icon DEBUG = IconLoader.getIcon("debug13.png");

    public static final String RUNNABLE_CLASS = "com.googlecode.junittoolbox.ParallelSuiteClasses";

    public static final String RUNNABLE_CLASS2 = "com.googlecode.junittoolbox.ParallelClasses";

    public static final String PARALLEL_STARTER = "gabor.paralleltester.patches.EntryPointStarter";

    public static final String JUNIT_PARALLEL_RUNNER_NAME = "gabor.paralleltester.patches.ParallelRunner";

    public static final String JUNIT_RUNNER2_TMP_FILE = "jun_par_tes.tmp";

    public static final String JUNIT_RUNNER3_TMP_FILE = "jun_par_run.tmp";

    public static final String SAVE_SETTINGS_RUNNER_ID = "junit.parallel.runner.runnerid";
}
