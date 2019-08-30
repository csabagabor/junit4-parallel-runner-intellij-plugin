package gabor.paralleltester.runner;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.JavaCommandLine;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import gabor.paralleltester.Resources;
import org.jetbrains.annotations.NotNull;

public class CustomRunner1 implements CustomDelegatorRunner {

    @Override
    public void doPreExecute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment env) throws ExecutionException {
        JavaParameters javaParameters = ((JavaCommandLine) state).getJavaParameters();
        javaParameters.setMainClass(Resources.PARALLEL_STARTER);
    }
}
