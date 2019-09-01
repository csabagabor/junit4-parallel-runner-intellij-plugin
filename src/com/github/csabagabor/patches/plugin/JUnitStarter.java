package com.github.csabagabor.patches.plugin;

import com.intellij.rt.execution.junit.IdeaTestRunner;
import com.intellij.rt.execution.junit.IdeaTestRunner.Repeater;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.intellij.rt.execution.junit.JUnitForkedSplitter;
import com.intellij.rt.execution.junit.RepeatCount;
import junit.textui.ResultPrinter;
import junit.textui.TestRunner;

/**
 * This class was copied from the open-source version of the JUnit plugin
 * for the sake of simplicity(to prevent using reflection). This must not be modified.
 */
public class JUnitStarter {
    public static final int VERSION = 5;
    public static final String IDE_VERSION = "-ideVersion";
    public static final String JUNIT3_PARAMETER = "-junit3";
    public static final String JUNIT4_PARAMETER = "-junit4";
    public static final String JUNIT5_PARAMETER = "-junit5";
    public static final String JUNIT5_KEY = "idea.is.junit5";
    private static final String SOCKET = "-socket";
    public static final String JUNIT3_RUNNER_NAME = "com.intellij.junit3.JUnit3IdeaTestRunner";
    public static final String JUNIT4_RUNNER_NAME = "com.intellij.junit4.JUnit4IdeaTestRunner";
    public static final String JUNIT5_RUNNER_NAME = "com.intellij.junit5.JUnit5IdeaTestRunner";
    private static String ourForkMode;
    private static String ourCommandFileName;
    private static String ourWorkingDirs;
    protected static int ourCount = 1;
    public static String ourRepeatCount = null;

    public JUnitStarter() {
    }

    public static void main(String[] args) throws IOException {
        Vector argList = new Vector();

        for (int i = 0; i < args.length; ++i) {
            String arg = args[i];
            argList.addElement(arg);
        }

        ArrayList listeners = new ArrayList();
        String[] name = new String[1];
        String agentName = processParameters(argList, listeners, name);
        if (!"com.intellij.junit5.JUnit5IdeaTestRunner".equals(agentName) && !canWorkWithJUnitVersion(System.err, agentName)) {
            System.exit(-3);
        }

        if (!checkVersion(args, System.err)) {
            System.exit(-3);
        }

        String[] array = new String[argList.size()];
        argList.copyInto(array);
        int exitCode = prepareStreamsAndStart(array, agentName, listeners, name[0]);
        System.exit(exitCode);
    }

    protected static String processParameters(Vector args, List listeners, String[] params) {
        String agentName = isJUnit5Preferred() ? "com.intellij.junit5.JUnit5IdeaTestRunner" : "com.intellij.junit4.JUnit4IdeaTestRunner";
        Vector result = new Vector(args.size());

        int i;
        String arg;
        for (i = 0; i < args.size(); ++i) {
            arg = (String) args.get(i);
            if (!arg.startsWith("-ideVersion")) {
                if (arg.equals("-junit3")) {
                    agentName = "com.intellij.junit3.JUnit3IdeaTestRunner";
                } else if (arg.equals("-junit4")) {
                    agentName = "com.intellij.junit4.JUnit4IdeaTestRunner";
                } else if (arg.equals("-junit5")) {
                    agentName = "com.intellij.junit5.JUnit5IdeaTestRunner";
                } else if (arg.startsWith("@name")) {
                    params[0] = arg.substring("@name".length());
                } else if (arg.startsWith("@w@")) {
                    ourWorkingDirs = arg.substring(3);
                } else {
                    int port;
                    if (arg.startsWith("@@@")) {
                        port = arg.indexOf(44);
                        ourForkMode = arg.substring(3, port);
                        ourCommandFileName = arg.substring(port + 1);
                    } else if (arg.startsWith("@@")) {
                        if ((new File(arg.substring(2))).exists()) {
                            try {
                                BufferedReader reader = new BufferedReader(new FileReader(arg.substring(2)));

                                String line;
                                while ((line = reader.readLine()) != null) {
                                    listeners.add(line);
                                }
                            } catch (Exception var22) {
                                var22.printStackTrace();
                            }
                        }
                    } else if (arg.startsWith("-socket")) {
                        port = Integer.parseInt(arg.substring("-socket".length()));

                        try {
                            Socket socket = new Socket(InetAddress.getByName("127.0.0.1"), port);
                            DataInputStream os = new DataInputStream(socket.getInputStream());

                            try {
                                os.readBoolean();
                            } finally {
                                os.close();
                            }
                        } catch (IOException var21) {
                            var21.printStackTrace();
                        }
                    } else {
                        port = RepeatCount.getCount(arg);
                        if (port != 0) {
                            ourRepeatCount = arg;
                            ourCount = port;
                        } else {
                            result.addElement(arg);
                        }
                    }
                }
            }
        }

        args.removeAllElements();

        for (i = 0; i < result.size(); ++i) {
            arg = (String) result.get(i);
            args.addElement(arg);
        }

        if ("com.intellij.junit3.JUnit3IdeaTestRunner".equals(agentName)) {
            try {
                Class.forName("org.junit.runner.Computer");
                agentName = "com.intellij.junit4.JUnit4IdeaTestRunner";
            } catch (ClassNotFoundException var19) {
                return "com.intellij.junit3.JUnit3IdeaTestRunner";
            }
        }

        if ("com.intellij.junit4.JUnit4IdeaTestRunner".equals(agentName)) {
            try {
                Class.forName("org.junit.Test");
            } catch (ClassNotFoundException var18) {
                return "com.intellij.junit3.JUnit3IdeaTestRunner";
            }
        }

        try {
            String forceJUnit3 = System.getProperty("idea.force.junit3");
            if (forceJUnit3 != null && Boolean.valueOf(forceJUnit3)) {
                return "com.intellij.junit3.JUnit3IdeaTestRunner";
            }
        } catch (SecurityException var17) {
        }

        return agentName;
    }

