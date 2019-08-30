package gabor.paralleltester.runner;

import com.intellij.execution.Executor;
import com.intellij.execution.ProgramRunnerUtil;
import com.intellij.execution.configurations.JavaCommandLine;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import gabor.paralleltester.Resources;
import gabor.paralleltester.executor.CustomRunnerExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomDelegatorFactory {
    private static int RUNNER_ID = 1;
    private static Map<Integer, CustomDelegatorRunner> runners = populateRunners();
    private static String originalStarterClass;
    private static List<String> originalParams;

    private static Map<Integer, CustomDelegatorRunner> populateRunners() {
        Map<Integer, CustomDelegatorRunner> runners = new HashMap<>();

        runners.put(1, new CustomRunner1());
        runners.put(2, new CustomRunner2());

        return runners;
    }

    public static void setOriginalParams(JavaParameters javaParameters) {
        originalStarterClass = javaParameters.getMainClass();
        ParametersList programParametersList = javaParameters.getProgramParametersList();
        originalParams = new ArrayList<>(javaParameters.getProgramParametersList().getList());
    }

    public static CustomDelegatorRunner getRunner() {
        return runners.get(RUNNER_ID);
    }

    public static void setNextRunner(RunProfileState state, ExecutionEnvironment env) {
        if (runners.containsKey(RUNNER_ID + 1)) {
            RUNNER_ID++;
            Executor executor = new CustomRunnerExecutor();
            ProgramRunnerUtil.executeConfiguration(env.getRunnerAndConfigurationSettings(), executor);
        }
    }
}
