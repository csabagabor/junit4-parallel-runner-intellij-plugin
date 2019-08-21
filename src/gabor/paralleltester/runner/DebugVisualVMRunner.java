package gabor.paralleltester.runner;

import com.googlecode.junittoolbox.ParallelSuite;
import com.intellij.ExtensionPoints;
import com.intellij.debugger.impl.GenericDebuggerRunner;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.JUnitPatcher;
import com.intellij.execution.configurations.ModuleRunProfile;
import com.intellij.execution.configurations.RunConfigurationWithSuppressedDefaultDebugAction;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.Extensions;
import gabor.paralleltester.executor.DebugVisualVMExecutor;
import org.jetbrains.annotations.NotNull;
import org.junit.runners.model.InitializationError;

public class DebugVisualVMRunner extends GenericDebuggerRunner {
    private static final Logger log = Logger.getInstance(DebugVisualVMRunner.class.getName());

    @NotNull
    public String getRunnerId() {
        return DebugVisualVMExecutor.EXECUTOR_ID;
    }

    @Override
    public void execute(@NotNull final ExecutionEnvironment environment)
            throws ExecutionException {


//        final Object[] patchers = Extensions.getExtensions(ExtensionPoints.JUNIT_PATCHER);
//        for (Object patcher : patchers) {
//            ((JUnitPatcher)patcher).patchJavaParameters(null, null);
//        }
//
//        super.execute(environment);

        try {
            new ParallelSuite(null, null);
        } catch (InitializationError initializationError) {
            initializationError.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected RunContentDescriptor doExecute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment env) throws ExecutionException {

         return super.doExecute(state, env);
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        if (!profile.toString().contains("JUnit")) {
            return false;
        }

        return executorId.equals(DebugVisualVMExecutor.EXECUTOR_ID) &&
                profile instanceof ModuleRunProfile &&
                !(profile instanceof RunConfigurationWithSuppressedDefaultDebugAction);
    }
}
