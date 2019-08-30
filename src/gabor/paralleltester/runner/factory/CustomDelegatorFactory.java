package gabor.paralleltester.runner.factory;

import com.intellij.execution.Executor;
import com.intellij.execution.ProgramRunnerUtil;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import gabor.paralleltester.executor.CustomDebuggerExecutor;
import gabor.paralleltester.executor.CustomRunnerExecutor;
import gabor.paralleltester.runner.CustomDelegatorRunner;
import gabor.paralleltester.runner.runners.CustomRunner1;
import gabor.paralleltester.runner.runners.CustomRunner2;
import gabor.paralleltester.runner.runners.CustomRunner3;

import java.util.HashMap;
import java.util.Map;

public abstract class CustomDelegatorFactory {
    protected static int RUNNER_ID = 1;

    protected static Map<Integer, CustomDelegatorRunner> runners = populateRunners();

    protected static Map<Integer, CustomDelegatorRunner> populateRunners() {
        Map<Integer, CustomDelegatorRunner> runners = new HashMap<>();

        //runners.put(1, new CustomRunner1());
       // runners.put(2, new CustomRunner2());
        runners.put(1, new CustomRunner3());

        //default runner(runs tests sequentially)
        runners.put(4, new CustomDelegatorRunner() {
        });

        return runners;
    }

    public static CustomDelegatorRunner getRunner() {
        return runners.get(RUNNER_ID);
    }

    public static void runNextRunner(RunProfileState state, ExecutionEnvironment env, boolean runner) {
        if (runners.containsKey(RUNNER_ID + 1)) {
            RUNNER_ID++;

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