    public static boolean isJUnit5Preferred() {
        String useJUnit5 = System.getProperty("idea.is.junit5");
        if (useJUnit5 == null) {
            return false;
        } else {
            Boolean boolValue = Boolean.valueOf(useJUnit5);
            return boolValue != null && boolValue;
        }
    }

    public static boolean checkVersion(String[] args, PrintStream printStream) {
        for (int i = 0; i < args.length; ++i) {
            String arg = args[i];
            if (arg.startsWith("-ideVersion")) {
                int ideVersion = Integer.parseInt(arg.substring("-ideVersion".length()));
                if (ideVersion != 5) {
                    printStream.println("Wrong agent version: 5. IDE expects version: " + ideVersion);
                    printStream.flush();
                    return false;
                }

                return true;
            }
        }

        return false;
    }

    protected static boolean canWorkWithJUnitVersion(PrintStream printStream, String agentName) {
        boolean var3;
        try {
            junitVersionChecks(agentName);
            return true;
        } catch (Throwable var7) {
            printStream.println("!!! JUnit version 3.8 or later expected:");
            printStream.println();
            var7.printStackTrace(printStream);
            printStream.flush();
            var3 = false;
        } finally {
            printStream.flush();
        }

        return var3;
    }

    protected static void junitVersionChecks(String agentName) throws ClassNotFoundException {
        Class.forName("junit.framework.ComparisonFailure");
        getAgentClass(agentName);
        (new TestRunner()).setPrinter((ResultPrinter) null);
    }

    protected static int prepareStreamsAndStart(String[] args, String agentName, ArrayList listeners, String name) {
        try {
            IdeaTestRunner testRunner = (IdeaTestRunner) getAgentClass(agentName).newInstance();
            if (ourCommandFileName == null || "none".equals(ourForkMode) && (ourWorkingDirs == null || (new File(ourWorkingDirs)).length() <= 0L)) {
                return Repeater.startRunnerWithArgs(testRunner, args, listeners, name, ourCount, true);
            } else {
                List newArgs = new ArrayList();
                newArgs.add(agentName);
                newArgs.addAll(listeners);
                return (new JUnitForkedSplitter(ourWorkingDirs, ourForkMode, newArgs)).startSplitting(args, name, ourCommandFileName, ourRepeatCount);
            }
        } catch (Exception var6) {
            var6.printStackTrace(System.err);
            return -2;
        }
    }

    protected static Class getAgentClass(String agentName) throws ClassNotFoundException {
        return Class.forName(agentName);
    }

    public static void printClassesList(List classNames, String packageName, String category, String filters, File tempFile) throws IOException {
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(tempFile), "UTF-8"));

        try {
            writer.println(packageName);
            writer.println(category);
            writer.println(filters);

            for (int i = 0; i < classNames.size(); ++i) {
                writer.println(classNames.get(i));
            }
        } finally {
            writer.close();
        }

    }
}
