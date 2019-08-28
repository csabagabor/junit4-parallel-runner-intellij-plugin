package gabor.paralleltester.runner;

import com.googlecode.junittoolbox.ParallelSuite;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.JavaCommandLine;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.application.PathManager;
import gabor.paralleltester.Resources;
import gabor.paralleltester.helper.UIHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public interface GenericRunner {

    default void doPreExecute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment env) throws ExecutionException {
        if (state instanceof JavaCommandLine) {
            JavaParameters javaParameters = ((JavaCommandLine) state).getJavaParameters();

            ParametersList programParametersList = javaParameters.getProgramParametersList();

            List<String> list = new ArrayList<>(javaParameters.getProgramParametersList().getList());

            programParametersList.clearAll();

            boolean classAdded = false;
            for (String s : list) {
                if (!s.contains("@")) {
                    programParametersList.add(s);
                } else if (!classAdded) {
                    classAdded = true;
                    programParametersList.add(Resources.RUNNABLE_CLASS);
                }
            }

            javaParameters.getClassPath().addFirst(PathManager.getPluginsPath());
            javaParameters.getClassPath().addFirst(PathManager.getJarPathForClass(ParallelSuite.class));
        }
    }

    default void doPostExecute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment env,
                               RunContentDescriptor runContentDescriptor) throws ExecutionException {

        final ProcessHandler processHandler = runContentDescriptor.getProcessHandler();

        if (processHandler != null) {

            processHandler.addProcessListener(new ProcessAdapter() {
                public void processTerminated(@NotNull ProcessEvent event) {
                    if (event == null) {
                        return;
                    }
                    UIHelper.showErrorMessage("Cannot run tests in parallel", env.getProject());
                }
            });
        }
    }
}
