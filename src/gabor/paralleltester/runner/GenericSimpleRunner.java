package gabor.paralleltester.runner;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.ProgramRunnerUtil;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import org.jetbrains.annotations.NotNull;

public interface GenericSimpleRunner {



    default void doPostExecute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment env,
                               RunContentDescriptor runContentDescriptor) throws ExecutionException {

        final ProcessHandler processHandler = runContentDescriptor.getProcessHandler();

        if (processHandler != null) {

            processHandler.addProcessListener(new ProcessAdapter() {
                public void processTerminated(@NotNull ProcessEvent event) {
                    if (event == null) {
                        return;
                    }

                    if (event.getExitCode() < 0) {
                        ProgramRunnerUtil.executeConfiguration(env.getRunnerAndConfigurationSettings(), revertBackExecutor());
                    }
                }
            });
        }
    }

    Executor revertBackExecutor();
}
