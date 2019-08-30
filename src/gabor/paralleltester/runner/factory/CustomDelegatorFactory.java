package gabor.paralleltester.runner.factory;

import com.intellij.execution.Executor;
import com.intellij.execution.ProgramRunnerUtil;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.ide.util.PropertiesComponent;
import gabor.paralleltester.Resources;
import gabor.paralleltester.executor.CustomDebuggerExecutor;
import gabor.paralleltester.executor.CustomRunnerExecutor;
import gabor.paralleltester.runner.CustomDelegatorRunner;
import gabor.paralleltester.runner.runners.CustomRunner1;
import gabor.paralleltester.runner.runners.CustomRunner2;
import gabor.paralleltester.runner.runners.CustomRunner3;

import java.util.HashMap;
import java.util.Map;

public abstract class CustomDelegatorFactory {
    protected static Integer RUNNER_ID = loadRunnerId();
    private static int incrementRunnerId = 0;

    private static int loadRunnerId() {
        return PropertiesComponent.getInstance().getInt(Resources.SAVE_SETTINGS_RUNNER_ID, 1);
    }

    protected static Map<Integer, CustomDelegatorRunner> runners = populateRunners();

    protected static Map<Integer, CustomDelegatorRunner> populateRunners() {
        Map<Integer, CustomDelegatorRunner> runners = new HashMap<>();

        //this plugin was developed with portability in mind, so even if the imported classes change in the
        //custom runners(that specific custom runner will fail), there are other runners which can be run
        try {
            addRunner(runners, new CustomRunner1());
        } catch (NoClassDefFoundError ignore) {
        }

        try {
            addRunner(runners, new CustomRunner2());
        } catch (NoClassDefFoundError ignore) {
        }

        try {
            addRunner(runners, new CustomRunner3());
        } catch (NoClassDefFoundError ignore) {
        }

        //default runner(runs tests sequentially)
        addRunner(runners, new CustomDelegatorRunner() {
        });

        return runners;
    }

    private static void addRunner(Map<Integer, CustomDelegatorRunner> runners, CustomDelegatorRunner runner) {
        incrementRunnerId++;
        runners.put(incrementRunnerId, runner);
    }

    public static CustomDelegatorRunner getRunner() {
        return runners.get(RUNNER_ID);
    }

    public static void runNextRunner(RunProfileState state, ExecutionEnvironment env, boolean runner) {
        if (runners.containsKey(RUNNER_ID + 1)) {
            RUNNER_ID++;

            PropertiesComponent.getInstance().setValue(Resources.SAVE_SETTINGS_RUNNER_ID, RUNNER_ID.toString());

            Executor executor = null;
            if (runner) {
                executor = new CustomRunnerExecutor();
            } else {
                executor = new CustomDebuggerExecutor();
            }

            ProgramRunnerUtil.executeConfiguration(env.getRunnerAndConfigurationSettings(), executor);
        }
    }
}
