package gabor.paralleltester.runner;

import com.intellij.debugger.impl.GenericDebuggerRunner;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.ModuleRunProfile;
import com.intellij.execution.configurations.RunConfigurationWithSuppressedDefaultDebugAction;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.execution.junit.TestObject;
import com.intellij.execution.junit.TestsPattern;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiDirectory;
import gabor.paralleltester.executor.DebugVisualVMExecutor;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashSet;
import java.util.Set;

public class DebugVisualVMRunner extends GenericDebuggerRunner {
    private static final Logger log = Logger.getInstance(DebugVisualVMRunner.class.getName());

    @NotNull
    public String getRunnerId() {
        return DebugVisualVMExecutor.EXECUTOR_ID;
    }

    @Override
    public void execute(@NotNull final ExecutionEnvironment environment)
            throws ExecutionException {

        super.execute(environment);
    }

    @Override
    public void patch(JavaParameters javaParameters, RunnerSettings settings, RunProfile runProfile, boolean beforeExecution) throws ExecutionException {

        super.patch(javaParameters, settings, runProfile, beforeExecution);
    }

    @Override
    protected RunContentDescriptor doExecute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment env) throws ExecutionException {


        Class<?> testClass = null;
        try {
            testClass = Class.forName("com.intellij.execution.junit.TestClass");

            if (state instanceof TestsPattern) {
                Set<String> patterns = ((TestsPattern) state).getConfiguration().getPersistentData().getPatterns();

                patterns.clear();
                patterns.add("gabor.paralleltester.runner.A");
                ((TestsPattern) state).getConfiguration().getPersistentData().setPatterns((LinkedHashSet<String>)
                        patterns);

            } else if (testClass.isInstance(state)) {
                Object obj = testClass.cast(state);
                String mainClassName = ((TestObject) (testClass.cast(state))).
                        getConfiguration().getPersistentData()
                        .MAIN_CLASS_NAME = "com.googlecode.junittoolbox.C";

                       // setMainClass("gabor.paralleltester.runner.A");
            }


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        return super.doExecute(state, env);
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        if (profile.toString().contains("Application")) {
            return false;
        }

        return executorId.equals(DebugVisualVMExecutor.EXECUTOR_ID) &&
                profile instanceof ModuleRunProfile &&
                !(profile instanceof RunConfigurationWithSuppressedDefaultDebugAction);
    }
}
