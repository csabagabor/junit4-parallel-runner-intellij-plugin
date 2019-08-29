package gabor.paralleltester.runner;

import com.googlecode.junittoolbox.ParallelSuite;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.ProgramRunnerUtil;
import com.intellij.execution.configurations.JavaCommandLine;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.impl.DefaultJavaProgramRunner;
import com.intellij.execution.junit.JUnitConfiguration;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.RunConfigurationWithSuppressedDefaultRunAction;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.diagnostic.Logger;
import gabor.paralleltester.executor.RunVisualVMExecutor;
import org.jetbrains.annotations.NotNull;

public class SimpleParallelRunner extends DefaultJavaProgramRunner implements GenericSimpleRunner {
    private static final Logger log = Logger.getInstance(RunVisualVMRunner.class.getName());

    @NotNull
    public String getRunnerId() {
        return RunVisualVMExecutor.RUN_WITH_VISUAL_VM;
    }

    @Override
    protected RunContentDescriptor doExecute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment env) throws ExecutionException {
        JavaParameters javaParameters = ((JavaCommandLine) state).getJavaParameters();

        javaParameters.getClassPath().addFirst(PathManager.getPluginsPath());
        javaParameters.getClassPath().addFirst(PathManager.getJarPathForClass(ParallelSuite.class));

        RunContentDescriptor runContentDescriptor = super.doExecute(state, env);
        doPostExecute(state, env, runContentDescriptor);

        return runContentDescriptor;
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return executorId.equals(RunVisualVMExecutor.RUN_WITH_VISUAL_VM) &&
                profile instanceof JUnitConfiguration &&
                !(profile instanceof RunConfigurationWithSuppressedDefaultRunAction);
    }

    @Override
    public Executor revertBackExecutor() {
        return new RunVisualVMExecutor();
    }
}
