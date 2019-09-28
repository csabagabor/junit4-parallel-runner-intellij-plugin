package com.github.csabagabor.patches;

import com.github.csabagabor.Resources;
import com.github.csabagabor.patches.plugin.JUnitStarter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

public class ParallelStarter extends JUnitStarter {
    public ParallelStarter() {
    }

    public static void start(String[] args) throws IOException {

        Vector argList = new Vector();

        for (int i = 0; i < args.length; ++i) {
            String arg = args[i];
            argList.addElement(arg);
        }

        ArrayList listeners = new ArrayList();
        String[] name = new String[1];

        String agentName = (String) JUnitStarter.processParameters(argList, listeners, name);

        if (!agentName.equals(JUnitStarter.JUNIT4_RUNNER_NAME)) {
            System.exit(-8);
        }

        try {
            getAgentClass(Resources.JUNIT_PARALLEL_RUNNER_NAME);
            agentName = Resources.JUNIT_PARALLEL_RUNNER_NAME;
        } catch (ClassNotFoundException e) {
            System.exit(-4);
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
