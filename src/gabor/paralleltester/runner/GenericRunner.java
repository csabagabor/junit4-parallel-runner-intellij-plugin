package gabor.paralleltester.runner;

import com.googlecode.junittoolbox.ParallelSuite;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.JavaCommandLine;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.JavaRunConfigurationModule;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.junit.JUnitConfiguration;
import com.intellij.execution.junit.TestClassFilter;
import com.intellij.execution.junit.TestObject;
import com.intellij.execution.junit.TestPackage;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.io.FileUtilRt;
import gabor.paralleltester.Resources;
import gabor.paralleltester.helper.UIHelper;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public interface GenericRunner {

    default void doPreExecute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment env) throws ExecutionException {
        if (state instanceof JavaCommandLine) {
            JavaParameters javaParameters = ((JavaCommandLine) state).getJavaParameters();

            ParametersList programParametersList = javaParameters.getProgramParametersList();

            List<String> list = new ArrayList<>(javaParameters.getProgramParametersList().getList());

            //get list of test(s) or file where they are stored
            //Data data = env.getConfiguration().getPersistentData();


            if (state instanceof TestObject) {
                JUnitConfiguration.Data persistentData = ((TestObject) state).getConfiguration().getPersistentData();
                String testObject = persistentData.TEST_OBJECT;

//                JUnit4TestRunnerUtil.buildRequest(new String[]{processParameters(list)},
//                        null, true);

                if (!"method".equals(testObject)) {
                    File dir = new File(FileUtilRt.getTempDirectory());

                    File file = new File(dir, "jun_par_tes.tmp");

                    PrintWriter writer = null;
                    try {
                        writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file),
                                "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    try {
                        List<String> classNames = new ArrayList<>();
                        if (("pattern".equals(testObject))) {
                            Set<String> patterns = persistentData.getPatterns();
                            classNames = new ArrayList<>(patterns);
                        } else if (("class".equals(testObject))) {
                            classNames = Arrays.asList(persistentData.MAIN_CLASS_NAME);
                        } else if (("package".equals(testObject))) {
                            System.out.println("a");
                            Module module = ((TestObject) state).getConfiguration().getConfigurationModule().getModule();
                            TestClassFilter classFilter = TestPackage.this.getClassFilter(data);
                            ((TestPackage) state).searchTests(env.get, classFilter, this.myClassNames);
                        }

                        for (int i = 0; i < classNames.size(); ++i) {
                            writer.println(classNames.get(i));
                        }

                    } finally {
                        writer.close();
                    }

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

                    if (event.getExitCode() < 0) {
                        UIHelper.showErrorMessage("Cannot run tests in parallel", env.getProject());
                    }
                }
            });
        }
    }

    static String processParameters(List<String> args) {
        String arg;
        List<String> excludeArgList = Arrays.asList("-junit3", "-junit4", "-junit5");
        List<String> excludeStartWithArgList = Arrays.asList("@name", "@w@", "@@@", "@@", "-socket");

        for (int i = 0; i < args.size(); ++i) {
            arg = args.get(i);
            if (!arg.startsWith("-ideVersion")) {

                boolean startsWithExcluded = false;

                for (String exclude : excludeStartWithArgList) {
                    if (arg.startsWith(exclude)) {
                        startsWithExcluded = true;
                    }
                }

                if (!startsWithExcluded && !excludeArgList.contains(arg)) {
                    return arg;
                }
            }
        }

        return null;
    }
}
