package gabor.paralleltester.runner.factory;

import com.intellij.execution.Executor;
import com.intellij.execution.ProgramRunnerUtil;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import gabor.paralleltester.executor.CustomRunnerExecutor;

public class CustomRunnerDelegatorFactory extends CustomDelegatorFactory{
    public static void runNextRunner(RunProfileState state, ExecutionEnvironment env) {
            if (runners.containsKey(RUNNER_ID + 1)) {
                RUNNER_ID++;
                Executor executor = new CustomRunnerExecutor();
                ProgramRunnerUtil.executeConfiguration(env.getRunnerAndConfigurationSettings(), executor);
            } else {
                //run default runner
                Executor executor = DefaultRunExecutor.getRunExecutorInstance();
                ProgramRunnerUtil.executeConfiguration(env.getRunnerAndConfigurationSettings(), executor);
            }
    }
}
