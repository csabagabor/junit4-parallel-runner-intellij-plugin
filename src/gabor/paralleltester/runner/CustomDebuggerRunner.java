package gabor.paralleltester.runner;

import com.googlecode.junittoolbox.ParallelSuite;
import com.intellij.debugger.impl.GenericDebuggerRunner;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.*;
import com.intellij.execution.junit.JUnitConfiguration;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.diagnostic.Logger;
import gabor.paralleltester.executor.CustomDebuggerExecutor;
import gabor.paralleltester.runner.factory.CustomDelegatorFactory;
import org.jetbrains.annotations.NotNull;

public class CustomDebuggerRunner extends GenericDebuggerRunner {
    private static final Logger log = Logger.getInstance(CustomDebuggerRunner.class.getName());
    private CustomDelegatorRunner delegatorRunner;

    @NotNull
    public String getRunnerId() {
        return CustomDebuggerExecutor.EXECUTOR_ID;
    }

    @Override
    protected RunContentDescriptor doExecute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment env) throws ExecutionException {
        JavaParameters javaParameters = ((JavaCommandLine) state).getJavaParameters();
        javaParameters.getClassPath().addFirst(PathManager.getPluginsPath());
        javaParameters.getClassPath().addFirst(PathManager.getJarPathForClass(ParallelSuite.class));

        delegatorRunner = CustomDelegatorFactory.getRunner();
        delegatorRunner.doPreExecute(state, env);
        RunContentDescriptor runContentDescriptor = super.doExecute(state, env);
        delegatorRunner.doPostExecute(state, env, runContentDescriptor, new Runnable() {
            @Override
            public void run() {
                CustomDelegatorFactory.runNextRunner(state, env, false);
            }
        });

        return runContentDescriptor;
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return executorId.equals(CustomDebuggerExecutor.EXECUTOR_ID) &&
                profile instanceof JUnitConfiguration &&
                !(profile instanceof RunConfigurationWithSuppressedDefaultDebugAction);
    }
}
