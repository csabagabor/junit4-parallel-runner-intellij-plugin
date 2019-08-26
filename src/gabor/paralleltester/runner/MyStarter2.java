//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package gabor.paralleltester.runner;

import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.intellij.rt.execution.junit.JUnitForkedSplitter;
import com.intellij.rt.execution.junit.JUnitStarter;
import com.intellij.rt.execution.junit.RepeatCount;
import com.intellij.ui.awt.RelativePoint;
import junit.textui.ResultPrinter;
import junit.textui.TestRunner;

public class MyStarter2 extends JUnitStarter {
    public static final String JUNIT_PARALLEL_RUNNER_NAME = "com.intellij.junit5.JUnit5IdeaTestRunner";


    private static String ourForkMode;
    private static String ourCommandFileName;
    private static String ourWorkingDirs;
    protected static int ourCount = 1;
    public static String ourRepeatCount = null;

    public MyStarter2() {
    }

    public static void main(String[] args) throws IOException {
        System.exit(-3);

        Vector argList = new Vector();

        for (int i = 0; i < args.length; ++i) {
            String arg = args[i];
            argList.addElement(arg);
        }

        ArrayList listeners = new ArrayList();
        String[] name = new String[1];

        try {
            for (Method declaredMethod : JUnitStarter.class.getDeclaredMethods()) {
                System.out.println(declaredMethod);
            }
            System.out.println();

            Class[] paramStringArray = new Class[3];
            paramStringArray[0] = Vector.class;
            paramStringArray[1] = List.class;
            paramStringArray[2] = String[].class;


            Method processParameters = JUnitStarter.class.getDeclaredMethod(
                    "processParameters", paramStringArray);
            processParameters.setAccessible(true);
            System.out.println("entered");

            String agentName = (String) processParameters.invoke(null, argList, listeners, name);

            if (!agentName.equals(JUnitStarter.JUNIT4_RUNNER_NAME)) {
                shutDownPlugin();
            }

            System.out.println(agentName);

            try {
                getAgentClass(JUNIT_PARALLEL_RUNNER_NAME);
                agentName = JUNIT_PARALLEL_RUNNER_NAME;
            } catch (ClassNotFoundException e) {
                //if class is not accessible, then revert back to JUnit4 runner

            }

            if (!checkVersion(args, System.err)) {
                System.exit(-3);
            }

            String[] array = new String[argList.size()];
            argList.copyInto(array);
            int exitCode = prepareStreamsAndStart(array, agentName, listeners, name[0]);

            System.out.println("exitCode:" + exitCode);

            System.exit(exitCode);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException | InvocationTargetException e) {
            System.exit(-3);
        }
    }

    private static void shutDownPlugin() {
//        StatusBar statusBar = WindowManager.getInstance()
//                .getStatusBar(DataKeys.PROJECT.getData(actionEvent.getDataContext()));
//
//        JBPopupFactory.getInstance()
//                .createHtmlTextBalloonBuilder("blabla", MessageType.ERROR, null)
//                .setFadeoutTime(7500)
//                .createBalloon()
//                .show(RelativePoint.getCenterOf(statusBar.getComponent()),
//                        Balloon.Position.atRight);
    }

    private static boolean canWorkWithJUnitVersion(PrintStream printStream, String agentName) {
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

    private static void junitVersionChecks(String agentName) throws ClassNotFoundException {
        Class.forName("junit.framework.ComparisonFailure");
        getAgentClass(agentName);
        (new TestRunner()).setPrinter((ResultPrinter) null);
    }

    private static int prepareStreamsAndStart(String[] args, String agentName, ArrayList listeners, String name) {
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

    static Class getAgentClass(String agentName) throws ClassNotFoundException {
        return Class.forName(agentName);
    }
}
