package intellij.junit.plugin;

import java.util.ArrayList;
import java.util.List;

public interface IdeaTestRunner {
    void createListeners(ArrayList var1, int var2);

    int startRunnerWithArgs(String[] var1, String var2, int var3, boolean var4);

    Object getTestToStart(String[] var1, String var2);

    List getChildTests(Object var1);

    String getStartDescription(Object var1);

    String getTestClassName(Object var1);

    public static class Repeater {
        public Repeater() {
        }

        public static int startRunnerWithArgs(com.intellij.rt.execution.junit.IdeaTestRunner testRunner, String[] args, ArrayList listeners, String name, int count, boolean sendTree) {
            testRunner.createListeners(listeners, count);
            if (count == 1) {
                return testRunner.startRunnerWithArgs(args, name, count, sendTree);
            } else {
                boolean success;
                int result;
                if (count > 0) {
                    success = true;

                    for(result = 0; result++ < count; sendTree = false) {
                        result = testRunner.startRunnerWithArgs(args, name, count, sendTree);
                        if (result == -2) {
                            return result;
                        }

                        success &= result == 0;
                    }

                    return success ? 0 : -1;
                } else {
                    success = true;

                    do {
                        result = testRunner.startRunnerWithArgs(args, name, count, sendTree);
                        if (result == -2) {
                            return -1;
                        }

                        success &= result == 0;
                    } while(count != -2 || success);

                    return -1;
                }
            }
        }
    }
}
