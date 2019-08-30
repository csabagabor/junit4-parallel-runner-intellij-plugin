package gabor.paralleltester.runner;

import com.intellij.execution.Executor;
import com.intellij.execution.ProgramRunnerUtil;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import gabor.paralleltester.executor.CustomDebuggerExecutor;
import gabor.paralleltester.executor.CustomRunnerExecutor;

import java.util.HashMap;
import java.util.Map;

public class CustomDebuggerDelegatorFactory extends CustomDelegatorFactory {
    private static Map<Integer, CustomDelegatorRunner> runners = populateRunners();

    private static Map<Integer, CustomDelegatorRunner> populateRunners() {
        Map<Integer, CustomDelegatorRunner> runners = new HashMap<>();

        runners.put(1, new CustomRunner1());
        runners.put(2, new CustomRunner2());

        return runners;
    }

    public static CustomDelegatorRunner getRunner() {
        return runners.get(RUNNER_ID);
    }

    public static void runNextRunner(RunProfileState state, ExecutionEnvironment env) {
        if (runners.containsKey(RUNNER_ID + 1)) {
            RUNNER_ID++;
            Executor executor = new CustomDebuggerExecutor();
            ProgramRunnerUtil.executeConfiguration(env.getRunnerAndConfigurationSettings(), executor);
        } else {
            //run default runner
            Executor executor = DefaultDebugExecutor.getDebugExecutorInstance();
            ProgramRunnerUtil.executeConfiguration(env.getRunnerAndConfigurationSettings(), executor);
        }
    }
}
