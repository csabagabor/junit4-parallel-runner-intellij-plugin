
package gabor.paralleltester.runner;

import com.googlecode.junittoolbox.ParallelScheduler;
import com.intellij.junit4.JUnit4TestListener;
import com.intellij.junit4.JUnit4TestRunnerUtil;
import com.intellij.rt.execution.junit.IDEAJUnitListener;
import intellij.junit.plugin.JUnit4IdeaTestRunner;
import org.junit.runner.*;
import org.junit.runners.ParentRunner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ParallelRunner extends JUnit4IdeaTestRunner {

    public int startRunnerWithArgs(String[] args, String name, int count, boolean sendTree) {
        try {
            final Request request = JUnit4TestRunnerUtil.buildRequest(args, name, sendTree);
            if (request == null) return -2;

            final Runner testRunner = request.getRunner();
            Description description = getDescription(request, testRunner);
            if (description == null) {
                return -2;
            }

            //changed code
            if (testRunner instanceof ParentRunner) {
                List<String> classes = new ArrayList<>();

                for (Description child : testRunner.getDescription().getChildren()) {
                    classes.add(child.getClassName());
                }

                int numThreads;
                try {
                    String configuredNumThreads = System.getProperty("maxParallelTestThreads");
                    numThreads = Math.max(2, Integer.parseInt(configuredNumThreads));
                } catch (Exception ignored) {
                    Runtime runtime = Runtime.getRuntime();
                    numThreads = Math.max(2, runtime.availableProcessors());
                }

                ParentRunner parentRunner = (ParentRunner) testRunner;


                parentRunner.setScheduler(new ParallelScheduler());
            }

            //

            if (sendTree) {
                do {
                    ((JUnit4TestListener) myTestsListener).sendTree(description);
                }
                while (--count > 0);
            }

            final JUnitCore runner = new JUnitCore();
            runner.addListener(myTestsListener);
            for (Iterator iterator = myListeners.iterator(); iterator.hasNext(); ) {
                final IDEAJUnitListener junitListener = (IDEAJUnitListener) Class.forName((String) iterator.next()).newInstance();
                runner.addListener(new MyCustomRunListenerWrapper(junitListener, description.getDisplayName()));
            }
            final Result result = runner.run(testRunner);
            return result.wasSuccessful() ? 0 : -1;
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return -2;
        }
    }
}