package gabor.paralleltester.runner;

import com.intellij.debugger.impl.GenericDebuggerRunner;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.RunConfigurationWithSuppressedDefaultDebugAction;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.junit.JUnitConfiguration;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.diagnostic.Logger;
import gabor.paralleltester.executor.CustomDebuggerExecutor;
import org.jetbrains.annotations.NotNull;

public class CustomDebuggerRunner extends GenericDebuggerRunner {
    private static final Logger log = Logger.getInstance(CustomDebuggerRunner.class.getName());

    @NotNull
    public String getRunnerId() {
        return CustomDebuggerExecutor.EXECUTOR_ID;
    }

    @Override
    protected RunContentDescriptor doExecute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment env) throws ExecutionException {

        //doPreExecute(state, env);
        RunContentDescriptor runContentDescriptor = super.doExecute(state, env);
        //doPostExecute(state, env, runContentDescriptor);

        return runContentDescriptor;
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return executorId.equals(CustomDebuggerExecutor.EXECUTOR_ID) &&
                profile instanceof JUnitConfiguration &&
                !(profile instanceof RunConfigurationWithSuppressedDefaultDebugAction);
    }
}