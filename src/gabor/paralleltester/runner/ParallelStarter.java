//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package gabor.paralleltester.runner;

import intellij.junit.plugin.JUnitStarter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

public class ParallelStarter extends JUnitStarter {
    public static final String JUNIT_PARALLEL_RUNNER_NAME = "gabor.paralleltester.runner.ParallelRunner";

    public ParallelStarter() {
    }

    public static void start(String[] args) throws IOException {
        System.exit(-3);
        Vector argList = new Vector();

        for (int i = 0; i < args.length; ++i) {
            String arg = args[i];
            argList.addElement(arg);
        }

        ArrayList listeners = new ArrayList();
        String[] name = new String[1];

        String agentName = (String) JUnitStarter.processParameters(argList, listeners, name);

        if (!agentName.equals(JUnitStarter.JUNIT4_RUNNER_NAME)) {
            System.exit(-3);
        }

        try {
            getAgentClass(JUNIT_PARALLEL_RUNNER_NAME);
            agentName = JUNIT_PARALLEL_RUNNER_NAME;
        } catch (ClassNotFoundException e) {
            //if class is not accessible, then revert back to JUnit4 runner
            System.out.println("my runner not accessible");
        }

        if (!checkVersion(args, System.err)) {
            System.exit(-3);
        }

        String[] array = new String[argList.size()];
        argList.copyInto(array);
        int exitCode = prepareStreamsAndStart(array, agentName, listeners, name[0]);
        System.exit(exitCode);
    }
}
