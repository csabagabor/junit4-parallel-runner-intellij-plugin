package com.github.csabagabor.runner.factory;

import com.github.csabagabor.Resources;
import com.github.csabagabor.executor.CustomDebuggerExecutor;
import com.github.csabagabor.executor.CustomRunnerExecutor;
import com.github.csabagabor.runner.CustomDelegatorRunner;
import com.github.csabagabor.runner.runners.CustomRunner1;
import com.github.csabagabor.runner.runners.CustomRunner2;
import com.github.csabagabor.runner.runners.CustomRunner3;
import com.intellij.execution.Executor;
import com.intellij.execution.ProgramRunnerUtil;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.util.io.FileUtilRt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public abstract class CustomDelegatorFactory {
    protected static Integer RUNNER_ID = loadRunnerId();
    private static int incrementRunnerId = 0;

    private static int loadRunnerId() {
        return PropertiesComponent.getInstance().getInt(Resources.SAVE_SETTINGS_RUNNER_ID, 1);
    }

    protected static Map<Integer, CustomDelegatorRunner> runners = populateRunners();

    protected static Map<Integer, CustomDelegatorRunner> populateRunners() {
        Map<Integer, CustomDelegatorRunner> runners = new HashMap<>();

        //this plugin was developed with portability in mind, so even if the imported classes change in the
        //custom runners(that specific custom runner will fail), there are other runners which can be run
        try {
            addRunner(runners, new CustomRunner1());
        } catch (Throwable ignore) {
        }

        try {
            addRunner(runners, new CustomRunner2());
        } catch (Throwable ignore) {
        }

        try {
            addRunner(runners, new CustomRunner3());
        } catch (Throwable ignore) {
        }

        //default runner(runs tests sequentially)
        addRunner(runners, new CustomDelegatorRunner() {
        });

        return runners;
    }

    private static void addRunner(Map<Integer, CustomDelegatorRunner> runners, CustomDelegatorRunner runner) {
        incrementRunnerId++;
        runners.put(incrementRunnerId, runner);
    }

    public static CustomDelegatorRunner getRunner() {
        return runners.get(RUNNER_ID);
    }

    public static void runNextRunner(RunProfileState state, ExecutionEnvironment env, boolean runner) {
        File dir = new File(FileUtilRt.getTempDirectory());
        File file = new File(dir, "factory.tmp");

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file),
                    "UTF-8"));
            writer.println(RUNNER_ID.toString());
        } catch (UnsupportedEncodingException | FileNotFoundException e) {
            throw new RuntimeException("Cannot write to file");
        } finally {
            if (writer != null) {
                writer.close();
            }
        }

        if (runners.containsKey(RUNNER_ID + 1)) {
            RUNNER_ID++;

            //save the id of the current runner only if it is not the default runner(last one)
            //in case it is the default runner, on the next startup the first runner will be tried again
            if(RUNNER_ID < runners.size()) {
                PropertiesComponent.getInstance().setValue(Resources.SAVE_SETTINGS_RUNNER_ID, RUNNER_ID.toString());
            }else{
                PropertiesComponent.getInstance().setValue(Resources.SAVE_SETTINGS_RUNNER_ID, "1");
            }

            Executor executor = null;
            if (runner) {
                executor = new CustomRunnerExecutor();
            } else {
                executor = new CustomDebuggerExecutor();
            }

            ProgramRunnerUtil.executeConfiguration(env.getRunnerAndConfigurationSettings(), executor);
        }
    }
}
