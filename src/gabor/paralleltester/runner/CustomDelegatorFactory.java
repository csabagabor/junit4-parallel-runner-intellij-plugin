package gabor.paralleltester.runner;

import com.intellij.execution.Executor;
import com.intellij.execution.executors.DefaultRunExecutor;

import java.util.HashMap;
import java.util.Map;

public class CustomDelegatorFactory {
    private static int RUNNER_ID = 1;
    private static Map<Integer, CustomDelegatorRunner> runners = populateRunners();

    private static Map<Integer, CustomDelegatorRunner> populateRunners() {
        Map<Integer, CustomDelegatorRunner> runners = new HashMap<>();

        runners.put(1, new CustomRunner1());
        runners.put(2, new CustomRunner1());

        return runners;
    }

    public static CustomDelegatorRunner getRunner() {
        return runners.get(RUNNER_ID);
    }

    public static void setNextRunner() {
        if (runners.containsKey(RUNNER_ID + 1)) {
            RUNNER_ID++;
//            Executor executor = DefaultRunExecutor.getRunExecutorInstance();
//        ProgramRunnerUtil.executeConfiguration(project, runnerAndConfigurationSettings, executor);
        }
    }
}
