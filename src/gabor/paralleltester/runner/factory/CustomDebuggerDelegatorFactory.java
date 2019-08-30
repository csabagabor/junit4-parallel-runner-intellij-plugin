package gabor.paralleltester.runner.factory;

import com.intellij.execution.Executor;
import com.intellij.execution.ProgramRunnerUtil;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import gabor.paralleltester.executor.CustomDebuggerExecutor;

public class CustomDebuggerDelegatorFactory extends CustomDelegatorFactory {
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
